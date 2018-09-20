package com.sinohb.music.adapter;

import android.content.Context;
import android.view.Gravity;
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
import com.sinohb.music.sdk.tools.InjectionTools;
import com.sinohb.music.utils.PopupWindowUtil;
import com.sinohb.music.utils.StringUtils;
import com.sinohb.music.widget.MusicVisualizer;
import com.sinohb.music.widget.popup.ItemMenuContract;
import com.sinohb.music.widget.popup.ItemMenuPopupPresenter;
import com.sinohb.music.widget.popup.ItemMenuPopupWindow;
import com.sinohb.music.widget.popup.SongItemPopupPresenter;

import java.util.List;

public class SongsAdapter extends BaseRecycleViewAdapter<Song> implements ItemMenuContract.View {
    private IMusicPlayManager playManager;
    private ItemMenuPopupWindow<Song> window;
    public SongsAdapter(Context context, List<Song> dataList) {
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
        Object tag = imageView.getTag(R.id.tab_album_img);
        if (tag != null && (int) tag != position) {
            Glide.with(mContext).clear(imageView);
        }
        //if (!isScroll()) {
            Glide.with(mContext)
                    .load(StringUtils.getImageUrl(data.getAlbumId()))
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_album_default)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .error(R.drawable.ic_album_default))
                    .into(imageView);
       // }
        imageView.setTag(R.id.tab_album_img, position);
        viewHolder.getView(R.id.popup_menu).setOnClickListener(view -> showPopupWindow(view, position, data));
    }



    protected void showPopupWindow(View view, int position, Song data) {
        ItemMenuPopupPresenter<Song> presenter = new SongItemPopupPresenter(InjectionTools.provideMusicDataRespository(mContext),
                InjectionTools.provideCollectDataRespository(mContext), playManager, position);
        window = new ItemMenuPopupWindow<>(mContext, data, String.format(mContext.getString(R.string.delete_song_content),
                data.getTitle()), presenter);
        presenter.takeView(SongsAdapter.this);
        window.showAtLocation(view, Gravity.TOP | Gravity.START, 0, 0);
    }
    public void dissmissWindow() {
        if (window != null && window.isShowing()) {
            window.dismiss();
        }
    }
    @Override
    public Song getData(int position) {
        return super.getData(position);
    }

    @Override
    public int getItemLayoutId(int viewType) {
        return R.layout.item_song;
    }

    @Override
    public void notifyDeleteSuccess(int pos) {
        removeItem(pos);
    }

    @Override
    public void notifyDeleteFailure(int pos) {

    }

    @Override
    public void notifySongAdd(int cout) {

    }
}
