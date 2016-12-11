package edu.bsu.digitalcorps.remotecursor;

import android.app.Application;
import android.content.SharedPreferences;

public class RemoteCursor extends Application {
    private static final String PREFS_NAME = "remote_cursor";

    private static final String SCROLL_SPEED = "scroll_speed";
    public float scrollSpeed;

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, 0);
        scrollSpeed = preferences.getFloat(SCROLL_SPEED, 1);
    }


}
