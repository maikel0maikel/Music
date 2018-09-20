package com.sinohb.music.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sinohb.common.adapter.recycleview.BaseRecycleViewAdapter;
import com.sinohb.music.R;
import com.sinohb.music.widget.DividerItemDecoration;
import com.sinohb.music.widget.FastScrollLinearLayoutManager;

import java.util.List;


public abstract class BaseFragment<T> extends Fragment implements BaseContact.View, BaseRecycleViewAdapter.OnItemClickListener {
    protected BaseContact.Presenter mPresenter;
    protected ProgressBar progress_bar;
    protected ViewStub empty_stub;
    protected RecyclerView recyclerView;
    protected BaseRecycleViewAdapter mAdapter;
    protected Context mContext;
    protected View rootView;
    protected TextView text_empty_title;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_recyclerview, container, false);
            recyclerView = rootView.findViewById(R.id.recyclerview);
            progress_bar = rootView.findViewById(R.id.progress_bar);
            empty_stub = rootView.findViewById(R.id.empty_stub);
            buildAdapter();
            recyclerView.setAdapter(mAdapter);
            FastScrollLinearLayoutManager linearLayoutManager = new FastScrollLinearLayoutManager(mContext);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);
            mAdapter.setOnItemClickListener(this);
            // recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST, mAdapter.hasHeader()));
        }
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        mPresenter.startGlide(true);
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        mPresenter.startGlide(true);
                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        mPresenter.startGlide(false);
                        break;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        if (mPresenter != null) {
            mPresenter.takeView(this);
            mPresenter.loadDataSource();
        }
        return rootView;
    }

    protected void moveToPosition(int n) {
        if (recyclerView == null) return;
        LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int firstItem = manager.findFirstVisibleItemPosition();
        int lastItem = manager.findLastVisibleItemPosition();
        if (n <= firstItem) {
            recyclerView.scrollToPosition(n);
        } else if (n <= lastItem) {
            int top = recyclerView.getChildAt(n - firstItem).getTop();
            recyclerView.scrollBy(0, top);
        } else {
            recyclerView.scrollToPosition(n);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onItemClick(View itemView, int pos) {
        mPresenter.onItemClick(mAdapter.getData(pos), pos);
    }

    @Override
    public void onItemLongClick(View itemView, int pos) {

    }

    @Override
    public void showLoding() {
        progress_bar.setVisibility(View.VISIBLE);
    }

    @Override
    public void dismissLoding() {
        progress_bar.setVisibility(View.GONE);
    }

    @Override
    public void showDataSource(List datas) {
        mAdapter.replaceData(datas);
    }

    @Override
    public void showEmptyView() {
        if (empty_stub.getParent() != null) {
            View view = empty_stub.inflate();
            text_empty_title = view.findViewById(R.id.text_empty_title);
            initEmptyTitle();
        }
        empty_stub.setVisibility(View.VISIBLE);
    }

    protected void initEmptyTitle() {

    }

    @Override
    public void hideEmptyView() {
        empty_stub.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (rootView != null && rootView.getParent() != null) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null)
            mPresenter.dropView();
    }

    protected abstract void buildAdapter();
}
