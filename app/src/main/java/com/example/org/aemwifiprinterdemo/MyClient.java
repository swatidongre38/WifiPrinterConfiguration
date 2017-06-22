package com.example.org.aemwifiprinterdemo;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class MyClient extends AsyncTask<Void, Void, Socket> {

    public static final int PORT_NUMBER = 9100;
    String mResponse = "";
    public Socket mSocket = null;
    public static final int BUFFER_SIZE = 2048;
    private PrintWriter mOut = null;
    private BufferedReader mIn = null;
    private String mDstAddress = null;
    private int mDstPort = PORT_NUMBER;
    private final Context mContext;
    private String mText;
    public static boolean mWifiConnection = false;
    public static boolean connection = true;

    /**
     * Constructor with Host, Port and MAC Address
     */
    public MyClient(final String text, final Context context) {
        this.mDstAddress = text;
        this.mDstPort = PORT_NUMBER;
        this.mContext = context;
    }

    public void configSocket(final String host) {
        this.mDstAddress = host;
        this.mDstPort = PORT_NUMBER;
    }

    @Override
    protected Socket doInBackground(Void... arg0) {
        try {
            this.mSocket = new Socket(mDstAddress, mDstPort);
            mOut = new PrintWriter(mSocket.getOutputStream());
            mIn = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
            mWifiConnection = true;
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            mWifiConnection = false;
            connection = false;
            e.printStackTrace();
        } catch (IOException e) {
            mWifiConnection = false;
            connection = false;
            e.printStackTrace();
        } finally {
        }
        return this.mSocket;
    }

    @Override
    protected void onPostExecute(final Socket result) {
        try {
            if (result != null) {
                ((MainActivity) mContext).onSuccess(this.mResponse, this.mSocket, mDstAddress, mDstPort, mOut);
            } else {
                ((MainActivity) mContext).onError(this.mResponse);
            }
        } catch (final Exception ec) {
            ((MainActivity) mContext).onError(this.mResponse);
        }
    }

    public void sendDataOnSocket(final String message, final PrintWriter out) {
        if (message != null) {
            out.write(message);
            out.flush();
        }
    }

    public void sendBytesOnSocket(final byte[] btPkt, final int numBytes, final Socket socket) {
        try {
            socket.getOutputStream().write(btPkt, 0, numBytes);
        } catch (IOException e) {
            ((MainActivity) mContext).onError(this.mResponse);
        }
    }

    public int disConnectWithServer() {
        int retVal = 0;
        if (mSocket != null) {
            if (mSocket.isConnected()) {
                try {
                    mSocket.close();
                    while (true) {
                        if (mSocket.isClosed() == true) {
                            retVal = 1;
                            return retVal;
                        }
                    }
                } catch (IOException e) {
                    ((MainActivity) mContext).onError(this.mResponse);
                }
            }
        }
        return retVal;
    }
}