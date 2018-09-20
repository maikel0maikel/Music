package com.sinohb.base;


import io.reactivex.Observable;

public abstract class UseCase<T> {
    public abstract Observable<T> buildObservable();
}
