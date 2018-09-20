package com.sinohb.music.ui.music.artists;

import android.content.Context;

import com.sinohb.music.adapter.ArtistsAdapter;
import com.sinohb.music.base.BaseFragment;
import com.sinohb.music.base.BaseShowDetailFragment;
import com.sinohb.music.play.IMusicPlayManager;
import com.sinohb.music.sdk.entities.Artist;
import com.sinohb.music.sdk.tools.InjectionTools;
import com.sinohb.music.utils.Injection;

import java.util.ArrayList;

public class ArtistsFragment extends BaseShowDetailFragment<Artist> {
    public static ArtistsFragment newInstance() {
        return new ArtistsFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mPresenter = new ArtistsPresenter(InjectionTools.provideMusicDataRespository(context),
                InjectionTools.provideCollectDataRespository(context), (IMusicPlayManager) context,context);
    }

    @Override
    protected void buildAdapter() {
        mAdapter = new ArtistsAdapter(mContext,new ArrayList<>());
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!isVisible() || hidden && mAdapter != null) {
            ((ArtistsAdapter) mAdapter).dissmissWindow();
        }
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!getUserVisibleHint() && mAdapter != null) {
            ((ArtistsAdapter) mAdapter).dissmissWindow();
        }
    }
}
