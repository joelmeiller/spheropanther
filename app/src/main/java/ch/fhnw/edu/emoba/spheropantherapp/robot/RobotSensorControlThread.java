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

    public static final String VALUES = "values";
    public static final String TIMESTAMP = "timestamp";
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
        private float[] values = new float[] {0.0f, 0.0f, 0.0f};
        private float timestamp = 0.f;

        public float[] getValues() {
            return values;
        }

        public float getTimestamp() {
            return timestamp;
        }

        public void setValues(float[] values) {
            this.values = values;
        }

        public void setTimestamp(float timestamp) {
            this.timestamp = timestamp;
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
                    sensorValues.setValues(msg.getData().getFloatArray(VALUES));
                    sensorValues.setTimestamp(msg.getData().getFloat(TIMESTAMP));
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
                // Calculate rotation matrix
                // This timestep's delta rotation to be multiplied by the current rotation
                // after computing it from the gyro sample data.
                if (lastTimestamp != 0) {
                    final float dT = (sensorValues.getTimestamp() - lastTimestamp) * NS2S;
                    // Axis of the rotation sample, not normalized yet.
                    float axisX = sensorValues.getValues()[0];
                    float axisY = sensorValues.getValues()[1];
                    float axisZ = sensorValues.getValues()[2];

                    // Calculate the angular speed of the sample
                    float omegaMagnitude = (float) Math.sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ);

                    // Normalize the rotation vector if it's big enough to get the axis
                    // (that is, EPSILON should represent your maximum allowable margin of error)
                    if (omegaMagnitude > EPSILON) {
                        axisX /= omegaMagnitude;
                        axisY /= omegaMagnitude;
                        axisZ /= omegaMagnitude;
                    }

                    // Integrate around this axis with the angular speed by the timestep
                    // in order to get a delta rotation from this sample over the timestep
                    // We will convert this axis-angle representation of the delta rotation
                    // into a quaternion before turning it into the rotation matrix.
                    float thetaOverTwo = omegaMagnitude * dT / 2.0f;
                    float sinThetaOverTwo = (float) Math.sin(thetaOverTwo);
                    float cosThetaOverTwo = (float) Math.cos(thetaOverTwo);

                    deltaRotationVector[0] = sinThetaOverTwo * axisX;
                    deltaRotationVector[1] = sinThetaOverTwo * axisY;
                    deltaRotationVector[2] = sinThetaOverTwo * axisZ;
                    deltaRotationVector[3] = cosThetaOverTwo;
                }

                lastTimestamp = sensorValues.getTimestamp();

                float[] deltaRotationMatrix = new float[9];
                SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector);
                // User code should concatenate the delta rotation we computed with the current rotation
                // in order to get the updated rotation.
                // rotationCurrent = rotationCurrent * deltaRotationMatrix;

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Log.e(TAG, e.toString());
                }

                Log.d(TAG, "Direction: ");
                // proxy.drive(directionDegree, (float) velocity);
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
