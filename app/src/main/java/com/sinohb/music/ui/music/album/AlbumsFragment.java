package com.sinohb.music.ui.music.album;

import android.content.Context;

import com.sinohb.music.adapter.AlbumsAdapter;
import com.sinohb.music.base.BaseShowDetailFragment;
import com.sinohb.music.play.IMusicPlayManager;
import com.sinohb.music.sdk.entities.Album;
import com.sinohb.music.sdk.tools.InjectionTools;
import com.sinohb.music.utils.Injection;

import java.util.ArrayList;

public class AlbumsFragment extends BaseShowDetailFragment<Album> {

    public static AlbumsFragment newInstance() {
        return new AlbumsFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mPresenter = new AlbumsPresenter(InjectionTools.provideMusicDataRespository(context),
                InjectionTools.provideCollectDataRespository(context), (IMusicPlayManager) context,context);
    }

    @Override
    protected void buildAdapter() {
        mAdapter = new AlbumsAdapter(mContext, new ArrayList<>());
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!isVisible() || hidden && mAdapter != null) {
            ((AlbumsAdapter) mAdapter).dissmissWindow();
        }
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!getUserVisibleHint() && mAdapter != null) {
            ((AlbumsAdapter) mAdapter).dissmissWindow();
        }
    }
}
