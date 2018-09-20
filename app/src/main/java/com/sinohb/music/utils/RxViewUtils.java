package com.sinohb.music.utils;

import android.os.Looper;
import android.support.annotation.CheckResult;
import android.view.View;
import android.widget.SeekBar;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

public class RxViewUtils {

    private RxViewUtils() {
    }

    /**
     * 防止重复点击
     *
     * @param target 目标view
     * @param action 监听器
     */
    public static void setOnClickListeners(final Action1 action, @NonNull View... target) {
        for (View view : target) {
            RxViewUtils.onClick(view).throttleFirst(500, TimeUnit.MILLISECONDS).subscribe((Consumer<View>) action::onClick);
        }
    }

    /**
     * 监听onclick事件防抖动
     *
     * @param view
     * @return
     */
    @CheckResult
    @NonNull
    private static Observable onClick(@NonNull View view) {
        return Observable.create(new ViewClickOnSubscribe(view));
    }

    /**
     * onclick事件防抖动
     * 返回view
     */
    private static class ViewClickOnSubscribe implements ObservableOnSubscribe {
        private View view;

        public ViewClickOnSubscribe(View view) {
            this.view = view;
        }

        @Override
        public void subscribe(@io.reactivex.annotations.NonNull final ObservableEmitter e) throws Exception {
            if (Looper.getMainLooper() != Looper.myLooper()) {
                throw new IllegalStateException(
                        "Must be called from the main thread. Was: " + Thread.currentThread());
            }

            View.OnClickListener listener = v -> {
                if (!e.isDisposed()) {
                    e.onNext(view);
                }
            };
            view.setOnClickListener(listener);
        }
    }


    /**
     * 防止重复点击
     *
     * @param target 目标view
     * @param action 监听器
     */
    public static void setOnSeekListeners(final Action2 action, @NonNull SeekBar... target) {
        for (SeekBar view : target) {
            onSeek(view).throttleFirst(500, TimeUnit.MILLISECONDS).subscribe(new Consumer<SeekBarT>() {
                @Override
                public void accept(SeekBarT o) throws Exception {
                    switch (o.methodId) {
                        case SeekBarT.METHOD_PROGRESS_CHANGED:
                            action.onProgressChanged(o.seekBar,o.progress,o.b);
                            break;
                        case SeekBarT.METHOD_START_TRACKING_TOUCH:
                            action.onStartTrackingTouch(o.seekBar);
                            break;
                        case SeekBarT.METHOD_STOP_TRACKING_TOUCH:
                            action.onStopTrackingTouch(o.seekBar);
                            break;
                    }
                }
            });
        }
    }
    /**
     * 监听onclick事件防抖动
     *
     * @param view
     * @return
     */
    @CheckResult
    @NonNull
    private static Observable onSeek(@NonNull SeekBar view) {
        return Observable.create(new ViewSeekBarOnSubscribe(view));
    }
    /**
     * seekBar事件防抖动
     * 返回view
     */
    private static class ViewSeekBarOnSubscribe implements ObservableOnSubscribe {
        private SeekBar view;

        public ViewSeekBarOnSubscribe(SeekBar view) {
            this.view = view;
        }

        @Override
        public void subscribe(@io.reactivex.annotations.NonNull final ObservableEmitter e) throws Exception {
            if (Looper.getMainLooper() != Looper.myLooper()) {
                throw new IllegalStateException(
                        "Must be called from the main thread. Was: " + Thread.currentThread());
            }

            SeekBar.OnSeekBarChangeListener listener = new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//                    SeekBarT seekBarT = new SeekBarT();
//                    seekBarT.methodId = SeekBarT.METHOD_PROGRESS_CHANGED;
//                    seekBarT.seekBar = seekBar;
//                    seekBarT.b = b;
//                    seekBarT.progress = i;
//                    e.onNext(seekBarT);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
//                    SeekBarT seekBarT = new SeekBarT();
//                    seekBarT.methodId = SeekBarT.METHOD_START_TRACKING_TOUCH;
//                    seekBarT.seekBar = seekBar;
//                    e.onNext(seekBarT);
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    SeekBarT seekBarT = new SeekBarT();
                    seekBarT.methodId = SeekBarT.METHOD_STOP_TRACKING_TOUCH;
                    seekBarT.seekBar = seekBar;
                    e.onNext(seekBarT);
                }
            };
            view.setOnSeekBarChangeListener(listener);
        }
    }

    public static class SeekBarT {
        static final int METHOD_START_TRACKING_TOUCH = 1;
        static final int METHOD_STOP_TRACKING_TOUCH = 2;
        static final int METHOD_PROGRESS_CHANGED = 3;
        SeekBar seekBar;
        int progress;
        int methodId;
        boolean b;
    }

    /**
     * A one-argument action. 点击事件转发接口
     */
    public interface Action1<T> {
        void onClick(T t);
    }

    public interface Action2<T> {
        void onProgressChanged(T seekBar, int i, boolean b);

        void onStartTrackingTouch(T seekBar);

        void onStopTrackingTouch(T seekBar);
    }
}
