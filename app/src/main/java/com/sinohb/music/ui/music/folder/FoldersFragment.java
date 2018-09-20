package com.sinohb.music.ui.music.folder;

import android.content.Context;

import com.sinohb.music.adapter.FoldersAdapter;
import com.sinohb.music.base.BaseShowDetailFragment;
import com.sinohb.music.play.IMusicPlayManager;
import com.sinohb.music.sdk.entities.MusicFolderInfo;
import com.sinohb.music.sdk.tools.InjectionTools;
import com.sinohb.music.utils.Injection;

import java.util.ArrayList;

public class FoldersFragment extends BaseShowDetailFragment<MusicFolderInfo> {
    public static FoldersFragment newInstance() {
        return new FoldersFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mPresenter = new FoldersPresenter(InjectionTools.provideMusicDataRespository(context),
                InjectionTools.provideCollectDataRespository(context), (IMusicPlayManager) context, context);
    }

    @Override
    protected void buildAdapter() {
        mAdapter = new FoldersAdapter(mContext, new ArrayList<>());
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!isVisible() || hidden && mAdapter != null) {
            ((FoldersAdapter) mAdapter).dissmissWindow();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!getUserVisibleHint() && mAdapter != null) {
            ((FoldersAdapter) mAdapter).dissmissWindow();
        }
    }
}
