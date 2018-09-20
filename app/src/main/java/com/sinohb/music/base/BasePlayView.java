package com.sinohb.music.base;

public interface BasePlayView<T> extends BaseContact.View<T> {
    T getData(int postion);

    void notifyItemPlaying(int pos);

    void notifyItemNormal(int pos);

    void showTitle(String title);
}
