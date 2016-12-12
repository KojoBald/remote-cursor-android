package edu.bsu.digitalcorps.remotecursor;

import android.app.Activity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class GestureHandler implements GestureDetector.OnGestureListener {
    private RemoteCursor app;
    private Activity parent;
    private SocketProcess socketProcess;

    public GestureHandler(Activity parent) {
        this.parent = parent;
        this.app = (RemoteCursor) parent.getApplication();
        this.socketProcess = app.getSocketProcess();
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//        MoveQuantity move = new MoveQuantity(distanceX * app.scrollSpeed, distanceY * app.scrollSpeed);
//        socketProcess.addMove(move);
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    public void multiTouch(MotionEvent event) {

    }

    public void singleTouch(MotionEvent event) {

    }
}
