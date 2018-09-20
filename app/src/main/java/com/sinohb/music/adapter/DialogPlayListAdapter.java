package com.sinohb.music.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.sinohb.common.adapter.recycleview.BaseRecycleViewAdapter;
import com.sinohb.common.adapter.recycleview.BaseViewHolder;
import com.sinohb.music.R;
import com.sinohb.music.play.IMusicPlayManager;
import com.sinohb.music.sdk.entities.Song;
import com.sinohb.music.utils.StringUtils;
import com.sinohb.music.widget.MusicVisualizer;

import java.util.List;

public class DialogPlayListAdapter extends BaseRecycleViewAdapter<Song> {
    private IMusicPlayManager playManager;

    public DialogPlayListAdapter(Context context, List<Song> dataList) {
        super(context, dataList);
        playManager = (IMusicPlayManager) mContext;
    }


    @Override
    public void onBind(BaseViewHolder viewHolder, int position, Song data) {
        TextView textView = viewHolder.getView(R.id.text_item_title);
        textView.setText(data.getTitle());
        TextView artistsView = viewHolder.getView(R.id.text_item_subtitle);
        artistsView.setText(data.getArtist());
        TextView albumView = viewHolder.getView(R.id.text_item_subtitle_2);
        albumView.setText(data.getAlbums());
        MusicVisualizer playView = viewHolder.getView(R.id.visualizer);
        ImageView imageView = viewHolder.getView(R.id.image);
        if (!isScroll()) {
            Glide.with(mContext)
                    .load(StringUtils.getImageUrl(data.getAlbumId()))
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_album_default)
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .error(R.drawable.ic_album_default))
                    .into(imageView);
        }
        if (data.getId() == playManager.getCurrentPlayId()) {
            textView.setTextColor(mContext.getResources().getColor(R.color.yellow));
            if (playManager.isPlaying()) {
                playView.setVisibility(View.VISIBLE);
                playView.setColor(mContext.getResources().getColor(R.color.playing_blue));
            } else {
                playView.setColor(mContext.getResources().getColor(R.color.transparent));
                playView.setVisibility(View.GONE);
            }
        } else {
            textView.setTextColor(mContext.getResources().getColor(R.color.lightWhite));
            playView.setColor(mContext.getResources().getColor(R.color.transparent));
            playView.setVisibility(View.GONE);
        }
        viewHolder.getView(R.id.popup_menu).setOnClickListener(view -> {
            playManager.removePlaySong(data);
        });
    }

    public void notifySongDeleted(int pos) {
       // notifyItemRemoved(pos);
        notifyDataSetChanged();
    }

    @Override
    protected void initRes(BaseViewHolder viewHolder) {
        ImageView imageView = viewHolder.getView(R.id.popup_menu);
        imageView.setImageResource(R.drawable.ic_dialog_song_item_del_selector);
    }

    @Override
    public int getItemLayoutId(int viewType) {
        return R.layout.item_song;
    }
}
