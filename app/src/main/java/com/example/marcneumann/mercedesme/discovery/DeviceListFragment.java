package com.example.marcneumann.mercedesme.discovery;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.marcneumann.mercedesme.MainActivity;
import com.example.marcneumann.mercedesme.R;
import com.example.marcneumann.mercedesme.common.Anim;

import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeviceListFragment extends Fragment implements BluetoothInteractor {
    private static final int MY_PERMISSIONS_REQUEST_BLUETOOTH = 0;
    private static final int REQUEST_ENABLE_BT = 1;

    @BindView(R.id.new_devices)
    RecyclerView mDeviceList;

    @BindView(R.id.device_list_refresh)
    FloatingActionButton mRefreshButton;

    private MainActivity mContext;
    private DeviceAdapter mAdapter;
    private BluetoothAdapter mBluetoothAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_device_list, container, false);
        ButterKnife.bind(this, rootView);
        mContext.setTitle("Wähle ein Bluetooth Gerät");

        // setup recycler
        mDeviceList.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new DeviceAdapter(this);
        mDeviceList.setAdapter(mAdapter);

        // setup intent filters
        setupFilters();

        // check permissions
        int permissionCheck = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permissionCheck < 0) {
            requestBluetoothPermissions();
        } else {
            searchBluetoothDevices();
        }
        return rootView;
    }

    private void setupFilters() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        mContext.registerReceiver(mReceiver, filter);
        filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        mContext.registerReceiver(mReceiver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        mContext.registerReceiver(mReceiver, filter);
    }

    private void searchBluetoothDevices() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            mContext.showNoConnection("Bluetooth nicht verfügbar.");
        } else if (!mBluetoothAdapter.isEnabled()) {
            turnOnBluetooth();
        } else {
            Anim.rotate(mRefreshButton);
            getPairedDevices();
            mBluetoothAdapter.startDiscovery();
        }
    }

    private void turnOnBluetooth() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }

    private void getPairedDevices() {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                mAdapter.updateDevices(device);
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    private void requestBluetoothPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(mContext, Manifest.permission.BLUETOOTH)
                && ActivityCompat.shouldShowRequestPermissionRationale(mContext, Manifest.permission.BLUETOOTH_ADMIN)
                && ActivityCompat.shouldShowRequestPermissionRationale(mContext, Manifest.permission.ACCESS_COARSE_LOCATION)) {
        } else {
            ActivityCompat.requestPermissions(mContext, new String[]{Manifest.permission.BLUETOOTH
                    , Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_BLUETOOTH);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK) {
            searchBluetoothDevices();
        } else {
            Snackbar.make(mDeviceList, "Could not turn on bluetooth", Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_BLUETOOTH: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    searchBluetoothDevices();
                }
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            mContext = (MainActivity) context;
        } else {
            throw new ClassCastException(null);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mContext.unregisterReceiver(mReceiver);

    }

    @Override
    public void createBond(BluetoothDevice device) {
        mContext.showNoConnection("Pairing...");
        device.createBond();
    }

    @Override
    public void initializeSocketConnection(BluetoothDevice device) {
        mContext.showNoConnection("Initialisiere Verbindung");
        new ConnectThread(mBluetoothAdapter, device, this).start();
    }

    @Override
    public void establishedSocketConnection(BluetoothSocket socket) {
        if (socket.isConnected()) {
            mContext.goToModeSelect(socket);
        } else {
            mContext.showNoConnection("Verbindung fehlgeschlagen");
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            BluetoothDevice device;
            switch (action) {
                case BluetoothDevice.ACTION_FOUND:
                    device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    mAdapter.updateDevices(device);
                    break;
                case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                    device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                        initializeSocketConnection(device);
                    } else {
                        mAdapter.clear();
                        searchBluetoothDevices();
                    }
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    mRefreshButton.clearAnimation();
                    break;
            }
        }
    };

    @OnClick(R.id.device_list_refresh)
    void onRefreshClicked() {
        mAdapter.clear();
        searchBluetoothDevices();
    }
}
