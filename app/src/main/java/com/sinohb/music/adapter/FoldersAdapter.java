package com.sinohb.music.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sinohb.common.adapter.recycleview.BaseRecycleViewAdapter;
import com.sinohb.common.adapter.recycleview.BaseViewHolder;
import com.sinohb.music.R;
import com.sinohb.music.play.IMusicPlayManager;
import com.sinohb.music.sdk.entities.Album;
import com.sinohb.music.sdk.entities.MusicFolderInfo;
import com.sinohb.music.sdk.tools.InjectionTools;
import com.sinohb.music.utils.Injection;
import com.sinohb.music.utils.PopupWindowUtil;
import com.sinohb.music.utils.StringUtils;
import com.sinohb.music.widget.popup.FolderItemPopupPresenter;
import com.sinohb.music.widget.popup.ItemMenuContract;
import com.sinohb.music.widget.popup.ItemMenuPopupPresenter;
import com.sinohb.music.widget.popup.ItemMenuPopupWindow;

import java.util.List;

public class FoldersAdapter extends BaseRecycleViewAdapter<MusicFolderInfo> implements ItemMenuContract.View {
    private ItemMenuPopupWindow<MusicFolderInfo> window;

    public FoldersAdapter(Context context, List<MusicFolderInfo> dataList) {
        super(context, dataList);
    }

    @Override
    public void onBind(BaseViewHolder viewHolder, int position, MusicFolderInfo data) {

        ImageView icView = viewHolder.getView(R.id.image);
        icView.setImageResource(R.drawable.ic_folder_black_48dp);

        TextView folderNameView = viewHolder.getView(R.id.text_item_title);
        folderNameView.setText(data.folderName);

        TextView songCountView = viewHolder.getView(R.id.text_item_subtitle);
        songCountView.setText(StringUtils.parsString(mContext, R.plurals.Nsongs, data.songCount));

        TextView folderPathView = viewHolder.getView(R.id.text_item_subtitle_2);
        folderPathView.setText(data.folderPath);
        viewHolder.getView(R.id.popup_menu).setOnClickListener(view -> {
            ItemMenuPopupPresenter<MusicFolderInfo> presenter = new FolderItemPopupPresenter(InjectionTools.provideMusicDataRespository(mContext),
                    InjectionTools.provideCollectDataRespository(mContext), (IMusicPlayManager) mContext, position);
            String title = StringUtils.parsString(mContext, R.plurals.NNNdeletefolder, data.songCount);
            presenter.takeView(FoldersAdapter.this);
            window = new ItemMenuPopupWindow<>(mContext, data, title, presenter);
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
