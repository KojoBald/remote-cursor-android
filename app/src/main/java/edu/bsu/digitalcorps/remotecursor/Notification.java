package edu.bsu.digitalcorps.remotecursor;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class Notification extends LinearLayout implements View.OnTouchListener {
    private static final int WIDTH = 200;
    private static final int MIN_HEIGHT = 90;
    private TextView text;

    protected float xInLayout;
    protected float yInLayout;

    private Notification(Context context, String message) {
        super(context);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(convertToDip(WIDTH), ViewGroup.LayoutParams.WRAP_CONTENT);

        setOrientation(VERTICAL);
        int padding = convertToDip(5);
        setPadding(padding, padding, padding, padding);
        setLayoutParams(layoutParams);
        layoutParams.setMargins(0, 0, 0, convertToDip(10));
        setMinimumHeight(convertToDip(MIN_HEIGHT));

        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        text = new TextView(context);
        text.setText(message);
        textParams.gravity = Gravity.CENTER;
        text.setLayoutParams(textParams);
        addView(text);

        setOnTouchListener(this);
    }

    float dX, originalX, originalY;

    @Override
    public boolean onTouch(final View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                originalX = view.getX();
                originalY = view.getY();
                dX = view.getX() - event.getRawX();
                break;

            case MotionEvent.ACTION_MOVE:
                view.animate()
                        .x(event.getRawX() + dX)
                        .setDuration(0)
                        .start();
                break;
            case MotionEvent.ACTION_UP:
                float changeX = view.getX() - originalX;
                if(changeX < 0) {
                    changeX = changeX*-1;
                }
                if(changeX > .7*(view.getWidth())) {
                    exitAnimation(view, originalX, originalY);
;                } else {
                    view.animate()
                            .x(originalX)
                            .setDuration(500)
                            .start();
                }
            default:
                return false;
        }
        return true;
    }

    public static Notification createNotification(Context context, String message) {
        Notification newNotification = new Notification(context, message);
        newNotification.setBackgroundColor(newNotification.getResources().getColor(R.color.notification));
        return newNotification;
    }

    public static Notification createSuccessNotification(Context context, String message) {
        Notification newNotification = new Notification(context, message);
        newNotification.setBackgroundColor(newNotification.getResources().getColor(R.color.successNotification));
        return newNotification;
    }

    public static Notification createErrorNotification(Context context, String message) {
        Notification newNotification = new Notification(context, message);
        newNotification.setBackgroundColor(newNotification.getResources().getColor(R.color.errorNotification));
        return newNotification;
    }

    public static Notification createServerNotification(Context context, String message) {
        Notification newNotification = createNotification(context, message);
        TextView serverMessage = new TextView(context);
        serverMessage.setText(R.string.server_message);
        serverMessage.setTypeface(null, Typeface.BOLD);
//        serverMessage.setTextSize(serverMessage.getTextSize());
        newNotification.addView(serverMessage, 0);
        return newNotification;
    }

    private int convertToDip(int px) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (px * scale + 0.5f);
    }

    private void exitAnimation(final View view, float previousX, float previousY) {
        AlphaAnimation fade = new AlphaAnimation(1, 0);
        TranslateAnimation move = new TranslateAnimation(TranslateAnimation.RELATIVE_TO_PARENT, 0.0f, TranslateAnimation.RELATIVE_TO_PARENT, 1.0f, TranslateAnimation.RELATIVE_TO_SELF, 0.0f, TranslateAnimation.RELATIVE_TO_SELF, 0.0f);
        final AnimationSet animations = new AnimationSet(true);

        animations.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                ((ViewGroup)view.getParent()).removeView(view);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        int index = ((ViewGroup)view.getParent()).indexOfChild(view);
        ArrayList<View> nextViews = new ArrayList<>();
        for(int i = index+1; i<((ViewGroup)view.getParent()).getChildCount(); i++) {
            nextViews.add(((ViewGroup)view.getParent()).getChildAt(i));
        }

        animations.addAnimation(fade);
        animations.addAnimation(move);
        animations.setDuration(500);
        view.startAnimation(animations);

//        for(int i = 0; i < nextViews.size(); i++) {
//            View nextView = nextViews.get(i);
//            float currentX = nextView.getX();
//            float currentY = nextView.getY();
//            if(i == 0) {
//                replaceAnimation(nextView, previousX, previousY, view);
//            } else {
//                replaceAnimation(nextView, previousX, previousY, null);
//            }
//            previousX = currentX;
//            previousY = currentY;
//        }

        for(View nextView : nextViews) {
            float currentX = nextView.getX();
            float currentY = nextView.getY();
            replaceAnimation(nextView, previousX, previousY);
            previousX = currentX;
            previousY = currentY;
        }
    }

    private void replaceAnimation(final View view, final float x, final float y) {
        float currentHeightPercentage = (((ViewGroup)view.getParent()).getHeight()/view.getY())/100;
        float nextHeightPercentage = (((ViewGroup)view.getParent()).getHeight()/y)/100;
//        Log.i("Notification", "Current height: " + currentHeightPercentage + ", next height: " + nextHeightPercentage);
        TranslateAnimation move = new TranslateAnimation(TranslateAnimation.RELATIVE_TO_PARENT, 0.0f, TranslateAnimation.RELATIVE_TO_PARENT, 0.0f, TranslateAnimation.RELATIVE_TO_SELF, 0.0f, TranslateAnimation.RELATIVE_TO_SELF, -1.2f);
        move.setDuration(500);
        move.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                view.setX(x);
                view.setY(y);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        view.startAnimation(move);
    }
}
