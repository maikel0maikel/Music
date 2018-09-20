package com.sinohb.base;

public interface BasePresenter<T extends BaseView> {

    void takeView(T view);

    void dropView();
}
