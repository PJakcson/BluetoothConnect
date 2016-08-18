package com.example.marcneumann.mercedesme.discovery;

import android.bluetooth.BluetoothDevice;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.marcneumann.mercedesme.R;

import java.util.ArrayList;
import java.util.List;

class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.SimpleViewHolder> {
    private BluetoothInteractor mInteractor;
    private List<BluetoothDevice> mDevices = new ArrayList<>();

    DeviceAdapter(BluetoothInteractor interactor) {
        mInteractor = interactor;
    }

    void updateDevices(BluetoothDevice device) {
        if (!mDevices.contains(device)) {
            mDevices.add(device);
            notifyDataSetChanged();
        }
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, int position) {
        BluetoothDevice device = mDevices.get(position);
        holder.mTextView.setText(BluetoothHelper.deviceToString(device));
        if (device.getName().contains("HC") && device.getName().contains("05")) {
            holder.itemView.setBackgroundResource(R.color.colorHC05Highlight);
        }
    }

    @Override
    public int getItemCount() {
        return mDevices.size();
    }

    private void onDeviceClicked(int index) {
        if (index < 0 || index >= getItemCount()) {
            return;
        }
        BluetoothDevice device = mDevices.get(index);
        if (device.getBondState() == BluetoothDevice.BOND_NONE) {
            mInteractor.createBond(device);
        } else if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
            mInteractor.initializeSocketConnection(device);
        }
    }

    void clear() {
        mDevices.clear();
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {

        private TextView mTextView;

        SimpleViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView;
            itemView.setOnClickListener(view -> onDeviceClicked(getAdapterPosition()));
        }
    }
}