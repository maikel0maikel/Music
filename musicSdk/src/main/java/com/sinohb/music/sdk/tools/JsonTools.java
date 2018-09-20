package com.sinohb.music.sdk.tools;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.sinohb.logger.LogTools;

import java.util.ArrayList;
import java.util.List;

public class JsonTools {
    private static final String TAG = "JsonTools";

    private JsonTools() {
    }
    private static final Gson gson = new Gson();
    public static <T> T parse(String jsonData, Class<T> type) throws Exception {
        if (jsonData == null || jsonData.length() == 0){
            return null;
        }
        T result = gson.fromJson(jsonData, type);
        return result;
    }

    /**
     * 将Json数组解析成相应的映射对象列表
     *
     * @param jsonData
     * @param cls
     * @param <T>
     * @return
     */
    public static <T> List<T> parseArray(String jsonData, Class<T> cls) {
        List<T> list = new ArrayList<>();
        try {
            JsonArray array = new JsonParser().parse(jsonData).getAsJsonArray();
            for (final JsonElement elem : array) {
                list.add(gson.fromJson(elem, cls));
            }
        } catch (Exception e) {
            LogTools.e(TAG, e, "解析失败：" + jsonData);
        }

        return list;
    }

    /**
     * 将Json数组解析成相应的映射对象列表
     *
     * @param jsonData
     * @param <T>
     * @return
     */
    public static <T> List<T> parseJsonList(String jsonData) {
        List<T> result = gson.fromJson(jsonData, new TypeToken<List<T>>() {
        }.getType());
        return result;
    }

    public static String toJson(Object object) {
        String json = gson.toJson(object);
        return json;
    }

}
