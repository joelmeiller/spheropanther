package ch.fhnw.edu.emoba.spheropantherapp.robot;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import ch.fhnw.edu.emoba.spherolib.SpheroRobotFactory;
import ch.fhnw.edu.emoba.spherolib.SpheroRobotProxy;
import ch.fhnw.edu.emoba.spheropantherapp.components.ControllerGrid;

/**
 * Created by Joel on 21/05/17.
 */

public class RobotTouchControlThread extends HandlerThread {

    public static final String TAG = RobotTouchControlThread.class.toString();

    public static final int POSITION_CHANGED = 1;

    public static final String POS_X = "position X";
    public static final String POS_Y = "position Y";
    public static final double FULL_CIRCLE = Math.PI * 2;

    private ScheduledFuture workerTask;
    private Handler robotControlHandler;

    private SpheroRobotProxy proxy;

    private ControllerGrid grid;

    private AtomicInteger x;
    private AtomicInteger y;

    public RobotTouchControlThread(String name, ControllerGrid grid) {
        super(name);

        this.grid = grid;

        proxy = SpheroRobotFactory.getActualRobotProxy();
        x = new AtomicInteger(0);
        y = new AtomicInteger(0);
    }

    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();

        robotControlHandler = new Handler(getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == POSITION_CHANGED) {
                    x.set(msg.getData().getInt(POS_X));
                    y.set(msg.getData().getInt(POS_Y));
                }
            }
        };

        Thread thread = new RobotDriverThread();
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        // Run thread each 10 milisecons
        workerTask = scheduler.scheduleAtFixedRate(thread, 0, 20, TimeUnit.MILLISECONDS);
    }

    class RobotDriverThread extends Thread {
        @Override
        public void run() {
            if (grid != null) {
                double velocity = 0f;
                double direction = 0f;

                // Calculate velocity
                Double dX = new Double(grid.getCenterX() - x.get());
                Double dY = new Double(grid.getCenterY() - y.get());

                double radius = Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2));
                velocity = (radius > grid.getRadius() ? grid.getRadius() : radius) / grid.getRadius();

                // Calculate direction
                // Value between 0 and + 360 degrees (clockwise)
                direction = Math.acos(dY / radius);
                direction = dX > 0 ? FULL_CIRCLE - direction : direction;
                int directionDegree = (int) Math.round(direction / Math.PI * 180);

                proxy.drive(directionDegree, (float) velocity);
            }
        }
    }

    public Handler getRobotControlThreadHandler() {
        return robotControlHandler;
    }

    public void stopRobot() {
        Log.i(TAG, "Stop touch control robot");
        if (workerTask != null) {
            workerTask.cancel(true);
        }
    }
}
