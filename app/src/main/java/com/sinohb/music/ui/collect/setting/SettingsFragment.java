package com.sinohb.music.ui.collect.setting;

import android.content.Context;
import android.content.Intent;
import android.media.audiofx.AudioEffect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sinohb.music.R;
import com.sinohb.music.play.IMusicPlayManager;


public class SettingsFragment extends Fragment {
    private View rootView;
    public static Fragment newInstance() {
        return new SettingsFragment();
    }
    private Context mContext;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null){
            rootView = inflater.inflate(R.layout.fragment_setting,container,false);
            rootView.findViewById(R.id.layout_equalzer).setOnClickListener(view -> {
                final Intent effects = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
                effects.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, ((IMusicPlayManager)mContext).getAudioSessionId());
                mContext.startActivity(effects);
            });
        }

        return rootView;
    }
}
