package com.cv4j.netdiscovery.core.config;

import com.cv4j.netdiscovery.core.utils.PropertyParser;
import com.cv4j.netdiscovery.core.utils.YamlParser;
import com.safframework.tony.common.utils.Preconditions;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by tony on 2019-01-15.
 */
public class Configuration {

    private static YamlParser yamlParser;
    private static PropertyParser propertyParser;
    private static Map<String,Object> configs = new ConcurrentHashMap<>();

    static {

        yamlParser = new YamlParser(".");
        try {
            Map<String,Object> yaml = yamlParser.decode(Configuration.class.getResourceAsStream("/application.yaml"));
            if(Preconditions.isNotBlank(yaml)) {
                configs.putAll(yaml);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        propertyParser = new PropertyParser();
        try {
            Map<String,Object> property = propertyParser.decode(Configuration.class.getResourceAsStream("/application.properties"));
            if (Preconditions.isNotBlank(property)) {
                configs.putAll(property);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Configuration() {
        throw new UnsupportedOperationException();
    }

    public static Set<String> keys() {

        return configs.keySet();
    }

    public static Object getConfig(String key) {

        return configs.get(key);
    }

    public static <T> T getConfig(String key, Class<T> clazz) {

        return (T)configs.get(key);
    }
}