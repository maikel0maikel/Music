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
import com.sinohb.music.sdk.entities.Artist;
import com.sinohb.music.sdk.entities.Song;
import com.sinohb.music.sdk.tools.InjectionTools;
import com.sinohb.music.utils.Injection;
import com.sinohb.music.utils.PopupWindowUtil;
import com.sinohb.music.utils.StringUtils;
import com.sinohb.music.widget.popup.ArtistsItemPopupPresenter;
import com.sinohb.music.widget.popup.ItemMenuContract;
import com.sinohb.music.widget.popup.ItemMenuPopupPresenter;
import com.sinohb.music.widget.popup.ItemMenuPopupWindow;
import com.sinohb.music.widget.popup.SongItemPopupPresenter;

import java.util.List;

public class ArtistsAdapter extends BaseRecycleViewAdapter<Artist> implements ItemMenuContract.View{
    private ItemMenuPopupWindow<Artist> window;
    public ArtistsAdapter(Context context, List<Artist> dataList) {
        super(context, dataList);
    }

    @Override
    public void onBind(BaseViewHolder viewHolder, int position, Artist data) {
        TextView artistNameView = viewHolder.getView(R.id.text_item_title);
        artistNameView.setText(data.name);
        TextView albumView = viewHolder.getView(R.id.text_item_subtitle);
        albumView.setText(StringUtils.parsString(mContext, R.plurals.Nalbums, data.albumCount));
        TextView songCoungView = viewHolder.getView(R.id.text_item_subtitle_2);
        songCoungView.setText(StringUtils.parsString(mContext, R.plurals.Nsongs, data.songCount));
        ImageView imageView = viewHolder.getView(R.id.image);
        if (!isScroll()) {
            Glide.with(mContext)
                    .load(StringUtils.getImageUrl(data.id))
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_singer_default)
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .error(R.drawable.ic_singer_default))
                    .into(imageView);
        }
        viewHolder.getView(R.id.popup_menu).setOnClickListener(view -> {
            ItemMenuPopupPresenter<Artist> presenter = new ArtistsItemPopupPresenter(InjectionTools.provideMusicDataRespository(mContext),
                    InjectionTools.provideCollectDataRespository(mContext), (IMusicPlayManager) mContext,position);
            String title = "";
            if (data.songCount>1){
                title = String.format(mContext.getString(R.string.delete_songs_content),
                        data.name,data.songCount) ;
            }else {
                title = String.format(mContext.getString(R.string.delete_song_content),
                        data.name);
            }
            presenter.takeView(ArtistsAdapter.this);
            window = new ItemMenuPopupWindow<>(mContext,data,title,presenter);
            window.showAtLocation(view, Gravity.TOP | Gravity.START, 0, 0);
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
    public void notifyDeleteFailure(int pos) {

    }

    @Override
    public void notifySongAdd(int cout) {

    }
}
