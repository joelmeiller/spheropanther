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
    private Sensor mSensor;

    private RobotSensorControlThread robotControlThread;
    private Handler robotControlHandler;
    private Handler mainHandler;

    private Point position;


    public SensorControllerView(Context context) {
        super(context);

        grid = new ControllerGrid(context);

        // Create Sensor Service
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        // Create Thread and Handlers
        mainHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case RobotSensorControlThread.HANDLER_READY:
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
    }

    public void startRobotControlThread() {
        if (robotControlThread == null) {
            robotControlThread = new RobotSensorControlThread("Robot Sensor Control", mainHandler, grid);
            robotControlThread.start();
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
            Log.i(TAG, "Started Robot Control Thread");
        }
    }

    public void stopRobotControlThread() {
        if (robotControlThread != null) {
            robotControlThread.stopRobot();
            robotControlThread.quit();
            robotControlThread = null;
            mSensorManager.unregisterListener(this);
            Log.i(TAG, "Stopped Robot Control Thread");
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
         if (robotControlHandler != null) {
            Message msg = robotControlHandler.obtainMessage();
            msg.what = RobotSensorControlThread.SENSOR_CHANGED;

            Bundle content = new Bundle();
            content.putFloatArray(RobotSensorControlThread.VALUES, event.values);
            content.putFloat(RobotSensorControlThread.TIMESTAMP, event.timestamp);

            msg.setData(content);
            msg.sendToTarget();
        }
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
