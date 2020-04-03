package cn.netdiscovery.downloader.httpclient;

import cn.netdiscovery.core.cache.RxCacheManager;
import cn.netdiscovery.core.config.Constant;
import cn.netdiscovery.core.cookies.CookiesPool;
import cn.netdiscovery.core.domain.Request;
import cn.netdiscovery.core.domain.Response;
import cn.netdiscovery.core.downloader.Downloader;
import cn.netdiscovery.core.rxjava.transformer.DownloaderDelayTransformer;
import com.safframework.rxcache.domain.Record;
import com.safframework.tony.common.utils.Preconditions;
import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.functions.Function;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;

/**
 * Created by tony on 2018/1/20.
 */
public class HttpClientDownloader implements Downloader{

    private HttpManager httpManager;

    public HttpClientDownloader() {

        httpManager = HttpManager.get();
    }

    @Override
    public Maybe<Response> download(final Request request) {

        // request 在 debug 模式下，并且缓存中包含了数据，则使用缓存中的数据
        if (request.isDebug()
                && RxCacheManager.getInstance().getRxCache()!=null
                && RxCacheManager.getInstance().getRxCache().get(request.getUrl(),Response.class)!=null) {

            Record<Response> response = RxCacheManager.getInstance().getRxCache().get(request.getUrl(),Response.class);
            return Maybe.just(response.getData());
        }

        return Maybe.create(new MaybeOnSubscribe<CloseableHttpResponse>(){

            @Override
            public void subscribe(MaybeEmitter emitter) throws Exception {

                emitter.onSuccess(httpManager.getResponse(request));
            }
        })
         .compose(new DownloaderDelayTransformer(request))
         .map(new Function<CloseableHttpResponse, Response>() {

            @Override
            public Response apply(CloseableHttpResponse closeableHttpResponse) throws Exception {

                String charset = null;
                if (Preconditions.isNotBlank(request.getCharset())) {
                    charset = request.getCharset();
                } else {
                    charset = Constant.UTF_8;
                }

                String html = EntityUtils.toString(closeableHttpResponse.getEntity(), charset);
                Response response = new Response();
                response.setContent(html.getBytes());
                response.setStatusCode(closeableHttpResponse.getStatusLine().getStatusCode());
                if (closeableHttpResponse.containsHeader(Constant.CONTENT_TYPE)) {
                    response.setContentType(closeableHttpResponse.getFirstHeader(Constant.CONTENT_TYPE).getValue());
                }

                if (request.isSaveCookie()) {

                    // save cookies
                    Header[] headers = closeableHttpResponse.getHeaders(Constant.SET_COOKIES_HEADER);

                    if (Preconditions.isNotBlank(headers)) {

                        for (Header header:headers) {

                            CookiesPool.getInsatance().saveCookie(request,header.getValue());
                        }
                    }
                }

                if (request.isDebug()) { // request 在 debug 模式，则缓存response

                    save(request.getUrl(),response);
                }

                return response;
            }
        });
    }

    @Override
    public void close() {

//        httpManager.close();
    }
}
