package edu.bsu.digitalcorps.remotecursor;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class GestureHandler implements GestureDetector.OnGestureListener {
    private RemoteCursor app;
    private SocketProcess socketProcess;

    public GestureHandler(RemoteCursor app, SocketProcess socketProcess) {
        this.app = app;
        this.socketProcess = socketProcess;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        Log.v("Main", "down");
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.v("Main", "single tap up");
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        MoveQuantity move = new MoveQuantity(distanceX * app.scrollSpeed, distanceY * app.scrollSpeed);
        socketProcess.addMove(move);
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}
