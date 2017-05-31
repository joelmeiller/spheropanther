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

import ch.fhnw.edu.emoba.spheropantherapp.robot.RobotTouchControlThread;

/**
 * Created by Joel on 09/05/17.
 */

public class TouchControllerView extends View {

    private static String TAG = TouchControllerView.class.toString();


    private ControllerGrid grid;
    private Point nextPosition;

    private RobotTouchControlThread robotControlThread;
    private Handler robotControlHandler;

    public TouchControllerView(Context context) {
        super(context);

        robotControlThread = new RobotTouchControlThread("Robot Control");
        nextPosition = new Point(0, 0);
    }


    public void startRobotControlThread() {
        robotControlThread.start();
        Log.i(TAG, "Started Robot Control Thread");
    }

    public void stopRobotControlThread() {
        robotControlThread.quit();
        Log.i(TAG, "Stopped Robot Control Thread");
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        super.onSizeChanged(width, height, oldw, oldh);

        Log.i(TAG, "Initializing robot control with width = " + width + " & height = " + height);

        if (width > 0 && height > 0) {
            grid = new ControllerGrid(getContext(), width, height);
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
                nextPosition.set(x, y);
                moveRobot(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                nextPosition.set(x, y);
                moveRobot(x, y);
                break;
            case MotionEvent.ACTION_UP:
                nextPosition.set(0, 0);
                break;
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
        if (robotControlHandler == null) {
            robotControlHandler = robotControlThread.getRobotControlThreadHandler();
        }

        if (robotControlHandler != null) {
            Message msg = robotControlHandler.obtainMessage();
            msg.what = RobotTouchControlThread.POSITION_CHANGED;

            Bundle content = new Bundle();
            content.putInt(RobotTouchControlThread.POS_X, x);
            content.putInt(RobotTouchControlThread.POS_Y, y);

            msg.setData(content);
            msg.sendToTarget();
        }
    }
}
