package edu.bsu.digitalcorps.remotecursor;

import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

public class CursorActivity extends AppCompatActivity {

    private GestureDetector gestureDetector;
    private final String DEBUG_TAG = "Main";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_cursor);

        gestureDetector = new GestureDetector(this, new GestureHandler(this));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        int index = MotionEventCompat.getActionIndex(event);
        int xPos = -1;
        int yPos = -1;

//        Log.d(DEBUG_TAG,"The action is " + actionToString(action));

        if (event.getPointerCount() > 1) {
//            for(int i = 0; i < event.getPointerCount(); i++) {
//                xPos = (int) MotionEventCompat.getX(event, i);
//                yPos = (int) MotionEventCompat.getY(event, i);
//                Log.d(DEBUG_TAG, "Multitouch event " + i + "; x: " + xPos + ", y: " + yPos);
//            }

        } else {
            xPos = (int)MotionEventCompat.getX(event, index);
            yPos = (int) MotionEventCompat.getY(event, index);
            Log.d(DEBUG_TAG,"Single touch event; x: " + xPos + ", y: " + yPos);
        }

        this.gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    public static String actionToString(int action) {
        switch (action) {

            case MotionEvent.ACTION_DOWN: return "Down";
            case MotionEvent.ACTION_MOVE: return "Move";
            case MotionEvent.ACTION_POINTER_DOWN: return "Pointer Down";
            case MotionEvent.ACTION_UP: return "Up";
            case MotionEvent.ACTION_POINTER_UP: return "Pointer Up";
            case MotionEvent.ACTION_OUTSIDE: return "Outside";
            case MotionEvent.ACTION_CANCEL: return "Cancel";
        }
        return "";
    }
}
