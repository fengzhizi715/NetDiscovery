package com.cv4j.netdiscovery.core.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cv4j.netdiscovery.core.domain.HttpRequestBody;
import com.cv4j.proxy.domain.Proxy;
import com.safframework.tony.common.utils.IOUtils;
import com.safframework.tony.common.utils.Preconditions;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
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

    /**
     * 返回验证码的内容
     * @param imageUrl 验证码的url
     * @return
     */
    public static String getCaptcha(String imageUrl) {

        return getCaptcha(imageUrl,null);
    }

    /**
     * 返回验证码的内容
     * @param imageUrl 验证码的url
     * @param proxy
     * @return
     */
    public static String getCaptcha(String imageUrl,Proxy proxy) {

        try {
            URL url = new URL("http://47.97.7.119:8018/captcha");
            HttpURLConnection httpUrlConnection = null;

            // 设置Proxy
            if (proxy!=null) {

                httpUrlConnection = (HttpURLConnection) url.openConnection(proxy.toJavaNetProxy());
            } else {

                httpUrlConnection = (HttpURLConnection) url.openConnection();
            }

            httpUrlConnection.setDoOutput(true);
            httpUrlConnection.setDoInput(true);
            httpUrlConnection.setRequestMethod("POST");
            httpUrlConnection.setUseCaches(false); // post 请求不用缓存

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("imageSource",imageUrl);
            HttpRequestBody body = HttpRequestBody.json(jsonObject);

            httpUrlConnection.setRequestProperty("Content-Type", HttpRequestBody.ContentType.JSON);

            OutputStream os = httpUrlConnection.getOutputStream();
            os.write(body.getBody());
            os.flush();
            os.close();

            httpUrlConnection.connect();

            String response = IOUtils.inputStream2String(httpUrlConnection.getInputStream());

            if (Preconditions.isNotBlank(response)) {

                JSONObject json = JSON.parseObject(response);
                return (String) json.get("text");
            }

            return null;
        } catch (IOException e) {
        }

        return null;
    }
}
