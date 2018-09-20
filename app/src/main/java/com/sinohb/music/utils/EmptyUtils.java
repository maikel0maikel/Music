package com.sinohb.music.utils;

import java.util.List;

public class EmptyUtils {


    public static final boolean isEmpty(String source) {
        return source == null || source.length() == 0;
    }

    public static final boolean isEmpty(List list) {
        return list == null || list.isEmpty();
    }


}
