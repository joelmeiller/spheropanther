package layout;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

/**
 * Created by Joel on 21/05/17.
 */

public class RobotControlThread extends HandlerThread {

    public static final String TAG = RobotControlThread.class.toString();

    public static final int POSITION_CHANGED = 1;

    public static final String POS_X = "position X";
    public static final String POS_Y = "position Y";

    private Handler uiThreadHandler;
    private Handler robotControlHandler;

    private int centerX = 0;
    private int centerY = 0;


    public RobotControlThread(String name, int cX, int cY) {
        super(name);

        this.centerX = cX;
        this.centerY = cY;
    }

    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();

        robotControlHandler = new Handler(getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                Log.i(TAG, "New message (type=" + msg.what + ")");
                int x = msg.getData().getInt(POS_X);
                int y = msg.getData().getInt(POS_Y);

                if (msg.what == POSITION_CHANGED) {

                    Log.d(TAG, "New position: " + x + " / " + y);
                    // TODO: drive robot

                }
            }
        };
    }

    private void moveRobot(int x, int y) {

    }

    public void setUIThreadHandler(Handler handler) {
        this.uiThreadHandler = handler;
    }

    public Handler getRobotControlThreadHandler() {
        return robotControlHandler;
    }
}
