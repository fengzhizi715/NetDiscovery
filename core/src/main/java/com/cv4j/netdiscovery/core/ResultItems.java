package com.cv4j.netdiscovery.core;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by tony on 2017/12/22.
 */
public class ResultItems {

    private Map<String, Object> fields = new LinkedHashMap<String, Object>();

    public <T> T get(String key) {
        Object o = fields.get(key);

        if (o == null) {
            return null;
        }
        return (T) fields.get(key);
    }

    public Map<String, Object> getAll() {
        return fields;
    }

    public <T> ResultItems put(String key, T value) {
        fields.put(key, value);
        return this;
    }

}
