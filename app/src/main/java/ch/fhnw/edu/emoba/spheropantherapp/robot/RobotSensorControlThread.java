package ch.fhnw.edu.emoba.spheropantherapp.robot;

import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.Serializable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;

import ch.fhnw.edu.emoba.spherolib.SpheroRobotFactory;
import ch.fhnw.edu.emoba.spherolib.SpheroRobotProxy;
import ch.fhnw.edu.emoba.spheropantherapp.components.ControllerGrid;

/**
 * Created by Joel on 21/05/17.
 */

public class RobotSensorControlThread extends HandlerThread {

    public static final String TAG = RobotSensorControlThread.class.toString();


    public static final int HANDLER_READY = 1;
    public static final int SENSOR_CHANGED = 2;
    public static final int POSITION_CHANGED = 3;

    public static final String PITCH = "pitch";
    public static final String ROLL = "roll";
    public static final String POS_X = "position X";
    public static final String POS_Y = "position Y";

    public static final double FULL_CIRCLE = Math.PI * 2;

    private ScheduledFuture workerTask;
    private Handler mainHandler;
    private Handler controlHandler;

    private SpheroRobotProxy proxy;

    private SensorValues sensorValues;

    class SensorValues implements Serializable {
        private float pitch  = 0.f;
        private float roll = 0.f;

        public float getPitch() {
            return pitch;
        }

        public float getRoll() {
            return roll;
        }

        public void setPitch(float pitch) {
            this.pitch = pitch;
        }

        public void setRoll(float roll) {
            this.roll = roll;
        }
    }

    public RobotSensorControlThread(String name, Handler mainHandler) {
        super(name);

        this.mainHandler = mainHandler;

        proxy = SpheroRobotFactory.getActualRobotProxy();

        sensorValues = new SensorValues();
    }

    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();

        controlHandler = new Handler(getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == SENSOR_CHANGED) {
                    sensorValues.setPitch(msg.getData().getFloat(PITCH));
                    sensorValues.setRoll(msg.getData().getFloat(ROLL));
                }
            }
        };

        Message msg = mainHandler.obtainMessage();
        msg.what = HANDLER_READY;
        msg.sendToTarget();

        Thread thread = new RobotDriverThread();
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        // Run thread each 10 milisecons
        workerTask = scheduler.scheduleAtFixedRate(thread, 0, 50, TimeUnit.MILLISECONDS);
    }

    class RobotDriverThread extends Thread {

        @Override
        public void run() {

            double dX = 0.;
            double dY = 0.;

            if (sensorValues.getRoll() == 0 && sensorValues.getRoll() == 0) {
                proxy.drive(0, 0);
            } else {
                double velocity = 0f;
                double direction = 0f;

                // Calculate velocity
                dX = sensorValues.getRoll();
                dY = sensorValues.getPitch();

                double radius = Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2));
                velocity = radius > 1.0 ? 1.0 : radius;

                // Calculate direction
                // Value between 0 and + 360 degrees (clockwise)
                direction = Math.acos(dY);
                direction = dX > 0 ? FULL_CIRCLE - direction : direction;
                int directionDegree = 360 - (int) Math.round(direction / Math.PI * 180);

                proxy.drive(directionDegree, (float) velocity);
            }

            Message msg = mainHandler.obtainMessage();
            msg.what = RobotSensorControlThread.POSITION_CHANGED;

            Bundle content = new Bundle();
            content.putDouble(POS_X, dX);
            content.putDouble(POS_Y, dY);

            msg.setData(content);
            msg.sendToTarget();
        }

    }

    public Handler getRobotControlThreadHandler() {
        return controlHandler;
    }

    public void stopRobot() {
        Log.i(TAG, "Stop sensor control robot");
        if (workerTask != null) {
            workerTask.cancel(true);
        }
    }
}
