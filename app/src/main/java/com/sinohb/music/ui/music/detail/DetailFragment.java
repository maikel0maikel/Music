package com.sinohb.music.ui.music.detail;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sinohb.music.R;
import com.sinohb.music.adapter.DetailSongsAdapter;
import com.sinohb.music.base.BasePlayFragment;
import com.sinohb.music.play.IMusicPlayManager;
import com.sinohb.music.sdk.tools.InjectionTools;
import com.sinohb.music.utils.Constants;
import com.sinohb.music.utils.Injection;
import com.sinohb.music.widget.DividerItemDecoration;

import java.util.ArrayList;

public class DetailFragment extends BasePlayFragment {
    private TextView title_detail_tv;

    public static DetailFragment newInstance(int type, String name, Object arg) {
        Bundle args = new Bundle();
        args.putInt(Constants.KEY_EXTRA_TYPE, type);
        args.putString(Constants.KEY_EXTRA_TITLE, name);
        Constants.bundleValues(args, Constants.KEY_EXTRA_VALUE, arg);
        DetailFragment detailFragment = new DetailFragment();
        detailFragment.setArguments(args);
        return detailFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mPresenter = new DetailPresenter(InjectionTools.provideMusicDataRespository(context),
                InjectionTools.provideCollectDataRespository(context), (IMusicPlayManager) context,context, getArguments());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null){
            rootView = inflater.inflate(R.layout.fragment_detail,container,false);
            recyclerView = rootView.findViewById(R.id.recyclerview);
            progress_bar = rootView.findViewById(R.id.progress_bar);
            empty_stub = rootView.findViewById(R.id.empty_stub);
            title_detail_tv = rootView.findViewById(R.id.title_detail_tv);
            buildAdapter();
            recyclerView.setAdapter(mAdapter);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);
            mAdapter.setOnItemClickListener(this);
            ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
            //recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST, false));
        }
        if (mPresenter != null) {
            mPresenter.takeView(this);
            mPresenter.loadDataSource();
        }
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mPresenter != null) {
            mPresenter.takeView(this);
            mPresenter.loadDataSource();
        }
    }

    @Override
    protected void buildAdapter() {
        mAdapter = new DetailSongsAdapter(mContext, new ArrayList<>());
    }

    @Override
    public void showTitle(String title) {
        if (title_detail_tv != null) {
            title_detail_tv.setText(title);
        }
    }
}
