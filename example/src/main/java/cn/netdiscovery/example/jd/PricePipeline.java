package cn.netdiscovery.example.jd;

import cn.netdiscovery.core.domain.ResultItems;
import cn.netdiscovery.core.pipeline.Pipeline;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by tony on 2018/6/12.
 */
@Slf4j
public class PricePipeline extends Pipeline {

    @Override
    public void process(ResultItems resultItems) {

        Elements elements = resultItems.get("goods_elements");
        if (elements != null && elements.size() >= 10) {
            for (int i = 0; i < 10; i++) {
                Element element = elements.get(i);
                String storeName = element.select("div[class=p-shopnum] a").first().tagName("title").text();
                String goodsName = element.select("div[class=p-name] a").first().tagName("title").text();
                String goodsPrice = element.select("div[class=p-price] i").first().text();
                log.info(storeName + "  " + goodsName + "  ￥" + goodsPrice);
            }
        }
    }
}
