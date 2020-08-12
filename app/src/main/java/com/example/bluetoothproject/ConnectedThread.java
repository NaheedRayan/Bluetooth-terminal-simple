package com.example.bluetoothproject;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class ConnectedThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    public static final int RESPONSE_MESSAGE = 10;
    Handler uih;

    public ConnectedThread(BluetoothSocket socket, Handler uih) {
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        this.uih = uih;
        Log.i("[THREAD-CT]", "Creating thread");
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();

        } catch (IOException e) {
            Log.e("[THREAD-CT]", "Error:" + e.getMessage());
        }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
//        try {
//            mmOutStream.flush();
//        } catch (IOException e) {
//            return;
//        }
        Log.i("[THREAD-CT]", "IO's obtained");

    }

    public void run() {
        byte[] buffer = new byte[128];  // buffer store for the stream
        int bytes; // bytes returned from read()
        String readMessage = "";
        readMessage ="" ;


        // Keep listening to the InputStream until an exception occurs
        while (true) {

            try {

                bytes = mmInStream.read(buffer);
                readMessage = new String(buffer, 0, bytes);

                // Send the obtained bytes to the UI Activity
                Log.i("[The message]", readMessage);
                Log.i("[The bytes]", String.valueOf(bytes));

            } catch (IOException e) {
                e.printStackTrace();
                Log.i("[e inside while]", e.getMessage());

            }

            //Transfer these data to the UI thread
            Message msg = new Message();
            msg.what = RESPONSE_MESSAGE;
            msg.obj = readMessage;
            uih.sendMessage(msg);


            Log.i("[THREAD-CT]", "While loop ended");
            //readMessage = "";
        }

    }


    public void write(byte[] bytes) {
        try {
            Log.i("[THREAD-CT]", "Writting bytes");
            mmOutStream.write(bytes);
            //return;

        } catch (IOException e) {
        }
    }

    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
        }
    }
}