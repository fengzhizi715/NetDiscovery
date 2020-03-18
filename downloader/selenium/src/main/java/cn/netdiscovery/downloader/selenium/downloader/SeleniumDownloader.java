package cn.netdiscovery.downloader.selenium.downloader;

import cn.netdiscovery.core.config.Constant;
import cn.netdiscovery.core.domain.Request;
import cn.netdiscovery.core.domain.Response;
import cn.netdiscovery.core.downloader.Downloader;
import cn.netdiscovery.core.rxjava.transformer.DownloaderDelayTransformer;
import cn.netdiscovery.downloader.selenium.action.SeleniumAction;
import cn.netdiscovery.downloader.selenium.pool.WebDriverPool;
import com.safframework.tony.common.utils.Preconditions;
import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.functions.Function;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by tony on 2018/1/28.
 */
public class SeleniumDownloader implements Downloader {

    private WebDriver webDriver;
    private List<SeleniumAction> actions = new LinkedList<>();

    public SeleniumDownloader() {

        this.webDriver = WebDriverPool.borrowOne(); // 从连接池中获取webDriver
    }

    public SeleniumDownloader(SeleniumAction action) {

        this.webDriver = WebDriverPool.borrowOne(); // 从连接池中获取webDriver
        this.actions.add(action);
    }

    public SeleniumDownloader(List<SeleniumAction> actions) {

        this.webDriver = WebDriverPool.borrowOne(); // 从连接池中获取webDriver
        this.actions.addAll(actions);
    }

    @Override
    public Maybe<Response> download(Request request) {

        return Maybe.create(new MaybeOnSubscribe<String>(){

            @Override
            public void subscribe(MaybeEmitter emitter) throws Exception {

                if (webDriver!=null) {
                    webDriver.get(request.getUrl());

                    if (Preconditions.isNotBlank(actions)) {

                        actions.forEach(
                                action-> action.perform(webDriver)
                        );
                    }

                    emitter.onSuccess(webDriver.getPageSource());
                }
            }
        })
        .compose(new DownloaderDelayTransformer(request))
        .map(new Function<String, Response>() {

            @Override
            public Response apply(String html) throws Exception {

                Response response = new Response();
                response.setContent(html.getBytes());
                response.setStatusCode(Constant.OK_STATUS_CODE);
                response.setContentType(getContentType(webDriver));
                return response;
            }
        });
    }

    /**
     * @param webDriver
     * @return
     */
    private String getContentType(final WebDriver webDriver) {

        if (webDriver instanceof JavascriptExecutor) {

            final JavascriptExecutor jsExecutor = (JavascriptExecutor) webDriver;
            // TODO document.contentType does not exist.
            final Object ret = jsExecutor
                    .executeScript("return document.contentType;");
            if (ret != null) {
                return ret.toString();
            }
        }
        return "text/html";
    }


    @Override
    public void close() {

        if (webDriver!=null) {
            WebDriverPool.returnOne(webDriver); // 将webDriver返回到连接池
        }
    }
}
