package com.example.marcneumann.mercedesme;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;

import com.example.marcneumann.mercedesme.discovery.DeviceListFragment;
import com.example.marcneumann.mercedesme.mode_select.ModeSelectFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.fragment_container)
    FrameLayout mContainer;

    private ModeSelectFragment mModeSelectFragment;
    private DeviceListFragment mDeviceListFragment;
    private BluetoothSocket mBluetoothSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        goToModeSelect();
    }


    public void goToModeSelect(BluetoothSocket socket) {
        mBluetoothSocket = socket;
        goToModeSelect();
    }

    public void goToModeSelect() {
        if (mModeSelectFragment == null) {
            mModeSelectFragment = new ModeSelectFragment();
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, mModeSelectFragment)
                .addToBackStack(null)
                .commit();
    }

    public void goToDeviceList() {
        if (mDeviceListFragment == null) {
            mDeviceListFragment = new DeviceListFragment();
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, mDeviceListFragment)
                .addToBackStack(null)
                .commit();
    }

    public void showNoConnection(String message) {
        Snackbar.make(mContainer, message, Snackbar.LENGTH_LONG).show();
    }

    public void showNoConnection() {
        Snackbar.make(mContainer, "Keine Verbindung verfügbar", Snackbar.LENGTH_INDEFINITE)
                .setAction("Verbinden", view -> goToDeviceList()).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (mDeviceListFragment != null) {
            mDeviceListFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public BluetoothSocket getBluetoothSocket() {
        return mBluetoothSocket;
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() <= 1) {
            finish();
        } else {
            super.onBackPressed();
        }
    }
}
