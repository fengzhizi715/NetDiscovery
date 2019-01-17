package com.cv4j.netdiscovery.core.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tony on 2019-01-17.
 */
public class SerializableUtils {

    private static Gson gson;

    static {
        gson = new Gson();
    }

    private SerializableUtils() {
        throw new UnsupportedOperationException();
    }

    public static <T> T fromJson(String json, Type type) {

        return gson.fromJson(json,type);
    }

    public static <T> T fromJson(JsonElement jsonElement, Type type) {

        return gson.fromJson(jsonElement,type);
    }

    public static <T> List<T> fromJsonToList(String json, Type type) {
        JsonParser jsonParser = new JsonParser();
        JsonArray jsonArray = jsonParser.parse(json).getAsJsonArray();
        List<T> list = new ArrayList<>();
        jsonArray.forEach(jsonElement -> list.add(SerializableUtils.fromJson(jsonElement, type)));
        return list;
    }

    public static String toJson(Object data){

        return gson.toJson(data);
    }
}
