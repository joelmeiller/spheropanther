package ch.fhnw.edu.emoba.spheropantherapp.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;

import ch.fhnw.edu.emoba.spheropantherapp.robot.RobotSensorControlThread;

/**
 * Created by Joel on 09/05/17.
 */

public class SensorControllerView extends View implements SensorEventListener {

    private static String TAG = SensorControllerView.class.toString();

    private ControllerGrid grid;

    // Sensor Thread
    private SensorManager mSensorManager;
    private Sensor mSensorACC;
    private Sensor mSensorMAG;

    private RobotSensorControlThread robotControlThread;
    private Handler robotControlHandler;
    private Handler mainHandler;

    private Point position;
    private static int SMOOTHNESS = 1;
    private float[] pitches;
    private float[] rolls;

    float[] mGravity = new float[3];
    float[] mGeomagnetic = new float[3];

    public SensorControllerView(Context context) {
        super(context);

        grid = new ControllerGrid(context);

        // Create Sensor Service
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mSensorACC = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorMAG = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        // Create Thread and Handlers
        mainHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case RobotSensorControlThread.HANDLER_READY:
                        Log.i(TAG, "Sensor Thread Handler attached");
                        robotControlHandler = robotControlThread.getRobotControlThreadHandler();
                        break;

                    case RobotSensorControlThread.POSITION_CHANGED:
                        Bundle data = msg.getData();
                        position = new Point(data.getInt(RobotSensorControlThread.POS_X), data.getInt(RobotSensorControlThread.POS_Y));
                        postInvalidate();
                        break;

                    default:
                        super.handleMessage(msg);

                }
            }
        };

        pitches = new float[SMOOTHNESS];
        rolls = new float[SMOOTHNESS];
    }

    public void startRobotControlThread() {
        if (robotControlThread == null) {
            robotControlThread = new RobotSensorControlThread("Robot Sensor Control", mainHandler, grid);
            robotControlThread.start();
            mSensorManager.registerListener(this, mSensorACC, SensorManager.SENSOR_DELAY_NORMAL);
            mSensorManager.registerListener(this, mSensorMAG, SensorManager.SENSOR_DELAY_NORMAL);
            Log.i(TAG, "Started Robot Control Thread");
        }
    }

    public void stopRobotControlThread() {
        if (robotControlThread != null) {
            robotControlThread.stopRobot();
            robotControlThread.quit();
            robotControlThread = null;
            mSensorManager.unregisterListener(this, mSensorACC);
            mSensorManager.unregisterListener(this, mSensorMAG);
            Log.i(TAG, "Stopped Robot Control Thread");
        }
    }

    // From source: https://gist.github.com/abdelhady/501f6e48c1f3e32b253a#file-deviceorientation
    @Override
    public void onSensorChanged(SensorEvent event) {
         Log.d(TAG, "Value: " + event.values[0]);
         if (robotControlHandler != null) {
             // It is good practice to check that we received the proper sensor event

             if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
                 mGravity = event.values;
             if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
                 mGeomagnetic = event.values;

             if (mGravity != null && mGeomagnetic != null) {
                 float R[] = new float[9];
                 float I[] = new float[9];
                 boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
                 if (success) {
                     float orientationData[] = new float[3];
                     SensorManager.getOrientation(R, orientationData);

                     Log.d(TAG, "Pitch: " + orientationData[1] + " / Roll: " + orientationData[2]);

                     float averagePitch = addValue(orientationData[1], pitches);
                     float averageRoll = addValue(orientationData[2], rolls);

                     Log.d(TAG, "Average Pitch: " + orientationData[1] + " / Roll: " + orientationData[2]);

                     Message msg = robotControlHandler.obtainMessage();
                     msg.what = RobotSensorControlThread.SENSOR_CHANGED;

                     Bundle content = new Bundle();
                     content.putFloat(RobotSensorControlThread.PITCH, averagePitch);
                     content.putFloat(RobotSensorControlThread.ROLL, averageRoll);

                     msg.setData(content);
                     msg.sendToTarget();
                 }
             }
        }
    }

    private float addValue(float value, float[] values) {
        value = (float) Math.round((Math.toDegrees(value)));
        float average = 0;
        for (int i = 1; i < SMOOTHNESS; i++) {
            values[i - 1] = values[i];
            average += values[i];
        }
        values[SMOOTHNESS - 1] = value;
        average = (average + value) / SMOOTHNESS;
        return average;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

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
            grid.drawGrid(canvas, position);
        }
    }
}
