package com.cv4j.netdiscovery.core.utils;

import com.safframework.tony.common.utils.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by tony on 2019-01-15.
 */
public final class PropertyParser {

    private final Properties properties;
    private final Map<String,Object> map;

    public PropertyParser() {

        properties = new Properties();
        map = new HashMap<>();
    }

    public Map<String,Object> decode(InputStream inputStream) throws IOException {

        if (inputStream==null) {
            return null;
        }

        try {
            properties.load(inputStream);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }

        properties.forEach((k,v)->{

            map.put(k.toString(),v);
        });

        return map;
    }
}
