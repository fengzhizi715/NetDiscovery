package com.cv4j.netdiscovery.core.utils;

import com.cv4j.proxy.domain.Proxy;
import com.safframework.tony.common.utils.IOUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by tony on 2018/1/9.
 */
public class Utils {

    public static boolean checkProxy(Proxy proxy) {

        if (proxy == null) return false;

        Socket socket = null;
        try {
            socket = new Socket();
            InetSocketAddress endpointSocketAddr = new InetSocketAddress(proxy.getIp(), proxy.getPort());
            socket.connect(endpointSocketAddr, 3000);
            return true;
        } catch (IOException e) {
            return false;
        } finally {

            IOUtils.closeQuietly(socket);
        }
    }

}
