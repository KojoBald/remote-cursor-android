package edu.bsu.digitalcorps.remotecursor;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;

import java.util.LinkedList;

public abstract class RemoteCursorActivity extends AppCompatActivity {
    private static final int NOTIFICATION_MARGIN = 10;
    public RelativeLayout notificationsContainer;

//    private BlockingQueue<Notification> pendingNotifications = new ArrayBlockingQueue<>(1);
    private LinkedList<Notification> pendingNotifications = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((RemoteCursor)getApplication()).setCurrentActivity(this);
    }

    public void showNotification(final Notification notification) {
        if(pendingNotifications.isEmpty()) {
            pendingNotifications.add(notification);
            addNotificationsToView();
        } else {
//            Runnable notificationTask = new Runnable() {
//                @Override
//                public void run() {
//                    Log.i("Notification", "waiting to put in queue");
//                    pendingNotifications.add(notification);
//                }
//            };
//            Thread thread = new Thread(notificationTask);
//            thread.start();
            pendingNotifications.add(notification);
        }
    }

    private void addNotificationsToView() {
        Notification notification = pendingNotifications.peek();
        Log.i("RemoteCursorActivity","showing notification");
        View lastNotification = notificationsContainer.getChildAt(notificationsContainer.getChildCount()-1);
        float newX = 0, newY = 0, heightPercentage = 0;
        if(lastNotification != null) {
            newX = lastNotification.getX();
            newY = lastNotification.getY() + lastNotification.getHeight() + convertToDip(NOTIFICATION_MARGIN);

            heightPercentage = (notificationsContainer.getHeight()/newY)/100;
            Log.i("Notification", heightPercentage + "");
        }

        TranslateAnimation addAnimation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 1.0f, Animation.RELATIVE_TO_PARENT, heightPercentage);
        addAnimation.setDuration(500);
        addAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                Log.i("Notification", "Animation ending");
                if(!pendingNotifications.isEmpty()) {
                    Log.i("Notification", "adding another notification");
                    addNotificationsToView();
                }
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        notificationsContainer.addView(notification);
        if(lastNotification != null) {
            notification.setX(newX);
            notification.setY(newY);
        }
        notification.startAnimation(addAnimation);
    }

    private int convertToDip(int px) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (px * scale + 0.5f);
    }
}
