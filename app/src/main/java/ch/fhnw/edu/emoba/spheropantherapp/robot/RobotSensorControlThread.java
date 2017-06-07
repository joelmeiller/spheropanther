package ch.fhnw.edu.emoba.spheropantherapp.robot;

import android.hardware.SensorManager;
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

    private ControllerGrid grid;

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

    public RobotSensorControlThread(String name, Handler mainHandler, ControllerGrid grid) {
        super(name);

        this.mainHandler = mainHandler;
        this.grid = grid;

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
        workerTask = scheduler.scheduleAtFixedRate(thread, 0, 20, TimeUnit.MILLISECONDS);
    }

    class RobotDriverThread extends Thread {
        private static final float NS2S = 1.0f / 1000000000.0f;
        private static final float EPSILON = 0.05f;

        private final float[] deltaRotationVector = new float[4];

        private float lastTimestamp;

        @Override
        public void run() {
            if (grid != null) {
                // TODO
            }
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
