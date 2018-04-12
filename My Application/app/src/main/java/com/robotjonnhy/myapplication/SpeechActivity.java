package com.robotjonnhy.myapplication;

import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Locale;
import java.io.OutputStream;
import java.util.UUID;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import android.app.Activity;
import android.speech.RecognizerIntent;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.MotionEvent;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SpeechActivity extends AppCompatActivity  {


    private TextView txtSpeechInput;
    private ImageButton btnSpeak;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private String message;

    Button btnControl;
    TextView txtArduino, txtString, txtStringLength, sensorView0, sensorView1, sensorView2, sensorView3;
    Handler bluetoothIn;

    final int handlerState = 0;                        //used to identify handler message

    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder recDataString = new StringBuilder();

    private ConnectedThread mConnectedThread;

    // SPP UUID service - this should work for most devices
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // String for MAC address
    private static String address;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech);

        txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);

        btnControl = (Button) findViewById(R.id.buttonControl);

        txtString = (TextView) findViewById(R.id.txtString);


        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {                                     //if message is what we want
                    String readMessage = (String) msg.obj;                                                                // msg.arg1 = bytes from connect thread
                    recDataString.append(readMessage);                                      //keep appending to string until ~
                    int endOfLineIndex = recDataString.indexOf("~");                    // determine the end-of-line
                    if (endOfLineIndex > 0) {                                           // make sure there data before ~
                        String dataInPrint = recDataString.substring(0, endOfLineIndex);    // extract string
                        txtString.setText("Data Received = " + dataInPrint);
                        int dataLength = dataInPrint.length();                          //get length of data received
                        txtStringLength.setText("String Length = " + String.valueOf(dataLength));

                        if (recDataString.charAt(0) == '#')                             //if it starts with # we know it is what we are looking for
                        {
                            String sensor0 = recDataString.substring(1, 5);             //get sensor value from string between indices 1-5
                            String sensor1 = recDataString.substring(6, 10);            //same again...
                            String sensor2 = recDataString.substring(11, 15);
                            String sensor3 = recDataString.substring(16, 20);

                            sensorView0.setText(" Sensor 0 Voltage = " + sensor0 + "V");    //update the textviews with sensor values
                            sensorView1.setText(" Sensor 1 Voltage = " + sensor1 + "V");
                            sensorView2.setText(" Sensor 2 Voltage = " + sensor2 + "V");
                            sensorView3.setText(" Sensor 3 Voltage = " + sensor3 + "V");
                        }
                        recDataString.delete(0, recDataString.length());                    //clear all string data
                        // strIncom =" ";
                        dataInPrint = " ";
                    }
                }
            }
        };

        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        checkBTState();


        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                promptSpeechInput();
            }


        });

        btnControl.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                startActivity(new Intent(SpeechActivity.this, MainActivity.class));
            }


        });
    }



//    @Override
//Override    public void onJoystickMoved(float xPercent, float yPercent, int id) {
//        switch (id)
//        {
//            case R.id.joystickRight:
////                sendMessage(128);
//                Log.d("Right Joystick", "X percent: " + xPercent + " Y percent: " + yPercent);
//                break;
//            case R.id.joystickLeft:
//                Log.d("Left Joystick", "X percent: " + xPercent + " Y percent: " + yPercent);
//                break;
//        }
//    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txtSpeechInput.setText(result.get(0));

                    Toast.makeText(getBaseContext(), "Bonjour", Toast.LENGTH_SHORT).show();


                }
                break;
            }

        }
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connecetion with BT device using UUID
    }

    @Override
    public void onResume() {
        super.onResume();

        //Get MAC address from DeviceListActivity via intent
        Intent intent = getIntent();

        //Get the MAC address from the DeviceListActivty via EXTRA
        address = intent.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS);

        //create device and set the MAC address
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_LONG).show();
        }
        // Establish the Bluetooth socket connection.
        try
        {
            btSocket.connect();
        } catch (IOException e) {
            try
            {
                btSocket.close();
            } catch (IOException e2)
            {
                //insert code to deal with this
            }
        }
        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();

        if(txtSpeechInput.getText().equals("avance") || txtSpeechInput.getText().equals("Avance" )){
            sendMessage(14);
        }
        if(txtSpeechInput.getText().equals("arrête")){
            sendMessage(18);
        }
        if(txtSpeechInput.getText().equals("à droite")){
            sendMessage(15);
        }
        if(txtSpeechInput.getText().equals("à gauche")){
            sendMessage(17);
        }

        if(txtSpeechInput.getText().equals("recul")){
            sendMessage(16);
        }

        //I send a character when resuming.beginning transmission to check device is connected
        //If it is not an exception will be thrown in the write method and finish() will be called
        sendMessage(0);              //write bytes over BT connection via outstream

    }

    @Override
    public void onPause()
    {
        super.onPause();
        try
        {
            //Don't leave Bluetooth sockets open when leaving activity
            btSocket.close();
        } catch (IOException e2) {
            //insert code to deal with this
        }
    }

    //Checks that the Android device Bluetooth is available and prompts to be turned on if off
    private void checkBTState() {

        if(btAdapter==null) {
            Toast.makeText(getBaseContext(), "Device does not support bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }



    private void sendMessage(Integer input){
        try {
            mConnectedThread.write(input);
        } catch (IOException e) {
            //if you cannot write, close the application
            Toast.makeText(getBaseContext(), "Connection Failure", Toast.LENGTH_LONG).show();
            finish();

        }

    }



}

