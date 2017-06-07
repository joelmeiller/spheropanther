package ch.fhnw.edu.emoba.spheropantherapp.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import ch.fhnw.edu.emoba.spheropantherapp.R;
import ch.fhnw.edu.emoba.spheropantherapp.robot.RobotAimControlThread;


/**
 * Created by Joel on 09/05/17.
 */

public class AimControllerView extends View {

    private static String TAG = AimControllerView.class.toString();


    private AimGrid grid;
    private Point nextPosition;

    private RobotAimControlThread robotControlThread;
    private Handler robotControlHandler;

    public AimControllerView(Context context) {
        super(context);
    }

    public void setZeroHeading() {
        robotControlThread.setZeroHeading();
    }

    public void startRobotControlThread() {
        if (robotControlThread == null) {
            robotControlThread = new RobotAimControlThread("Robot Control", grid);
            robotControlThread.start();
            Log.i(TAG, "Started Robot Control Thread");
        }
    }

    public void stopRobotControlThread() {
        if (robotControlThread != null) {
            robotControlThread.stopRobot();
            robotControlThread.quit();
            robotControlThread = null;
            Log.i(TAG, "Stopped Robot Control Thread");
        }
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        super.onSizeChanged(width, height, oldw, oldh);

        Log.i(TAG, "Initializing robot control with width = " + width + " & height = " + height);

        if (width > 0 && height > 0) {
            grid = new AimGrid(getContext(), width, height);
            robotControlThread.setGrid(grid);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                nextPosition = new Point(x, y);
                moveRobot(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                if (nextPosition != null) {
                    nextPosition.set(x, y);
                    moveRobot(x, y);
                }
                break;
            case MotionEvent.ACTION_UP:
                nextPosition = null;
                moveRobot(0, 0);
            default:
                // Do nothing
        }

        postInvalidate();

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (grid != null) {
            grid.drawGrid(canvas, nextPosition);
        }
    }

    private void moveRobot(int x, int y) {
        if (robotControlHandler == null && robotControlThread != null) {
            robotControlHandler = robotControlThread.getRobotControlThreadHandler();
        }

        if (robotControlHandler != null && robotControlThread != null) {
            Message msg = robotControlHandler.obtainMessage();
            msg.what = RobotAimControlThread.POSITION_CHANGED;

            Bundle content = new Bundle();
            content.putInt(RobotAimControlThread.POS_X, x);
            content.putInt(RobotAimControlThread.POS_Y, y);

            msg.setData(content);
            msg.sendToTarget();
        }
    }
}
