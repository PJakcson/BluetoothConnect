package com.example.marcneumann.mercedesme;

import android.app.Application;
import android.bluetooth.BluetoothSocket;

public class MBsync extends Application {
    private BluetoothSocket mBluetoothSocket;

    public BluetoothSocket getBluetoothSocket() {
        return mBluetoothSocket;
    }

    public void setBluetoothSocket(BluetoothSocket bluetoothSocket) {
        mBluetoothSocket = bluetoothSocket;
    }
}
