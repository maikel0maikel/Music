package com.sinohb.music.base;


import com.sinohb.music.sdk.entities.Song;

public abstract class BasePlayFragment extends BaseFragment<Song> implements BasePlayView {

    @Override
    public void notifyItemPlaying(int position) {
        if (mAdapter != null) {
            mAdapter.notifyItemChange(position);
            // mAdapter.notifyDataSetChanged();
            if (recyclerView != null) {
                recyclerView.scrollToPosition(position + mAdapter.getHeadAndFootSize());
            }
            //moveToPosition(position+mAdapter.getHeadAndFootSize());
        }

    }


    @Override
    public void notifyItemNormal(int pos) {
        if (mAdapter != null)
            mAdapter.notifyItemChange(pos);
    }

    @Override
    public Song getData(int postion) {
        return mAdapter == null ? null : (Song) mAdapter.getData(postion);
    }

    @Override
    public void showTitle(String title) {

    }

}
