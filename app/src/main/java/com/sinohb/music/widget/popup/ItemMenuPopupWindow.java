package com.sinohb.music.widget.popup;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.sinohb.music.R;
import com.sinohb.music.utils.DialogUtils;
import com.sinohb.music.utils.PopupWindowUtil;

public class ItemMenuPopupWindow<T> extends PopupWindow implements View.OnClickListener {
    private Context mContext;
    private T data;
    private ItemMenuPopupPresenter<T> presenter;
    private String title;
    private View contentView;
    public ItemMenuPopupWindow(@NonNull Context context, T data, String title,
                               ItemMenuPopupPresenter<T> presenter) {
        super(context);
        this.mContext = context;
        this.data = data;
        this.title = title;
        this.presenter = presenter;
        contentView = LayoutInflater.from(mContext).inflate(R.layout.item_menu, null);
        //contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        // 计算contentView的高宽
       // final int windowHeight = contentView.getMeasuredHeight();
        //final int windowWidth = contentView.getMeasuredWidth();
//        int w = (int) context.getResources().getDimension(R.dimen.popupwindow_content_width);
        final int windowWidth = (int) context.getResources().getDimension(R.dimen.popupwindow_content_width);
        setWidth(windowWidth);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        contentView.findViewById(R.id.add_song_tv).setOnClickListener(this);
        contentView.findViewById(R.id.delete_song_tv).setOnClickListener(this);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable());
        setAnimationStyle(R.style.AnimationPreview);
        setContentView(contentView);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_song_tv:
                presenter.addSongToQueue(data);
                break;
            case R.id.delete_song_tv:
                DialogUtils.getAlertDialog(mContext, mContext.getString(R.string.delete_song),
                        title, (dialogInterface, i) -> {
                            presenter.deleteSong(data);
                            dialogInterface.dismiss();
                        }, (dialogInterface, i) -> dialogInterface.dismiss()).show();
                break;
        }
        dismiss();
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        int windowPos[] = PopupWindowUtil.calculatePopWindowPos(mContext, parent, contentView);
        int xOff = 20;
        windowPos[0] -= xOff;
        super.showAtLocation(parent, gravity,  windowPos[0], windowPos[1]);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (presenter!=null){
            presenter.dropView();
        }
    }
}
