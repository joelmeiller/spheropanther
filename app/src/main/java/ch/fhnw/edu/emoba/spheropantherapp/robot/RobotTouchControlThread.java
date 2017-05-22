package ch.fhnw.edu.emoba.spheropantherapp.robot;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

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

    private Handler robotControlHandler;

    private SpheroRobotProxy proxy;

    private ControllerGrid grid;

    public RobotTouchControlThread(String name) {
        super(name);

        proxy = SpheroRobotFactory.getActualRobotProxy();
    }

    public void setGrid(ControllerGrid grid) {
        this.grid = grid;
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
        if (grid != null) {
            double velocity = 0f;
            double direction = 0f;

            // Calculate velocity
            Double dX = new Double(grid.getCenterX() - x);
            Double dY = new Double(grid.getCenterY() - y);

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

    public Handler getRobotControlThreadHandler() {
        return robotControlHandler;
    }
}
