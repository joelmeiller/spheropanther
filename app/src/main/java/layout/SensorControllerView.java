package layout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
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

    public SensorControllerView(Context context) {
        super(context);

        grid = new ControllerGrid(context);

        // Created scheduler for view thread
        scheduler = Executors.newSingleThreadScheduledExecutor();
        viewThread = new ViewThread();

        // Create sensor thread to track values
        sensorThread = new SensorThread();

        // Create Sensor Service
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(sensorThread, mSensor, 1000);

        values = new AtomicIntegerArray(3);
    }

    public void start() {
        // Start Sensor-Task after a delay of 50ms with an interval of 50ms
        Log.i(TAG, "Starting Spheropanther");
        viewTask = scheduler.scheduleAtFixedRate(viewThread, 50, 50, TimeUnit.MILLISECONDS);

    }
    public void stop() {
        // Stop View-Task
        Log.i(TAG, "Stoping Spheropanther");
        viewTask.cancel(true);
    }

    class ViewThread extends Thread {
        @Override
        public void run() {
            // Calculate new position
            Log.i(TAG, "Calculating movement...");
            Log.i(TAG, String.valueOf(values.get(0)));
            postInvalidate();
        }
    }

    class SensorThread extends Thread implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {
            for (int i=0; i<event.values.length; i++) {
                values.set(i, (int)(event.values[i] * 1000000));
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Do nothing
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        grid.drawGrid(canvas, nextPosition);
    }

}
