package com.cv4j.netdiscovery.core.utils;

import com.google.gson.Gson;

import java.lang.reflect.Type;

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

    public static String toJson(Object data){

        return gson.toJson(data);
    }
}
