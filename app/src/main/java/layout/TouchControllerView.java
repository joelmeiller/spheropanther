package layout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import ch.fhnw.edu.emoba.spheropantherapp.R;

/**
 * Created by Joel on 09/05/17.
 */

public class TouchControllerView extends View {

    private static String TAG = TouchControllerView.class.toString();


    private ControllerGrid grid;
    private Point nextPosition;

    private RobotControlThread robotControlThread;
    private Handler robotControlHandler;

    public TouchControllerView(Context context) {
        super(context);
    }

    public void stopRobotControlThread() {
        if (robotControlThread != null) {
            robotControlThread.quit();
            robotControlThread = null;
            Log.d(TAG, "Stopped Robot Control Thread");
        }
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        super.onSizeChanged(width, height, oldw, oldh);

        Log.i(TAG, "Initializing robot control with width = " + width + " & height = " + height);

        if (width > 0 && height > 0) {
            grid = new ControllerGrid(getContext(), width, height);

            robotControlThread = new RobotControlThread("Robot Control", grid.getCenterX(), grid.getCenterY());
            robotControlThread.start();
            Log.i(TAG, "Started Robot Control Thread");
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
                nextPosition.set(x, y);
                moveRobot(x, y);
                break;
            default:
                nextPosition = null;
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
            msg.what = RobotControlThread.POSITION_CHANGED;

            Bundle content = new Bundle();
            content.putInt(RobotControlThread.POS_X, x);
            content.putInt(RobotControlThread.POS_Y, y);

            msg.setData(content);

            robotControlHandler.dispatchMessage(msg);
        }
    }
}
