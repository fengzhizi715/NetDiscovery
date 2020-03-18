package cn.netdiscovery.core.downloader.file;

import cn.netdiscovery.core.domain.Request;
import cn.netdiscovery.core.domain.Response;
import cn.netdiscovery.core.downloader.Downloader;
import cn.netdiscovery.core.rxjava.transformer.DownloaderDelayTransformer;
import com.safframework.tony.common.utils.IOUtils;
import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.functions.Function;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 在使用FileDownloader时，可以使用AfterRequest或者Pipeline对文件进行保存等处理。
 * 不建议在Parser中处理文件下载，因为Parser的主要功能是解析html、json等
 * Created by tony on 2018/3/11.
 */
public class FileDownloader implements Downloader {

    private HttpURLConnection httpUrlConnection = null;

    @Override
    public Maybe<Response> download(final Request request) {

        try {
            URL url = new URL(request.getUrl());
            // 将url以open方法返回的urlConnection连接强转为HttpURLConnection连接(标识一个url所引用的远程对象连接)
            // 此时cnnection只是为一个连接对象,待连接中
            httpUrlConnection = (HttpURLConnection) url.openConnection();
            // 设置是否要从 URL连接读取数据,默认为true
            httpUrlConnection.setDoInput(true);
            // 建立连接
            // (请求未开始,直到connection.getInputStream()方法调用时才发起,以上各个参数设置需在此方法之前进行)
            httpUrlConnection.connect();
            // 连接发起请求,处理服务器响应 (从连接获取到输入流)

            return Maybe.create(new MaybeOnSubscribe<InputStream>(){

                @Override
                public void subscribe(MaybeEmitter emitter) throws Exception {

                    emitter.onSuccess(httpUrlConnection.getInputStream());
                }
            })
            .compose(new DownloaderDelayTransformer(request))
            .map(new Function<InputStream, Response>() {
                @Override
                public Response apply(InputStream inputStream) throws Exception {

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    IOUtils.copyStream(inputStream,baos);
                    InputStream is = new ByteArrayInputStream(baos.toByteArray());  // 只针对小的文件使用，大型的文件不建议这样使用

                    Response response = new Response();
                    response.setIs(is);
                    response.setStatusCode(httpUrlConnection.getResponseCode());
                    response.setContentType(httpUrlConnection.getContentType());

                    return response;
                }
            });

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Maybe.empty();
    }

    @Override
    public void close() {

        if (httpUrlConnection!=null) {

            httpUrlConnection.disconnect();
        }
    }

}
