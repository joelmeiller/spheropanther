package layout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import ch.fhnw.edu.emoba.spheropantherapp.R;

/**
 * Created by Joel on 09/05/17.
 */

public class ControllerView extends View {

    private static String TAG = ControllerView.class.toString();

    private static int MARGIN_GRID = 80;
    private static int RADIUS_GRID = 150;
    private static int RADIUS_TOUCH = 20;

    private Paint paint;
    private Point lastPosition;

    public ControllerView(Context context) {
        super(context);

        // create the Paint and set its color
        paint = new Paint();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        int x = (int) event.getX();
        int y = (int) event.getY();

        if (lastPosition != null && (y < RADIUS_TOUCH + 10 || y > 2 * (RADIUS_GRID + MARGIN_GRID))) {
            x = lastPosition.x;
            y = lastPosition.y;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastPosition = new Point(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                lastPosition.set(x, y);
                break;
            default:
                lastPosition = null;
        }

        postInvalidate();

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int xC = (int) canvas.getWidth() / 2;
        int yC = RADIUS_GRID + MARGIN_GRID;

        // Draw controll cross
        drawControllCross(canvas, xC, yC);


        // Draw touch circle
        int color = ContextCompat.getColor(getContext(), R.color.colorAccent);
        paint.setColor(color);

        if (lastPosition != null) {
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(lastPosition.x, lastPosition.y, RADIUS_TOUCH, paint);

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(4);
            canvas.drawCircle(lastPosition.x, lastPosition.y, RADIUS_TOUCH + 10, paint);
        } else {
            paint.setStyle(Paint.Style.FILL);

            canvas.drawCircle(xC, yC, RADIUS_TOUCH - 5, paint);

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(3);
            canvas.drawCircle(xC, yC, RADIUS_TOUCH, paint);
        }
    }

    private void drawControllCross(Canvas canvas, int xC, int yC) {
        // Draw Lines
        // *************
        int color = ContextCompat.getColor(getContext(), R.color.colorLine);
        paint.setColor(color);
        paint.setStrokeWidth(4);

        // Horizontal
        int xStart = xC - RADIUS_GRID;
        int yStart = yC;
        int xEnd = xC + RADIUS_GRID;
        int yEnd = yC;
        canvas.drawLine(xStart, yStart, xEnd, yEnd, paint);

        // Vertical
        xStart = xC;
        yStart = yC - RADIUS_GRID;
        xEnd = xC;
        yEnd = yC + RADIUS_GRID;
        canvas.drawLine(xStart, yStart, xEnd, yEnd, paint);

        // Draw Circles
        // *************
        color = ContextCompat.getColor(getContext(), R.color.colorCircle);
        paint.setColor(color);

        // Outer circle
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(6);
        canvas.drawCircle(xC, yC, RADIUS_GRID, paint);

        // First inner circle
        paint.setStrokeWidth(2);
        canvas.drawCircle(xC, yC, RADIUS_GRID * 2 / 3, paint);

        // Second inner circle
        paint.setStrokeWidth(2);
        canvas.drawCircle(xC, yC, RADIUS_GRID / 3, paint);
    }
}
