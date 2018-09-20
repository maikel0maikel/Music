package com.sinohb.music.sdk.service;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;
import android.view.KeyEvent;

public class MediaHandler implements AudioManager.OnAudioFocusChangeListener {
    private Context mContext;
    private AudioManager mAudioManager;
    private AudioListener audioListener;
    private boolean hasRegist = false;

    public MediaHandler(Context context) {
        mContext = context.getApplicationContext();
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
    }

    public void setAudioListener(AudioListener listener) {
        this.audioListener = listener;
    }

    public int requestAudioFocus() {
        return mAudioManager == null ? -1 : mAudioManager.requestAudioFocus(this,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }

    public void abandonAudioFocus() {
        if (mAudioManager != null)
            mAudioManager.abandonAudioFocus(this);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_LOSS:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                unregisterMediaKey();
                abandonAudioFocus();
                if (audioListener != null) audioListener.notifyPause();
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                registerMediaKey();
                if (audioListener != null) audioListener.notifyPause();
                break;
            case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
            case AudioManager.AUDIOFOCUS_GAIN:
                if (audioListener != null) audioListener.notifyResume();
                registerMediaKey();
                break;
            default:
        }
    }

    class MyMediaButtonEventReceiver extends BroadcastReceiver {
        private String Tag = "MediaKey";

        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
                Log.i(Tag, "ACTION_MEDIA_BUTTON!");
                KeyEvent keyEvent = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
                switch (keyEvent.getKeyCode()) {
                    case KeyEvent.KEYCODE_HEADSETHOOK:
                        break;

                    case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                        break;

                    case KeyEvent.KEYCODE_MEDIA_PLAY:
                        if (keyEvent.getAction() == KeyEvent.ACTION_UP && audioListener != null) {
                            audioListener.notifyResume();
                        }
                        break;
                    case KeyEvent.KEYCODE_MEDIA_PAUSE:
                        if (keyEvent.getAction() == KeyEvent.ACTION_UP && audioListener != null) {
                            audioListener.notifyPause();
                        }
                        break;

                    case KeyEvent.KEYCODE_MEDIA_STOP:
                        break;

                    case KeyEvent.KEYCODE_MEDIA_NEXT:
                        if (keyEvent.getAction() == KeyEvent.ACTION_UP && audioListener != null) {
                            audioListener.notifyPlayNext();
                        }
                        break;

                    case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                        if (keyEvent.getAction() == KeyEvent.ACTION_UP && audioListener != null) {
                            audioListener.notifyPlayPre();
                        }
                        break;
                }
            }
        }
    }

    private void registerMediaKey() {
        if (mAudioManager != null && !hasRegist) {
            Log.i("MediaKey", "registerMediaKey isAudioFocusinMusic:");
            ComponentName name = new ComponentName(mContext.getPackageName(), MyMediaButtonEventReceiver.class.getName());
            mAudioManager.registerMediaButtonEventReceiver(name);
            hasRegist = true;
        }
    }

    private void unregisterMediaKey() {
        if (mAudioManager != null && hasRegist) {
            Log.i("MediaKey", "unregisterMediaKey isAudioFocusinMusic:");
            ComponentName name = new ComponentName(mContext.getPackageName(), MyMediaButtonEventReceiver.class.getName());
            mAudioManager.unregisterMediaButtonEventReceiver(name);
            hasRegist = false;
        }
    }

    public void destroy() {
        audioListener = null;
        unregisterMediaKey();
    }

    interface AudioListener {
        void notifyPause();

        void notifyResume();

        void notifyPlayNext();

        void notifyPlayPre();
    }

}
