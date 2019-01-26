package com.cv4j.netdiscovery.core.utils;

import com.safframework.tony.common.utils.Preconditions;

/**
 * Created by tony on 2019-01-17.
 */
public class BooleanUtils {

    public static boolean toBoolean(final Boolean bool) {
        return bool != null && bool.booleanValue();
    }

    public static boolean toBoolean(final String str) {

        if (Preconditions.isNotBlank(str)) {
            if (str.equals("true")||str.equals("TRUE")) {
                return Boolean.TRUE;
            }
        }
        
        return false;
    }

    public static boolean isTrue(final Boolean bool) {
        return Boolean.TRUE.equals(bool);
    }
}
