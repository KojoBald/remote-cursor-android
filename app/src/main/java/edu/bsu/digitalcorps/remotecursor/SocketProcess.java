package edu.bsu.digitalcorps.remotecursor;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
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

    private Activity parent;
    private LinkedList<MoveQuantity> pendingMoves;
    private BlockingQueue<MoveQuantity> moveQuants;

    public SocketProcess(Activity context) {
        this.parent = context;
        timeout = 15000;
        retryAttempts = 5;
        pendingMoves = new LinkedList<>();
        moveQuants = new ArrayBlockingQueue<>(1);
    }

    public SocketProcess(Activity context, long timeout, int retryAttempts) {
        this.parent = context;
        this.timeout = timeout;
        this.retryAttempts = retryAttempts;
        pendingMoves = new LinkedList<>();
        moveQuants = new ArrayBlockingQueue<>(1);
    }

    public void addMove(MoveQuantity moveQuantity) {
        boolean didAdd = false;
        if(moveQuants.isEmpty()) {
            didAdd = moveQuants.offer(moveQuantity);
        }
        if(!didAdd) {
            pendingMoves.add(moveQuantity);
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
                showToast("Could not establish socket connection. Retrying in " + timeout / 1000 + " seconds.");
                waitForRetry();
            }
        }
        if(attempts == retryAttempts) {
            showToast("Could not establish socket connection.");
            return;
        }
        boolean connectionAccepted = connectionProtocol();
        if(connectionAccepted) {
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
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            Log.wtf("SocketProcess","Could not establish socket connection");
            e.printStackTrace();
            return false;
        }
    }

    private boolean connectionProtocol() {
        try {
            String initialServerMessage = in.readLine();
            showServerMessageToast(initialServerMessage);
        } catch (IOException e) {
            Log.wtf("SocketProcess","Could not read initial server message");
            e.printStackTrace();
            return false;
        }
        out.println(API_KEY);
        try {
            String secondServerMessage = in.readLine();
            showServerMessageToast(secondServerMessage);
        } catch (IOException e) {
            Log.wtf("SocketProcess","Could not read second server message");
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

            if(!pendingMoves.isEmpty()) {
                moveQuants.add(pendingMoves.getFirst());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void showToast(final String message) {
        parent.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(parent.getBaseContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showServerMessageToast(String message) {
        showToast("Message from the server: " + message);
    }
}