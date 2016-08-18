package com.example.marcneumann.mercedesme.discovery;

import android.bluetooth.BluetoothDevice;

class BluetoothHelper {

    static String deviceToString(BluetoothDevice device) {
        return device.getName()
                + "\nMAC-Address: " + device.getAddress()
                + "\n" + bondToString(device.getBondState())
                + "\n" + typeToString(device.getType());
    }

    private static String typeToString(int type) {
        String s = "Device Type: ";
        switch (type) {
            case BluetoothDevice.DEVICE_TYPE_CLASSIC:
                return s + "classic";
            case BluetoothDevice.DEVICE_TYPE_DUAL:
                return s + "dual";
            case BluetoothDevice.DEVICE_TYPE_LE:
                return s + "le";
            default:
                return s + "unknown";
        }
    }

    private static String bondToString(int bond) {
        String s = "Bond State: ";
        switch (bond) {
            case BluetoothDevice.BOND_BONDED:
                return s + "bonded";
            case BluetoothDevice.BOND_BONDING:
                return s + "bonding";
            default:
                return s + "none";
        }
    }
}
