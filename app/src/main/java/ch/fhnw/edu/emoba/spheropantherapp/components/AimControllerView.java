package ch.fhnw.edu.emoba.spheropantherapp.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Joel on 09/05/17.
 */

public class AimControllerView extends View {

    private static String TAG = AimControllerView.class.toString();


    private AimGrid grid;
    private Point nextPosition;

    public AimControllerView(Context context) {
        super(context);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        super.onSizeChanged(width, height, oldw, oldh);

        Log.i(TAG, "initializing aim grid with widht = " + width + " & height = " + height);

        if (width > 0 && height > 0) {
            grid = new AimGrid(getContext(), width, height);
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
                break;
            case MotionEvent.ACTION_MOVE:
                nextPosition.set(x, y);
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

}
