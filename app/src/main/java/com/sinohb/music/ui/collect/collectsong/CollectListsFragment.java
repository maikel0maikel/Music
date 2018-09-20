package com.sinohb.music.ui.collect.collectsong;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.sinohb.music.R;
import com.sinohb.music.adapter.SongsAdapter;
import com.sinohb.music.base.BasePlayFragment;
import com.sinohb.music.play.IMusicPlayManager;
import com.sinohb.music.sdk.tools.InjectionTools;
import com.sinohb.music.utils.Injection;

import java.util.ArrayList;

public class CollectListsFragment extends BasePlayFragment {

    public static Fragment newInstance() {
        return new CollectListsFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mPresenter = new CollectsPresenter(InjectionTools.provideMusicDataRespository(context),
                InjectionTools.provideCollectDataRespository(context), (IMusicPlayManager) context,context);
    }

    @Override
    protected void buildAdapter() {
        mAdapter = new SongsAdapter(mContext,new ArrayList<>());
    }

    @Override
    protected void initEmptyTitle() {
        if (text_empty_title!=null)text_empty_title.setText(R.string.favorite_empty_default);
    }
}
