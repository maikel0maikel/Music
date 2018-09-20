package com.sinohb.music.ui.music.songs;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.sinohb.music.R;
import com.sinohb.music.adapter.SongsAdapter;
import com.sinohb.music.base.BasePlayFragment;
import com.sinohb.music.play.IMusicPlayManager;
import com.sinohb.music.sdk.tools.InjectionTools;
import com.sinohb.music.utils.RxViewUtils;

import java.util.ArrayList;

public class SongsFragment extends BasePlayFragment {

    public static SongsFragment newInstance() {
        return new SongsFragment();
    }

    private View header;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mPresenter = new SongsPresenter(InjectionTools.provideMusicDataRespository(context),
                InjectionTools.provideCollectDataRespository(context), (IMusicPlayManager) context, context);
    }

    @Override
    protected void buildAdapter() {
        mAdapter = new SongsAdapter(mContext, new ArrayList<>());
        header = LayoutInflater.from(mContext).inflate(R.layout.item_song_header, null);
        header.setVisibility(View.GONE);
        RxViewUtils.setOnClickListeners((RxViewUtils.Action1<View>) view -> ((SongsContractPresenter) mPresenter).randanPlayAll(),header);
        mAdapter.addHeaderView(header);

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!isVisible() || hidden && mAdapter != null) {
            ((SongsAdapter) mAdapter).dissmissWindow();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!getUserVisibleHint() && mAdapter != null) {
            ((SongsAdapter) mAdapter).dissmissWindow();
        }
    }

    @Override
    public void hideEmptyView() {
        super.hideEmptyView();
        if (header != null) {
            header.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showEmptyView() {
        super.showEmptyView();
        if (header != null) {
            header.setVisibility(View.GONE);
        }
    }
}
