package com.sinohb.music.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.View;

import com.sinohb.music.R;
import com.sinohb.music.play.IMusicPlayManager;
import com.sinohb.music.sdk.entities.Song;
import com.sinohb.music.sdk.tools.InjectionTools;
import com.sinohb.music.utils.Injection;
import com.sinohb.music.utils.PopupWindowUtil;
import com.sinohb.music.widget.popup.DetailItemPopupPresenter;
import com.sinohb.music.widget.popup.ItemMenuPopupPresenter;
import com.sinohb.music.widget.popup.ItemMenuPopupWindow;

import java.util.List;

public class DetailSongsAdapter extends SongsAdapter {
    public DetailSongsAdapter(Context context, List<Song> dataList) {
        super(context, dataList);
    }

    @Override
    protected void showPopupWindow(View view, int position, Song data) {
        ItemMenuPopupPresenter<Song> presenter = new DetailItemPopupPresenter(InjectionTools.provideMusicDataRespository(mContext),
                InjectionTools.provideCollectDataRespository(mContext), (IMusicPlayManager) mContext, position);
        ItemMenuPopupWindow<Song> window = new ItemMenuPopupWindow<>(mContext, data, String.format(mContext.getString(R.string.delete_song_content),
                data.getTitle()), presenter);
        presenter.takeView(DetailSongsAdapter.this);
        int windowPos[] = PopupWindowUtil.calculatePopWindowPos(mContext, view, window.getContentView());
        int xOff = 20;
        windowPos[0] -= xOff;
        window.showAtLocation(view, Gravity.TOP | Gravity.START, windowPos[0], windowPos[1]);
    }
}
