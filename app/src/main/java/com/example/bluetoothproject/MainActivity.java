package com.example.bluetoothproject;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.io.IOException;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {
    //this is the mac address of connected bluetooth module
    public final static String MODULE_MAC = "00:19:08:35:23:26";
    public final static int REQUEST_ENABLE_BT = 1;
    //the uuid of hc05 bluetooth module
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    BluetoothAdapter bta;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    ConnectedThread btt = null;
    Button switchLightON, switchLightOFF, switchRelay, send;
    TextView response;
    EditText editText;
    //boolean lightflag = false;
    boolean relayFlag = true;
    public Handler mHandler;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //hooks
        Log.i("[BLUETOOTH]", "Creating listeners");
        response = findViewById(R.id.response);
        switchRelay = findViewById(R.id.relay);
        switchLightON = findViewById(R.id.switchLightOn);
        switchLightOFF = findViewById(R.id.switchLightOff);
        editText = findViewById(R.id.command);
        send = findViewById(R.id.send);


        //when switch on button is clicked
        switchLightON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("[BLUETOOTH]", "Attempting to send data");
                if (mmSocket.isConnected() && btt != null) { //if we have connection to the bluetoothmodule
                    String sendtxt = "LY";
                    btt.write(sendtxt.getBytes());

                } else {
                    Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }
        });


        //when switch of button os clicked
        switchLightOFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("[BLUETOOTH]", "Attempting to send data");
                if (mmSocket.isConnected() && btt != null) { //if we have connection to the bluetoothmodule
                    String sendtxt = "LN";
                    btt.write(sendtxt.getBytes());
                } else {
                    Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }
        });

        //when send is clicked
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sendtxt = editText.getText().toString().trim();

                Log.i("[BLUETOOTH]", "Attempting to send data");
                if (mmSocket.isConnected() && btt != null) { //if we have connection to the bluetoothmodule
                    //the terminal is updated
                    response.append("\n");
                    response.append("Sent: "+sendtxt);
                    editText.setText("");
                    btt.write(sendtxt.getBytes());
                } else {
                    Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }

            }
        });

        //when relay is clicked
        switchRelay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("[BLUETOOTH]", "Attempting to send data");
                if (mmSocket.isConnected() && btt != null) { //if we have connection to the bluetoothmodule
                    if (relayFlag) {
                        String sendtxt = "RY";
                        btt.write(sendtxt.getBytes());
                        relayFlag = false;
                    } else {
                        String sendtxt = "RN";
                        btt.write(sendtxt.getBytes());
                        relayFlag = true;
                    }

                    //disable the button and wait for 4 seconds to enable it again
                    //here the thread will stop any ui
                    switchRelay.setEnabled(false);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(4000);
                            } catch (InterruptedException e) {
                                return;
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    switchRelay.setEnabled(true);
                                }
                            });

                        }
                    }).start();
                } else {
                    Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }
        });

        bta = BluetoothAdapter.getDefaultAdapter();

        //if bluetooth is not enabled then create Intent for user to turn it on
        if (!bta.isEnabled()) {
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
        } else {
            initiateBluetoothProcess();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUEST_ENABLE_BT) {
            initiateBluetoothProcess();
        }
    }

    //if bluetooth is on
    public void initiateBluetoothProcess() {

        if (bta.isEnabled()) {

            //attempt to connect to bluetooth module
            BluetoothSocket tmp = null;
            mmDevice = bta.getRemoteDevice(MODULE_MAC);


            //create socket
            try {
                tmp = mmDevice.createRfcommSocketToServiceRecord(MY_UUID);
                mmSocket = tmp;
                mmSocket.connect();
                Log.i("[BLUETOOTH]", "Connected to: " + mmDevice.getName());
            } catch (IOException e) {
                try {
                    mmSocket.close();
                } catch (IOException c) {
                    return;
                }
            }

            Log.i("[BLUETOOTH]", "Creating handler");
            //this handler is for getting the message from thread run()
            mHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    //super.handleMessage(msg);
                    if (msg.what == ConnectedThread.RESPONSE_MESSAGE) {
                        //Toast.makeText(MainActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                        String txt = (String) msg.obj;
                        if (response.getText().toString().length() >= 30) {
                            response.setText("");
                            response.append(txt);
                        } else {
                            response.append("\n" + txt);
                        }
                    }
                }
            };

            Log.i("[BLUETOOTH]", "Creating and running Thread");

            //passing the mSocket and handler and starting the thread
            btt = new ConnectedThread(mmSocket, mHandler);
            btt.start();

        }
    }

}
