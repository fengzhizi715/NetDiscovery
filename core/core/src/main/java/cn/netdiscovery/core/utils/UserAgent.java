package cn.netdiscovery.core.utils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by tony on 2018/2/2.
 */
public final class UserAgent {

    public static List<String> uas = new CopyOnWriteArrayList<>();

    public static String getUserAgent() {

        if (uas.size()>0) {

            int index=(int)(Math.random() * uas.size());
            return uas.get(index);
        }

        return null;
    }
}
