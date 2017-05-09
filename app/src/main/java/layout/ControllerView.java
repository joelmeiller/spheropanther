package layout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;
import android.view.View;

import ch.fhnw.edu.emoba.spheropantherapp.R;

/**
 * Created by Joel on 09/05/17.
 */

public class ControllerView extends View {

    private Paint paint;

    public ControllerView(Context context) {
        super(context);

        // create the Paint and set its color
        paint = new Paint();
        paint.setColor(Color.BLUE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(0, 0, 300, paint);
    }
}
