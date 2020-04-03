package cn.netdiscovery.downloader.okhttp;

import cn.netdiscovery.core.cache.RxCacheManager;
import cn.netdiscovery.core.config.Constant;
import cn.netdiscovery.core.domain.HttpRequestBody;
import cn.netdiscovery.core.domain.Request;
import cn.netdiscovery.core.domain.Response;
import cn.netdiscovery.core.downloader.Downloader;
import cn.netdiscovery.core.rxjava.transformer.DownloaderDelayTransformer;
import com.cv4j.proxy.domain.Proxy;
import com.safframework.rxcache.domain.Record;
import com.safframework.tony.common.utils.Preconditions;
import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.functions.Function;
import io.vertx.core.http.HttpMethod;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

import java.io.IOException;
import java.util.Map;

/**
 * Created by tony on 2018/1/21.
 */
public class OkHttpDownloader implements Downloader{

    private OkHttpClient client;

    public OkHttpDownloader() {
        client = new OkHttpClient.Builder().retryOnConnectionFailure(true).addInterceptor(new RedirectInterceptor()).build();
    }

    public OkHttpDownloader(Proxy proxy) {

        client = new OkHttpClient.Builder().proxy(proxy.toJavaNetProxy()).retryOnConnectionFailure(true).addInterceptor(new RedirectInterceptor()).build();
    }

    @Override
    public Maybe<Response> download(Request request) {

        // request 在 debug 模式下，并且缓存中包含了数据，则使用缓存中的数据
        if (request.isDebug()
                && RxCacheManager.getInstance().getRxCache()!=null
                && RxCacheManager.getInstance().getRxCache().get(request.getUrl(),Response.class)!=null) {

            Record<Response> response = RxCacheManager.getInstance().getRxCache().get(request.getUrl(),Response.class);
            return Maybe.just(response.getData());
        }

        okhttp3.Request.Builder requestBuilder = null;

        if (request.getHttpMethod() == HttpMethod.GET) {

            requestBuilder = new okhttp3.Request.Builder().url(request.getUrl());
        } else if (request.getHttpMethod() == HttpMethod.POST){

            HttpRequestBody httpRequestBody = request.getHttpRequestBody();

            if (httpRequestBody!=null) {

                MediaType mediaType = MediaType.parse(httpRequestBody.getContentType());

                //创建RequestBody对象，将参数按照指定的MediaType封装
                RequestBody requestBody = RequestBody.create(mediaType,httpRequestBody.getBody());

                requestBuilder = new okhttp3.Request.Builder().url(request.getUrl()).post(requestBody);
            }
        }

        if (request.getHeader()!=null) {

            for (Map.Entry<String, String> entry:request.getHeader().entrySet()) {
                requestBuilder.addHeader(entry.getKey(),entry.getValue());
            }
        }

        // 针对post请求，需要对header添加一些信息
        if (request.getHttpMethod()==HttpMethod.POST
                && Preconditions.isNotBlank(request.getHttpRequestBody())
                && Preconditions.isNotBlank(request.getHttpRequestBody().getContentType())) {

            requestBuilder.addHeader(Constant.CONTENT_TYPE,request.getHttpRequestBody().getContentType());
        }

        okhttp3.Request okrequest = requestBuilder.build();

        return Maybe.create(new MaybeOnSubscribe<okhttp3.Response>(){

            @Override
            public void subscribe(MaybeEmitter emitter) throws Exception {

                emitter.onSuccess(client.newCall(okrequest).execute());
            }
        })
        .compose(new DownloaderDelayTransformer(request))
        .map(new Function<okhttp3.Response, Response>() {

            @Override
            public Response apply(okhttp3.Response resp) throws Exception {

                Response response = new Response();
                response.setContent(resp.body().bytes());
                response.setStatusCode(resp.code());
                response.setContentType(resp.header(Constant.CONTENT_TYPE));

                if (request.isDebug()) { // request 在 debug 模式，则缓存response

                    save(request.getUrl(),response);
                }

                return response;
            }
        });
    }

    @Override
    public void close() {

        try {
            client.dispatcher().executorService().shutdown();   //清除并关闭线程池
            client.connectionPool().evictAll();                 //清除并关闭连接池
            if (client.cache()!=null) {
                client.cache().close();                         //清除cache
            }
        } catch (IOException e) {

        }

    }
}
