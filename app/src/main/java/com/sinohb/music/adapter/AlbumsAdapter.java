package com.sinohb.music.adapter;

import android.content.Context;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.sinohb.common.adapter.recycleview.BaseRecycleViewAdapter;
import com.sinohb.common.adapter.recycleview.BaseViewHolder;
import com.sinohb.music.R;
import com.sinohb.music.play.IMusicPlayManager;
import com.sinohb.music.sdk.entities.Album;
import com.sinohb.music.sdk.tools.InjectionTools;
import com.sinohb.music.utils.Injection;
import com.sinohb.music.utils.PopupWindowUtil;
import com.sinohb.music.utils.StringUtils;
import com.sinohb.music.widget.popup.AlbumItemPopupPresenter;
import com.sinohb.music.widget.popup.ItemMenuContract;
import com.sinohb.music.widget.popup.ItemMenuPopupPresenter;
import com.sinohb.music.widget.popup.ItemMenuPopupWindow;

import java.util.List;

public class AlbumsAdapter extends BaseRecycleViewAdapter<Album> implements ItemMenuContract.View {
    private ItemMenuPopupWindow<Album> window;
    public AlbumsAdapter(Context context, List<Album> dataList) {
        super(context, dataList);
    }

    @Override
    public void onBind(BaseViewHolder viewHolder, int position, Album data) {
        TextView artistNameView = viewHolder.getView(R.id.text_item_title);
        artistNameView.setText(data.title);
        TextView albumView = viewHolder.getView(R.id.text_item_subtitle);
        albumView.setText(data.artistName);
        TextView songCoungView = viewHolder.getView(R.id.text_item_subtitle_2);
        songCoungView.setText(StringUtils.parsString(mContext, R.plurals.Nsongs, data.songCount));
        ImageView imageView = viewHolder.getView(R.id.image);
        if (!isScroll()){
            Glide.with(mContext)
                    .load(StringUtils.getImageUrl(data.id))
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_album_default)
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .error(R.drawable.ic_album_default))
                    .into(imageView);
        }

        viewHolder.getView(R.id.popup_menu).setOnClickListener(view -> {
            ItemMenuPopupPresenter<Album> presenter = new AlbumItemPopupPresenter(InjectionTools.provideMusicDataRespository(mContext),
                    InjectionTools.provideCollectDataRespository(mContext), (IMusicPlayManager) mContext, position);
            String title;
            if (data.songCount > 1) {
                title = String.format(mContext.getString(R.string.delete_songs_content),
                        data.artistName, data.songCount);
            } else {
                title = String.format(mContext.getString(R.string.delete_song_content),
                        data.artistName);
            }
            presenter.takeView(AlbumsAdapter.this);
             window = new ItemMenuPopupWindow<>(mContext, data, title, presenter);
            window.showAtLocation(view, Gravity.TOP | Gravity.START,0, 0);
        });
    }
    public void dissmissWindow() {
        if (window != null && window.isShowing()) {
            window.dismiss();
        }
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
    public void notifySongAdd(int cout) {

    }

    @Override
    public void notifyDeleteFailure(int pos) {

    }
}
