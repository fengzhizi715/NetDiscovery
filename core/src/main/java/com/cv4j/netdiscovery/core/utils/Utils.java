package com.cv4j.netdiscovery.core.utils;

import com.cv4j.proxy.domain.Proxy;
import com.safframework.tony.common.utils.IOUtils;
import com.safframework.tony.common.utils.Preconditions;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.List;

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

    public static boolean isTextType(String contentType) {

        return contentType!=null?contentType.startsWith("text"):false;
    }

    public static boolean isApplicationJSONType(String contentType) {

        return contentType!=null?contentType.startsWith("application/json"):false;
    }

    /**
     * 导出csv
     * @param file
     * @param dataList
     * @param charset
     * @return
     */
    public static boolean exportCsv(File file, List<String> dataList, Charset charset){

        boolean isSucess=false;

        FileOutputStream out=null;
        OutputStreamWriter osw=null;
        BufferedWriter bw=null;
        try {
            out = new FileOutputStream(file);
            osw = new OutputStreamWriter(out, charset);
            bw =new BufferedWriter(osw);
            if(Preconditions.isNotBlank(dataList)){
                for(String data : dataList){
                    bw.append(data).append("\r");
                }
            }
            isSucess=true;
        } catch (Exception e) {
            isSucess=false;
        }finally{
            IOUtils.closeQuietly(bw);
            IOUtils.closeQuietly(osw);
            IOUtils.closeQuietly(out);
        }

        return isSucess;
    }

    public static ByteArrayOutputStream cloneInputStream(InputStream input) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = input.read(buffer)) > -1) {
                baos.write(buffer, 0, len);
            }
            baos.flush();
            return baos;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
