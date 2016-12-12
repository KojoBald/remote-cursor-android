package edu.bsu.digitalcorps.remotecursor;

import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class SocketProcess implements Runnable {
    private static final String API_KEY =   "specpromintebald";
    private static final String DEFAULT_HOST = "kojo-work-mac.dhcp.bsu.edu";
    private static final int DEFAULT_PORT = 3000;

    private long timeout;
    private int retryAttempts;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public RemoteCursorActivity parent;
    private BlockingQueue<MoveQuantity> moveQuants;

    private Runnable onConnectionEstablished;


    public SocketProcess() {
        timeout = 15000;
        retryAttempts = 5;
        moveQuants = new ArrayBlockingQueue<>(1);
    }

    public SocketProcess(long timeout, int retryAttempts) {
        this.timeout = timeout;
        this.retryAttempts = retryAttempts;
        moveQuants = new ArrayBlockingQueue<>(1);
    }

    protected void setConnectionCallback(Runnable connectionCallback) {
        onConnectionEstablished = connectionCallback;
    }

    public void addMove(MoveQuantity moveQuantity) {
        moveQuants.offer(moveQuantity);
    }

    protected void disconnectSocket(Runnable onDisconnect) {
        try {
            socket.close();
            onDisconnect.run();
        } catch (IOException e) {
            Log.wtf("SocketProcess","I/O error when disconnecting socket");
            Notification notification = Notification.createErrorNotification(parent, "I/O error when disconnecting socket");
            showNotification(notification);
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        int attempts = 0;
        while(attempts < retryAttempts+1) {
            boolean connected = attemptConnection();
            if(connected) {
                break;
            } else {
                attempts++;
                Notification notification = Notification.createErrorNotification(parent, "Could not establish socket connection. Retrying in " + timeout / 1000 + " seconds.");
                showNotification(notification);
                waitForRetry();
            }
        }
        if(attempts == retryAttempts) {
            Notification notification = Notification.createErrorNotification(parent, "Could not establish socket connection.");
            showNotification(notification);
            return;
        }
        boolean connectionAccepted = connectionProtocol();
        if(connectionAccepted) {
            if(onConnectionEstablished != null) {
                parent.runOnUiThread(onConnectionEstablished);
            }
            maintainConnection();
        }
    }

    private void createIO() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            Log.wtf("SocketProcess","Could not establish socket I/O");
            e.printStackTrace();
        }
    }

    private boolean attemptConnection() {
        try {
            socket = new Socket(DEFAULT_HOST, DEFAULT_PORT);
            createIO();
            return true;
        } catch (UnknownHostException e) {
            Log.wtf("SocketProcess","Could not resolve host");
            Notification notification = Notification.createErrorNotification(parent, "Could not resolve host");
            showNotification(notification);
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            Log.wtf("SocketProcess","Could not establish socket connection");
            Notification notification = Notification.createErrorNotification(parent, "Could not establish socket connection");
            showNotification(notification);
            e.printStackTrace();
            return false;
        }
    }

    private boolean connectionProtocol() {
        try {
            String initialServerMessage = in.readLine();
            Notification notification = Notification.createServerNotification(parent, initialServerMessage);
            showNotification(notification);
        } catch (IOException e) {
            Log.wtf("SocketProcess","Could not read initial server message");
            Notification notification = Notification.createErrorNotification(parent, "Could not read initial server message.");
            showNotification(notification);
            e.printStackTrace();
            return false;
        }
        out.println(API_KEY);
        try {
            String secondServerMessage = in.readLine();
            Notification notification = Notification.createServerNotification(parent, secondServerMessage);
            showNotification(notification);
        } catch (IOException e) {
            Log.wtf("SocketProcess","Could not read second server message");
            Notification notification = Notification.createErrorNotification(parent, "Could not read second server message");
            showNotification(notification);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void waitForRetry() {
        long start = new Date().getTime();
        long now = start;
        while(now - start < timeout) {
            now = new Date().getTime();
        }
    }

    private void maintainConnection() {
        while(!Thread.currentThread().isInterrupted()) {
            emitMove();
        }
    }

    private void emitMove() {
        try {
            MoveQuantity moveQuant = moveQuants.take();
            out.println((moveQuant.x*-1) + "," + (moveQuant.y*-1));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void showNotification(final Notification notification) {
        parent.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((RemoteCursor)parent.getApplication()).showNotification(notification);
            }
        });
    }

//    private void showServerNotification(String message) {
//        parent.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Notification notification = Notification.createServerNotification()
//            }
//        });
//    }
}