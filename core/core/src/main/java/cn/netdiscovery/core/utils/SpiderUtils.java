package cn.netdiscovery.core.utils;

import com.cv4j.proxy.domain.Proxy;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
public class SpiderUtils {

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

        return contentType != null ? contentType.startsWith("text") : false;
    }

    public static boolean isApplicationJSONType(String contentType) {

        return contentType != null ? contentType.startsWith("application/json") : false;
    }

    public static boolean isApplicationJSONPType(String contentType) {

        return contentType != null ? contentType.startsWith("application/javascript") || contentType.startsWith("application/x-javascript") : false;
    }

    /**
     * 导出csv
     *
     * @param file
     * @param dataList
     * @param charset
     * @return
     */
    public static boolean exportCsv(File file, List<String> dataList, Charset charset) {

        boolean isSucess = false;

        FileOutputStream out = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;
        try {
            out = new FileOutputStream(file);
            osw = new OutputStreamWriter(out, charset);
            bw = new BufferedWriter(osw);
            if (Preconditions.isNotBlank(dataList)) {
                for (String data : dataList) {
                    bw.append(data).append("\r");
                }
            }
            isSucess = true;
        } catch (Exception e) {
            isSucess = false;
        } finally {
            IOUtils.closeQuietly(bw,osw,out);
        }

        return isSucess;
    }

    /**
     * 返回验证码的内容
     *
     * @param imageUrl 验证码的url
     * @return
     */
    public static String getCaptcha(String imageUrl) {

        return getCaptcha(imageUrl, null);
    }

    /**
     * 返回验证码的内容
     *
     * @param imageUrl 验证码的url
     * @param proxy
     * @return
     */
    public static String getCaptcha(String imageUrl, Proxy proxy) {

        try {
            URL url = new URL("http://47.97.7.119:8018/captcha");
            HttpURLConnection httpUrlConnection = null;

            // 设置Proxy
            if (proxy != null) {

                httpUrlConnection = (HttpURLConnection) url.openConnection(proxy.toJavaNetProxy());
            } else {

                httpUrlConnection = (HttpURLConnection) url.openConnection();
            }

            httpUrlConnection.setDoOutput(true);
            httpUrlConnection.setDoInput(true);
            httpUrlConnection.setRequestMethod("POST");
            httpUrlConnection.setUseCaches(false); // post 请求不用缓存

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("imageSource", imageUrl);
            HttpRequestBody body = HttpRequestBody.json(jsonObject);

            httpUrlConnection.setRequestProperty("Content-Type", HttpRequestBody.ContentType.JSON);

            OutputStream os = httpUrlConnection.getOutputStream();
            os.write(body.getBody());
            os.flush();
            os.close();

            httpUrlConnection.connect();

            String response = IOUtils.inputStream2String(httpUrlConnection.getInputStream());

            if (Preconditions.isNotBlank(response)) {
                JsonObject json = new JsonParser().parse(response).getAsJsonObject();
                return json.get("text").getAsString();
            }

            return null;
        } catch (IOException e) {
        }

        return null;
    }
}
