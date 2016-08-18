package com.example.marcneumann.mercedesme.mode_select;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.marcneumann.mercedesme.MainActivity;
import com.example.marcneumann.mercedesme.R;
import com.example.marcneumann.mercedesme.common.Anim;

import java.util.List;

import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ModeSelectFragment extends Fragment {

    @BindViews({R.id.image_first, R.id.image_second, R.id.image_third, R.id.image_fourth,
            R.id.image_fifth, R.id.image_sixth})
    List<ImageView> mModePicker;

    private MainActivity mContext;
    private ImageView mColoredView;
    private BluetoothSocket mBluetoothSocket;
    private String[] mModes;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mode_select, container, false);
        ButterKnife.bind(this, rootView);
        mContext.setTitle("MercedesMe");

        mModes = getResources().getStringArray(R.array.mode_codes);

        mBluetoothSocket = mContext.getBluetoothSocket();
        if (mBluetoothSocket == null) {
            mContext.showNoConnection();
        }

        Anim.greyAllOut(mModePicker);
        return rootView;
    }

    private void sendMode(int i) {
        updateUI(mModePicker.get(i));
        if (mBluetoothSocket == null || !mBluetoothSocket.isConnected()) {
            mContext.showNoConnection();
        } else {
            ConnectedThread connectedThread = new ConnectedThread(mHandler, mBluetoothSocket);
            connectedThread.start();
//            connectedThread.write(mModes[i].getBytes());
            connectedThread.write(String.valueOf("#hi").getBytes());
        }
    }

    private void updateUI(ImageView greyToColor) {
        Anim.setColoured(greyToColor, mColoredView);
        mColoredView = greyToColor;
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ConnectedThread.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    System.out.println(readMessage);
                    break;
            }
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            mContext = (MainActivity) context;
        } else {
            throw new ClassCastException(null);
        }
    }

    @OnClick(R.id.first_container)
    void onFirstClicked() {
        sendMode(0);
    }

    @OnClick(R.id.second_container)
    void onSecondClicked() {
        sendMode(1);
    }

    @OnClick(R.id.third_container)
    void onThirdClicked() {
        sendMode(2);
    }

    @OnClick(R.id.fourth_container)
    void onFourthClicked() {
        sendMode(3);
    }

    @OnClick(R.id.fifth_container)
    void onFifthClicked() {
        sendMode(4);
    }

    @OnClick(R.id.sixth_container)
    void onSixthClicked() {
        sendMode(5);
    }
}
