package com.cv4j.netdiscovery.core.domain;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by tony on 2017/12/22.
 */
public class ResultItems {

    private Map<String, Object> fields = new LinkedHashMap<String, Object>();

    public <T> T get(String key) {

        return fields.get(key) != null ? (T) fields.get(key) : null;
    }

    public Map<String, Object> getAll() {
        return fields;
    }

    public <T> ResultItems put(String key, T value) {
        fields.put(key, value);
        return this;
    }

}
