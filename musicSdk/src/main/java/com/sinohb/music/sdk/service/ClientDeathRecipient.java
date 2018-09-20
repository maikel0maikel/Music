package com.sinohb.music.sdk.service;

import android.os.IBinder;

public class ClientDeathRecipient implements IBinder.DeathRecipient {
    private ClientDeathListener mListener;

    public ClientDeathRecipient(ClientDeathListener listener) {
        mListener = listener;
    }

    @Override
    public void binderDied() {
        if (mListener != null) mListener.onDied();
    }

    interface ClientDeathListener {
        void onDied();
    }
}
