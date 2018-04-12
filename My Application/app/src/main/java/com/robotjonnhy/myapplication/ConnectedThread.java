package com.robotjonnhy.myapplication;

import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * Created by lucascoiado on 10/04/18.
 */

public class ConnectedThread extends Thread {

    private final InputStream mmInStream;
    private final OutputStream mmOutStream;

    //creation of the connect thread
    public ConnectedThread(BluetoothSocket socket) {
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        try {
            //Create I/O streams for connection
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) { }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    public void run() {
        byte[] buffer = new byte[256];
        int bytes;

        // Keep looping to listen for received messages
        while (true) {
            try {
                bytes = mmInStream.read(buffer);            //read bytes from input buffer

                String readMessage = new String(buffer, 0, bytes);
                // Send the obtained bytes to the UI Activity via handler
//                bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
            } catch (IOException e) {
                break;
            }
        }
    }

    //write method
    public void write(int input) throws IOException {
        byte msgBuffer = new Integer(input).byteValue();             //converts entered String into bytes
        mmOutStream.write(msgBuffer);
    }
}
