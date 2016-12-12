package edu.bsu.digitalcorps.remotecursor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ConnectionActivity extends RemoteCursorActivity {
    private TextView connectionMessage;
    private Button connectionButton;
    private ProgressBar progressBar;
    private TextView retryMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        connectionMessage = (TextView)findViewById(R.id.connectionMessage);
        connectionButton = (Button)findViewById(R.id.connectionButton);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        retryMessage = (TextView)findViewById(R.id.retryMessage);
        notificationsContainer = (RelativeLayout) findViewById(R.id.notifications);

        setupForConnect();
    }

    private void setupForConnect() {
        connectionMessage.setText(R.string.desktop_connect);
        connectionButton.setText(R.string.connect);
        setupConnectButton();
    }

    private void setupForDisconnect() {
        connectionMessage.setText(R.string.desktop_disconnect);
        connectionButton.setText(R.string.disconnect);
        setupDisconnectButton();
    }

    private void setupConnectButton() {
        final Activity context = this;
        connectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                RemoteCursor app = (RemoteCursor)getApplication();
                app.startSocketProcess(new Runnable() {
                    @Override
                    public void run() {
                        //Callback only if connection is successful
                        progressBar.setVisibility(View.INVISIBLE);
                        findViewById(R.id.continueButton).setVisibility(View.VISIBLE);
                        findViewById(R.id.continueText).setVisibility(View.VISIBLE);
                        Notification notification = Notification.createSuccessNotification(context, "Connection accepted.");
                        showNotification(notification);
                        setupForDisconnect();
                    }
                });
            }
        });
    }

    private void setupDisconnectButton() {
        final Activity context = this;
        connectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemoteCursor app = (RemoteCursor)getApplication();
                app.stopSocketProcess(new Runnable() {
                    @Override
                    public void run() {
                        //Callback only if disconnect is successful
                        Notification notification = Notification.createSuccessNotification(context, "Disconnect successful.");
                        showNotification(notification);
                        setupForConnect();
                    }
                });
            }
        });
    }

    public void goToCursorActivity(View v) {
        Intent cursorIntent = new Intent(this, CursorActivity.class);
        startActivity(cursorIntent);
    }
}
