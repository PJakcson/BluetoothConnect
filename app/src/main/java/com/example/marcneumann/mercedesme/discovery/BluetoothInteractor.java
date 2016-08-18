package com.example.marcneumann.mercedesme.discovery;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

interface BluetoothInteractor {

    void createBond(BluetoothDevice device);

    void initializeSocketConnection(BluetoothDevice device);

    void establishedSocketConnection(BluetoothSocket socket);
}
