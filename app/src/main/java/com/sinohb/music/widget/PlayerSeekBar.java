package com.sinohb.music.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.sinohb.music.R;

/**
 * Created by bernie on 2017/4/10.
 */

public class PlayerSeekBar extends AppCompatSeekBar {
    private Drawable mDrawable;
    private boolean mDrawLoading = false;
    private int mDegree = 0;
    private Matrix mMatrix = new Matrix();
    private Bitmap mLoading = BitmapFactory.decodeResource(getResources(), R.drawable.ic_play_plybar_btn_loading);
    private boolean canTouch = false;

    public PlayerSeekBar(Context context) {
        super(context);
    }

    public PlayerSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        setThumb(getContext().getResources().getDrawable(R.drawable.ic_play_plybar_btn));
    }

    public void setLoading(boolean loading) {
        if (loading) {
            mDrawLoading = true;
            invalidate();
        } else {
            mDrawLoading = false;
        }
    }


    @Override
    public void setThumb(Drawable thumb) {
        Rect localRect = null;
        if (mDrawable != null) {
            localRect = mDrawable.getBounds();
        }
        super.setThumb(mDrawable);
        mDrawable = thumb;
        if ((localRect != null) && (mDrawable != null)) {
            mDrawable.setBounds(localRect);
        }
    }

    @Override
    public Drawable getThumb() {
        if (Build.VERSION.SDK_INT >= 16) {
            return super.getThumb();
        }
        return mDrawable;
    }

    protected synchronized void onDraw(Canvas canvas) {

        super.onDraw(canvas);
        if (mDrawLoading) {
            canvas.save();
            mDegree = ((int) (mDegree + 3.0F));
            mDegree %= 360;
            mMatrix.reset();
            mMatrix.postRotate(mDegree, mLoading.getWidth() / 2, mLoading.getHeight() / 2);
            canvas.translate(getPaddingLeft() + getThumb().getBounds().left + mDrawable.getIntrinsicWidth() / 2 - mLoading.getWidth() / 2 - getThumbOffset(), getPaddingTop() + getThumb().getBounds().top + mDrawable.getIntrinsicHeight() / 2 - mLoading.getHeight() / 2);

        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        return canTouch && super.onTouchEvent(event);
    }

    public void setCanTouch(boolean canTouch) {
        this.canTouch = canTouch;
    }
    public boolean isCanTouch(){
        return canTouch;
    }
}
