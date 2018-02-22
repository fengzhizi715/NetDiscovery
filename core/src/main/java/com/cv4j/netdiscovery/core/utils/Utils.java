package com.cv4j.netdiscovery.core.utils;

import com.cv4j.proxy.domain.Proxy;
import com.safframework.tony.common.utils.IOUtils;
import com.safframework.tony.common.utils.Preconditions;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
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

    /**
     * 导出
     *
     * @param file csv文件(路径+文件名)，csv文件不存在会自动创建
     * @param dataList 数据
     * @return
     */
    public static boolean exportCsv(File file, List<String> dataList){

        boolean isSucess=false;

        FileOutputStream out=null;
        OutputStreamWriter osw=null;
        BufferedWriter bw=null;
        try {
            out = new FileOutputStream(file);
            osw = new OutputStreamWriter(out);
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
            if(bw!=null){

                IOUtils.closeQuietly(bw);
            }
            if(osw!=null){

                IOUtils.closeQuietly(osw);
            }
            if(out!=null){

                IOUtils.closeQuietly(out);
            }
        }

        return isSucess;
    }
}
