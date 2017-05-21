package ch.fhnw.edu.emoba.spheropantherapp.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.v4.content.ContextCompat;

import ch.fhnw.edu.emoba.spheropantherapp.R;

/**
 * Created by Joel on 09/05/17.
 */

public class ControllerGrid {

    private static String TAG = ControllerGrid.class.toString();

    private static int marginGrid = 80;
    private static int radiusGrid = 150;
    private static int radiusTouch = 20;


    private Context context;
    private Paint paint;
    private Point lastPosition;

    private int xC = 0;
    private int yC = 0;

    public int getCenterX() { return xC; };
    public int getCenterY() { return yC; };
    public int getRadius() { return radiusGrid; };


    public ControllerGrid(Context context) {
        this.context = context;

        paint = new Paint();
        lastPosition = new Point(0, 0);
    }

    public ControllerGrid(Context context, int width, int height) {
        this(context);

        int measureSize = width > height ? height : width;

        radiusGrid = measureSize / 3;
        marginGrid = (height - (2 * radiusGrid)) / 3;
        radiusTouch = measureSize / 20;

        xC = (int) width / 2;
        yC = radiusGrid + marginGrid;
    }


    protected void drawGrid(Canvas canvas, Point nextPosition) {

        // Draw controll cross
        drawControllCross(canvas);


        // Draw position circle
        int color = ContextCompat.getColor(context, R.color.colorAccent);
        paint.setColor(color);

        if (nextPosition != null) {

            if (nextPosition.x < radiusTouch + 10) {
                nextPosition.x = lastPosition.x;
            } else if (nextPosition.x > 2 * (radiusGrid + marginGrid)) {
                nextPosition.x = lastPosition.x;
            }

            lastPosition = nextPosition;

            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(lastPosition.x, lastPosition.y, radiusTouch, paint);

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(4);
            canvas.drawCircle(lastPosition.x, lastPosition.y, radiusTouch + 10, paint);
        } else {
            paint.setStyle(Paint.Style.FILL);

            canvas.drawCircle(xC, yC, radiusTouch - 5, paint);

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(3);
            canvas.drawCircle(xC, yC, radiusTouch, paint);
        }
    }

    private void drawControllCross(Canvas canvas) {
        // Draw Lines
        // *************
        int color = ContextCompat.getColor(context, R.color.colorLine);
        paint.setColor(color);
        paint.setStrokeWidth(4);

        // Horizontal
        int xStart = xC - radiusGrid;
        int yStart = yC;
        int xEnd = xC + radiusGrid;
        int yEnd = yC;
        canvas.drawLine(xStart, yStart, xEnd, yEnd, paint);

        // Vertical
        xStart = xC;
        yStart = yC - radiusGrid;
        xEnd = xC;
        yEnd = yC + radiusGrid;
        canvas.drawLine(xStart, yStart, xEnd, yEnd, paint);

        // Draw Circles
        // *************
        color = ContextCompat.getColor(context, R.color.colorCircle);
        paint.setColor(color);

        // Outer circle
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(6);
        canvas.drawCircle(xC, yC, radiusGrid, paint);

        // First inner circle
        paint.setStrokeWidth(2);
        canvas.drawCircle(xC, yC, radiusGrid * 2 / 3, paint);

        // Second inner circle
        paint.setStrokeWidth(2);
        canvas.drawCircle(xC, yC, radiusGrid / 3, paint);
    }
}
