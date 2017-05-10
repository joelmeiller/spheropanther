package layout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;

import ch.fhnw.edu.emoba.spheropantherapp.R;

/**
 * Created by Joel on 09/05/17.
 */

public class ControllerGrid {

    private static String TAG = ControllerGrid.class.toString();

    private static int MARGIN_GRID = 80;
    private static int RADIUS_GRID = 150;
    private static int RADIUS_TOUCH = 20;


    private Context context;
    private Paint paint;
    private Point lastPosition;

    public ControllerGrid(Context context) {
        this.context = context;

        paint = new Paint();
        lastPosition = new Point(0, 0);
    }


    protected void drawGrid(Canvas canvas, Point nextPosition) {

        int xC = (int) canvas.getWidth() / 2;
        int yC = RADIUS_GRID + MARGIN_GRID;

        // Draw controll cross
        drawControllCross(canvas, xC, yC);


        // Draw position circle
        int color = ContextCompat.getColor(context, R.color.colorAccent);
        paint.setColor(color);

        if (nextPosition != null) {

            if (nextPosition.x < RADIUS_TOUCH + 10) {
                nextPosition.x = lastPosition.x;
            } else if (nextPosition.x > 2 * (RADIUS_GRID + MARGIN_GRID)) {
                nextPosition.x = lastPosition.x;
            }

            lastPosition = nextPosition;

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
        int color = ContextCompat.getColor(context, R.color.colorLine);
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
        color = ContextCompat.getColor(context, R.color.colorCircle);
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
