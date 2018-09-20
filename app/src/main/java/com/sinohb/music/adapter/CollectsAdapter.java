package com.sinohb.music.adapter;

import android.content.Context;
import android.widget.TextView;

import com.sinohb.common.adapter.recycleview.BaseRecycleViewAdapter;
import com.sinohb.common.adapter.recycleview.BaseViewHolder;
import com.sinohb.music.R;
import com.sinohb.music.sdk.entities.Song;

import java.util.List;

public class CollectsAdapter extends BaseRecycleViewAdapter<Song>{

    public CollectsAdapter(Context context, List<Song> dataList) {
        super(context, dataList);
    }

    @Override
    public void onBind(BaseViewHolder viewHolder, int position, Song data) {
        TextView textView = viewHolder.getView(R.id.text_item_title);
        textView.setText(data.getTitle());
        TextView artistsView = viewHolder.getView(R.id.text_item_subtitle);
        artistsView.setText(data.getArtist());
        TextView albumView = viewHolder.getView(R.id.text_item_subtitle_2);
        albumView.setText(data.getAlbums());
    }

    @Override
    public int getItemLayoutId(int viewType) {
        return R.layout.item_song;
    }
}
