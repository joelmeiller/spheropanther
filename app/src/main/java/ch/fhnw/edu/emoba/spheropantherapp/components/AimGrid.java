package ch.fhnw.edu.emoba.spheropantherapp.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import ch.fhnw.edu.emoba.spheropantherapp.R;

/**
 * Created by Joel on 09/05/17.
 */

public class AimGrid {

    private static String TAG = AimGrid.class.toString();

    private static int marginGrid = 80;
    private static int radiusGrid = 150;
    private static int radiusTouch = 10;
    private static int dartLength = 5;


    private Context context;
    private Paint paint;

    private int xC = 0;
    private int yC = 0;

    public int getCenterX() { return xC; };
    public int getCenterY() { return yC; };
    public int getRadius() { return radiusGrid; };


    public AimGrid(Context context) {
        this.context = context;

        paint = new Paint();
    }

    public AimGrid(Context context, int width, int height) {
        this(context);

        int measureSize = width > height ? height : width;

        radiusGrid = measureSize / 3;
        marginGrid = (height - (2 * radiusGrid)) / 3;
        radiusTouch = measureSize / 20;
        dartLength = measureSize / 20;

        xC = (int) width / 2;
        yC = radiusGrid + marginGrid;
    }


    protected void drawGrid(Canvas canvas, Point nextPosition) {

        // Draw controll cross
        drawAimDarts(canvas);

        // Draw position circle
        int color = ContextCompat.getColor(context, R.color.colorAccent);
        paint.setColor(color);

        if (nextPosition != null) {

            // Calculate velocity
            Double dX = new Double(xC - nextPosition.x);
            Double dY = new Double(yC - nextPosition.y);

            double radius = Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2));
            boolean inRadius = Math.abs(radiusGrid - radius) < radiusTouch;
            boolean inXRange = nextPosition.x >= (xC - radiusGrid) && nextPosition.x <= (xC + radiusGrid);
            boolean inYRange = nextPosition.y <= yC;

            // Redraw touch position only if close to arc
            if (inXRange && inYRange && inRadius) {

                Log.d(TAG, "Radius: " + radiusGrid + " / " + radiusTouch);
                // Calculate angle
                Double yCircle = yC - Math.sqrt(Math.pow(radiusGrid, 2) - Math.pow(dX, 2));


                paint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(nextPosition.x, yCircle.intValue(), radiusTouch, paint);

                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(4);
                canvas.drawCircle(nextPosition.x, yCircle.intValue(), radiusTouch + 10, paint);

            } else {
                drawStartTouchCircle(canvas);
            }
        } else {
            drawStartTouchCircle(canvas);
        }
    }

    private void drawStartTouchCircle(Canvas canvas) {
        paint.setStyle(Paint.Style.FILL);

        canvas.drawCircle(xC, yC - radiusGrid, radiusTouch - 5, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        canvas.drawCircle(xC, yC - radiusGrid, radiusTouch, paint);
    }

    private void drawAimDarts(Canvas canvas) {
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
        // canvas.drawLine(xStart, yStart, xEnd, yEnd, paint);

        // Draw Half Circle
        // *************
        color = ContextCompat.getColor(context, R.color.colorCircle);
        paint.setColor(color);

        // Outer circle
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(6);

        RectF halfCircleShape = new RectF(xStart, yC - radiusGrid , xEnd, yC + radiusGrid);
        canvas.drawArc(halfCircleShape, 180, 80, false, paint);
        canvas.drawArc(halfCircleShape, 280, 80, false, paint);

        // Darts
        int dartHeight = dartLength;
        int dartWidth = dartLength / 3 * 2;

        //  - Left
        canvas.drawLine(xStart - dartWidth, yStart - dartHeight, xStart + 1, yStart + 1, paint);
        canvas.drawLine(xStart + dartWidth, yStart - dartHeight, xStart + 1, yStart + 1, paint);

        //  - Right
        canvas.drawLine(xEnd - dartWidth, yEnd - dartHeight, xEnd + 1, yEnd + 1, paint);
        canvas.drawLine(xEnd + dartWidth, yEnd - dartHeight, xEnd + 1, yEnd + 1, paint);
    }
}
