package com.sinohb.music.utils;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;

public class StringUtils {
    private StringUtils(){}

    public static String parsString(final Context context, final int pluralInt,
                                    final int number){
        return context.getResources().getQuantityString(pluralInt, number, number);
    }

    public static String getImageUrl(long id){
        return ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), id).toString();
    }
}
