package com.sinohb.music.utils;

import android.os.Bundle;

public class Constants {

    public static final String KEY_EXTRA_TYPE = "extra.type";
    public static final String KEY_EXTRA_VALUE = "extra.value";
    public static final String KEY_EXTRA_TITLE = "extra.title";
    public static final int TYPE_ARTISTS = 1;
    public static final int TYPE_ALBUM = 2;
    public static final int TYPE_FOLDER = 3;


    public static Bundle bundleValues(String key, Object value) {
        Bundle bundle = new Bundle();
        if (value instanceof Integer) {
            bundle.putInt(key, (Integer) value);
        } else if (value instanceof Long) {
            bundle.putLong(key, (Long) value);
        } else if (value instanceof String) {
            bundle.putString(key, (String) value);
        } else if (value instanceof Boolean) {
            bundle.putBoolean(key, (Boolean) value);
        }
        return bundle;
    }

    public static Bundle bundleValues(Bundle bundle,String key, Object value) {
        if (value instanceof Integer) {
            bundle.putInt(key, (Integer) value);
        } else if (value instanceof Long) {
            bundle.putLong(key, (Long) value);
        } else if (value instanceof String) {
            bundle.putString(key, (String) value);
        } else if (value instanceof Boolean) {
            bundle.putBoolean(key, (Boolean) value);
        }
        return bundle;
    }

    public static String durationToTimeString(long duration) {
        StringBuffer sb = new StringBuffer();
        long m = duration / (60 * 1000);
        sb.append(m < 10 ? "0" + m : m);
        sb.append(":");
        long s = (duration % (60 * 1000)) / 1000;
        sb.append(s < 10 ? "0" + s : s);
        return sb.toString();
    }
}
