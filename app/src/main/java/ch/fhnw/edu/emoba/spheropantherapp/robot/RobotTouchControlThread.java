package ch.fhnw.edu.emoba.spheropantherapp.robot;

import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import ch.fhnw.edu.emoba.spherolib.SpheroRobotFactory;
import ch.fhnw.edu.emoba.spherolib.SpheroRobotProxy;
import ch.fhnw.edu.emoba.spheropantherapp.layout.ControllerGrid;

/**
 * Created by Joel on 21/05/17.
 */

public class RobotTouchControlThread extends HandlerThread {

    public static final String TAG = RobotTouchControlThread.class.toString();

    public static final int POSITION_CHANGED = 1;

    public static final String POS_X = "position X";
    public static final String POS_Y = "position Y";
    public static final double FULL_CIRCLE = Math.PI * 2;

    private Handler robotControlHandler;

    private SpheroRobotProxy proxy;

    private ControllerGrid grid;

    public RobotTouchControlThread(String name, ControllerGrid grid ) {
        super(name);

        this.grid = grid;

        Boolean onEmulator = Build.PRODUCT.startsWith("sdk");
        proxy = SpheroRobotFactory.createRobot(onEmulator);
    }

    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();

        robotControlHandler = new Handler(getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                int x = msg.getData().getInt(POS_X);
                int y = msg.getData().getInt(POS_Y);

                if (msg.what == POSITION_CHANGED) {
                    moveRobot(x, y);
                }
            }
        };
    }

    private void moveRobot(int x, int y) {
        double velocity = 0f;
        double direction = 0f;

        // Calculate velocity
        Integer dX = new Integer(grid.getCenterX() - x);
        Integer dY = new Integer(grid.getCenterY() - y);

        double radius = Math.sqrt(Math.pow(dX.doubleValue(), 2) + Math.pow(dY.doubleValue(), 2));
        velocity = (radius > grid.getRadius() ? grid.getRadius() : radius) / grid.getRadius();

        // Calculate direction
        // Value between 0 and + 360 degrees (clockwise)
        direction = Math.acos(dY.doubleValue() / radius);
        direction = dX > 0 ? FULL_CIRCLE - direction : direction;
        int directionDegree = (int) Math.round(direction / Math.PI * 180);

        proxy.drive(directionDegree, (float) velocity);
    }

    public Handler getRobotControlThreadHandler() {
        return robotControlHandler;
    }
}
