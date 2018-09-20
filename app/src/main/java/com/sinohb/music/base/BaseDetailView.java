package com.sinohb.music.base;


public interface BaseDetailView<T> extends BaseContact.View<T>{
    void showDetial(int type,String title,Object value);
}
