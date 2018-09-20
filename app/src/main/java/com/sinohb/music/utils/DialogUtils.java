package com.sinohb.music.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.sinohb.music.R;

public class DialogUtils {
    private static AlertDialog.Builder getDialog(Context context) {
        return new AlertDialog.Builder(context);
    }

    public static AlertDialog.Builder getAlertDialog(final Context context,
                                                       String title, String content,
                                                       DialogInterface.OnClickListener positiveListener,
                                                       DialogInterface.OnClickListener negativeListener) {
        return getDialog(context)
                .setTitle(title)
                .setMessage(content)
                .setPositiveButton(R.string.delete, positiveListener)
                .setNegativeButton(R.string.cancel, negativeListener);
    }

    public static AlertDialog.Builder getClearAlertDialog(final Context context,
                                                     String title, String content,
                                                     DialogInterface.OnClickListener positiveListener,
                                                     DialogInterface.OnClickListener negativeListener) {
        return getDialog(context)
                .setTitle(title)
                .setMessage(content)
                .setPositiveButton(R.string.sure, positiveListener)
                .setNegativeButton(R.string.cancel, negativeListener);
    }
}
