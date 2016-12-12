package edu.bsu.digitalcorps.remotecursor;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

public class RemoteCursor extends Application {
    private static final String PREFS_NAME = "remote_cursor";
    private static final String SCROLL_SPEED = "scroll_speed";

    private SocketProcess socketProcess;
    private Thread socketProcessThread;
    private boolean socketProcessRunning = false;
    private RemoteCursorActivity currentActivity;
    public float scrollSpeed;

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, 0);
        scrollSpeed = preferences.getFloat(SCROLL_SPEED, 1);
        socketProcess = new SocketProcess();
    }

    public SocketProcess getSocketProcess() {
        return socketProcess;
    }

    public void startSocketProcess(Runnable connectionCallback) {
        socketProcess.setConnectionCallback(connectionCallback);
        startSocketProcess();
    }

    public void startSocketProcess() {
        socketProcessThread = new Thread(socketProcess);
        socketProcessThread.start();
        socketProcessRunning = true;
    }

    public void stopSocketProcess(Runnable onDisconnect) {
        socketProcess.disconnectSocket(onDisconnect);
    }

    public void stopSocketProcess() {
        socketProcess.disconnectSocket(new Runnable() {
            @Override
            public void run() {}
        });
    }

    public void setCurrentActivity(RemoteCursorActivity newCurrent) {
        Log.i("RemoteCursor","activity changing");
        currentActivity = newCurrent;
        socketProcess.parent = currentActivity;
    }

    public void showNotification(Notification notification) {
        currentActivity.showNotification(notification);
    }


}
