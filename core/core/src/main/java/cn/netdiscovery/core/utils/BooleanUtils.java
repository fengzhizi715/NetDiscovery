package cn.netdiscovery.core.utils;

import com.safframework.tony.common.utils.Preconditions;

/**
 * Created by tony on 2019-01-17.
 */
public class BooleanUtils {

    public static boolean toBoolean(final Boolean bool) {
        return bool != null && bool.booleanValue();
    }

    public static boolean toBoolean(final String str) {

        return Preconditions.isNotBlank(str) && ("true".equals(str)||"TRUE".equals(str));
    }

    public static boolean toBoolean(final String str,boolean defaultValue) {

        if (Preconditions.isNotBlank(str)) {
            if ("true".equals(str)||"TRUE".equals(str)) {
                return Boolean.TRUE;
            } else if ("false".equals(str)||"FALSE".equals(str)){
                return Boolean.TRUE;
            } else {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    public static boolean isTrue(final Boolean bool) {
        return Boolean.TRUE.equals(bool);
    }
}
