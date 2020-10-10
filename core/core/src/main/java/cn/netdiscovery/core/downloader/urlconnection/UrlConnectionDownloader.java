package cn.netdiscovery.core.downloader.urlconnection;

import cn.netdiscovery.core.cache.RxCacheManager;
import cn.netdiscovery.core.config.Constant;
import cn.netdiscovery.core.cookies.CookiesPool;
import cn.netdiscovery.core.domain.Request;
import cn.netdiscovery.core.domain.Response;
import cn.netdiscovery.core.downloader.Downloader;
import cn.netdiscovery.core.rxjava.transformer.DownloaderDelayTransformer;
import com.safframework.rxcache.domain.Record;
import com.safframework.tony.common.utils.IOUtils;
import com.safframework.tony.common.utils.Preconditions;
import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.functions.Function;
import io.vertx.core.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Created by tony on 2018/3/2.
 */
public class UrlConnectionDownloader implements Downloader {

    private Logger log = LoggerFactory.getLogger(UrlConnectionDownloader.class);

    private HttpURLConnection httpUrlConnection = null;

    @Override
    public Maybe<Response> download(Request request) {

        // request 在 debug 模式下，并且缓存中包含了数据，则使用缓存中的数据
        if (request.getDebug()
                && RxCacheManager.getInstance().getRxCache() != null
                && RxCacheManager.getInstance().getRxCache().get(request.getUrl(), Response.class) != null) {

            Record<Response> response = RxCacheManager.getInstance().getRxCache().get(request.getUrl(), Response.class);
            return Maybe.just(response.getData());
        }

        try {
            URL url = new URL(request.getUrl());

            // 设置Proxy
            if (request.getProxy()!=null) {

                httpUrlConnection = (HttpURLConnection) url.openConnection(request.getProxy().toJavaNetProxy());
            } else {

                httpUrlConnection = (HttpURLConnection) url.openConnection();
            }

            // 使用Post请求时，设置Post body
            if (request.getHttpMethod() == HttpMethod.POST) {

                httpUrlConnection.setDoOutput(true);
                httpUrlConnection.setDoInput(true);
                httpUrlConnection.setRequestMethod("POST");
                httpUrlConnection.setUseCaches(false); // post 请求不用缓存

                if (request.getHttpRequestBody()!=null) {

                    httpUrlConnection.setRequestProperty(Constant.CONTENT_TYPE, request.getHttpRequestBody().getContentType());

                    OutputStream os = httpUrlConnection.getOutputStream();
                    os.write(request.getHttpRequestBody().getBody());
                    os.flush();
                    os.close();
                }
            }

            //设置请求头header
            if (Preconditions.isNotBlank(request.getHeader())) {

                for (Map.Entry<String, String> entry:request.getHeader().entrySet()) {
                    httpUrlConnection.setRequestProperty(entry.getKey(),entry.getValue());
                }
            }

            //设置字符集
            if (Preconditions.isNotBlank(request.getCharset())) {

                httpUrlConnection.setRequestProperty("Accept-Charset", request.getCharset());
            }

            httpUrlConnection.connect();

           return Maybe.create(new MaybeOnSubscribe<InputStream>() {

                @Override
                public void subscribe(MaybeEmitter<InputStream> emitter) throws Exception {

                    emitter.onSuccess(httpUrlConnection.getInputStream());
                }
            })
             .compose(new DownloaderDelayTransformer(request))
             .map(new Function<InputStream, Response>() {

                @Override
                public Response apply(InputStream inputStream) throws Exception {

                    Response response = new Response();
                    response.setContent(IOUtils.readInputStream(inputStream));
                    response.setStatusCode(httpUrlConnection.getResponseCode());
                    response.setContentType(httpUrlConnection.getContentType());

                    if (request.getSaveCookie()) {

                        // save cookies
                        Map<String, List<String>> maps = httpUrlConnection.getHeaderFields();
                        List<String> cookies = maps.get(Constant.SET_COOKIES_HEADER);
                        CookiesPool.getInsatance().saveCookie(request,cookies);
                    }

                    if (request.getDebug()) { // request 在 debug 模式，则缓存response

                        save(request.getUrl(),response);
                    }

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
    public void close() throws IOException {

        if (httpUrlConnection!=null) {

            httpUrlConnection.disconnect();
        }
    }
}
