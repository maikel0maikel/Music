package com.sinohb.base.tools;


import android.content.Context;
import android.widget.Toast;


public class ToastTools {

    private static Toast mToast = null;//全局唯一的Toast

    private ToastTools(){}

    public static void showShort(Context context, int resId) {
        if (mToast == null) {
            mToast = Toast.makeText(context, resId, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(resId);
        }
        mToast.show();
    }

    public static void showShorts(Context context, String resId) {
        if (mToast == null) {
            mToast = Toast.makeText(context, resId, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(resId);
        }
        mToast.show();
    }


}
