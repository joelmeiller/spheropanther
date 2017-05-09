package layout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.View;

import ch.fhnw.edu.emoba.spheropantherapp.R;

/**
 * Created by Joel on 09/05/17.
 */

public class ControllerView extends View {

    private static int RADIUS = 150;

    private Paint paint;

    public ControllerView(Context context) {
        super(context);

        // create the Paint and set its color
        paint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int xC = (int) canvas.getWidth() / 2;
        int yC = RADIUS + 20;

        // Draw Lines
        // *************
        int color = ContextCompat.getColor(getContext(), R.color.colorLine);
        paint.setColor(color);
        paint.setStrokeWidth(4);

        // Horizontal
        int xStart = xC - RADIUS;
        int yStart = yC;
        int xEnd = xC + RADIUS;
        int yEnd = yC;
        canvas.drawLine(xStart, yStart, xEnd, yEnd, paint);

        // Vertical
        xStart = xC;
        yStart = yC - RADIUS;
        xEnd = xC;
        yEnd = yC + RADIUS;
        canvas.drawLine(xStart, yStart, xEnd, yEnd, paint);

        // Draw Circles
        // *************
        color = ContextCompat.getColor(getContext(), R.color.colorCircle);
        paint.setColor(color);

        // Outer circle
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(6);
        canvas.drawCircle(xC, yC, RADIUS, paint);

        // First inner circle
        paint.setStrokeWidth(2);
        canvas.drawCircle(xC, yC, RADIUS * 2 / 3, paint);

        // Second inner circle
        paint.setStrokeWidth(2);
        canvas.drawCircle(xC, yC, RADIUS / 3, paint);


    }
}
