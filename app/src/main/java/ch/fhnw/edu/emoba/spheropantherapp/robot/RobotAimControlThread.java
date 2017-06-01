package ch.fhnw.edu.emoba.spheropantherapp.robot;

import android.os.Build;
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
import ch.fhnw.edu.emoba.spheropantherapp.components.AimGrid;
import ch.fhnw.edu.emoba.spheropantherapp.components.ControllerGrid;

/**
 * Created by Joel on 21/05/17.
 */

public class RobotAimControlThread extends HandlerThread {

    public static final String TAG = RobotAimControlThread.class.toString();

    public static final int POSITION_CHANGED = 1;

    private static final double SPEED = 0.1;

    public static final String POS_X = "position X";
    public static final String POS_Y = "position Y";
    public static final double FULL_CIRCLE = Math.PI * 2;

    private float direction;

    private ScheduledFuture workerTask;
    private Handler robotControlHandler;


    private SpheroRobotProxy proxy;

    private AimGrid grid;

    private AtomicInteger x;
    private AtomicInteger y;


    public RobotAimControlThread(String name, AimGrid grid ) {
        super(name);

        this.grid = grid;
        this.direction = 0.f;

        proxy = SpheroRobotFactory.getActualRobotProxy();
        proxy.setBackLedBrightness(1.f);

        x = new AtomicInteger(0);
    }

    public void setGrid(AimGrid grid) {
        this.grid = grid;
    }

    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();

        robotControlHandler = new Handler(getLooper()) {
            @Override
            public void handleMessage(Message msg) {

                if (msg.what == POSITION_CHANGED) {
                    x.set(msg.getData().getInt(POS_X));
                }
            }
        };

        Thread thread = new RobotAimControlThread.RobotDriverThread();
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        // Run thread each 10 milisecons
        workerTask = scheduler.scheduleAtFixedRate(thread, 0, 20, TimeUnit.MILLISECONDS);
    }

    class RobotDriverThread extends Thread {
        @Override
        public void run() {
            if (grid != null) {
                double directionDelta = 0.0;


                // Calculate direction change
                Double dX = new Double(x.get() - grid.getCenterX());

                // Value between - and + 90 degrees (clockwise)
                directionDelta = Math.asin(dX / grid.getRadius()) * SPEED;
                direction += (float) Math.round(directionDelta / Math.PI * 180);
                direction = (direction % 360 + 360) % 360;

                proxy.drive(direction, 0.f);
            }
        }
    }

    public void setZeroHeading() {
        Log.d(TAG, "Set Zero Heading: " + direction);
        proxy.setZeroHeading();
    }

    public void stopRobot() {
        Log.i(TAG, "Stop aiming robot");
        if (workerTask != null) {
            workerTask.cancel(true);
        }
        proxy.setBackLedBrightness(0.f);
    }

    public Handler getRobotControlThreadHandler() {
        return robotControlHandler;
    }

}
