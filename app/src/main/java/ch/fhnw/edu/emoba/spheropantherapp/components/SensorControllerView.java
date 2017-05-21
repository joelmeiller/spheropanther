package ch.fhnw.edu.emoba.spheropantherapp.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.View;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * Created by Joel on 09/05/17.
 */

public class SensorControllerView extends View {

    private static String TAG = SensorControllerView.class.toString();

    private ControllerGrid grid;
    private Point nextPosition;

    // View Thread (each 50ms)
    private ScheduledExecutorService scheduler;
    private ScheduledFuture viewTask;
    private ViewThread viewThread;

    // Sensor Thread
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private SensorThread sensorThread;

    private AtomicIntegerArray values;
    private AtomicBoolean isRunning;

    public SensorControllerView(Context context) {
        super(context);

        grid = new ControllerGrid(context);

        // Created scheduler for view thread
        scheduler = Executors.newSingleThreadScheduledExecutor();
        viewThread = new ViewThread();

        // Create Sensor Service
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        values = new AtomicIntegerArray(3);
        isRunning = new AtomicBoolean(false);
    }

    public void start() {
        // Start Sensor-Task after a delay of 50ms with an interval of 50ms
        Log.i(TAG, "Starting Sensor Control ...");

        viewTask = scheduler.scheduleAtFixedRate(viewThread, 50, 50, TimeUnit.MILLISECONDS);

        // Start Sensor Task and attach SensorListener
        if (sensorThread == null) {
            sensorThread = new SensorThread();
            sensorThread.start();
            mSensorManager.registerListener(sensorThread, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        isRunning.set(true);
    }
    public void stop() {
        // Stop Tasks
        Log.i(TAG, "Stopping Sensor Control ...");

        isRunning.set(false);

        if (sensorThread != null) {
            mSensorManager.unregisterListener(sensorThread);
            sensorThread = null;
        }
        if (viewTask != null) {
            viewTask.cancel(true);
        }
    }

    class ViewThread extends Thread {
        private String VIEW_TASK_TAG = ViewThread.class.toString();

        @Override
        public void run() {
            // Calculate new position
            Log.i(VIEW_TASK_TAG, "Redraw movement...");
            Log.i(VIEW_TASK_TAG, String.valueOf(values.get(0)));
            postInvalidate();
        }
    }

    class SensorThread extends Thread implements SensorEventListener {

        private String SENSOR_TASK_TAG = SensorThread.class.toString();

        private static final float NS2S = 1.0f / 1000000000.0f;
        private static final float EPSILON = 0.05f;

        private final float[] deltaRotationVector = new float[4];

        private float[] rotations;
        private float lastTimestamp;
        private float nextTimestamp;

        SensorThread() {
            lastTimestamp = 0;
        }

        @Override
        public void run() {
            while (isRunning.get()) {
                // Calculate rotation matrix
                // This timestep's delta rotation to be multiplied by the current rotation
                // after computing it from the gyro sample data.
                if (lastTimestamp != 0) {
                    final float dT = (nextTimestamp - lastTimestamp) * NS2S;
                    // Axis of the rotation sample, not normalized yet.
                    float axisX = rotations[0];
                    float axisY = rotations[1];
                    float axisZ = rotations[2];

                    // Calculate the angular speed of the sample
                    float omegaMagnitude = (float) Math.sqrt(axisX*axisX + axisY*axisY + axisZ*axisZ);

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

                lastTimestamp = nextTimestamp;

                float[] deltaRotationMatrix = new float[9];
                SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector);
                // User code should concatenate the delta rotation we computed with the current rotation
                // in order to get the updated rotation.
                // rotationCurrent = rotationCurrent * deltaRotationMatrix;

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Log.e(SENSOR_TASK_TAG, e.toString());
                }
            }
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            rotations = event.values;
            nextTimestamp = event.timestamp;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Do nothing
        }
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        super.onSizeChanged(width, height, oldw, oldh);

        Log.i(TAG, "initializing controller grid with widht = " + width + " & height = " + height);

        if (width > 0 && height > 0) {
            grid = new ControllerGrid(getContext(), width, height);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (grid != null) {
            grid.drawGrid(canvas, nextPosition);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (isRunning.get()) {
            stop();
        }
    }
}
