package layout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Joel on 09/05/17.
 */

public class TouchControllerView extends View {

    private static String TAG = TouchControllerView.class.toString();


    private ControllerGrid grid;
    private Point nextPosition;

    public TouchControllerView(Context context) {
        super(context);

        grid = new ControllerGrid(context);
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

        grid.drawGrid(canvas, nextPosition);
    }

}
