package com.cv4j.netdiscovery.example.jd;

import cn.netdiscovery.core.domain.Page;
import cn.netdiscovery.core.parser.Parser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * Created by tony on 2018/6/12.
 */
public class PriceParser implements Parser{

    @Override
    public void process(Page page) {

        String pageHtml = page.getHtml().toString();
        Document document = Jsoup.parse(pageHtml);
        Elements elements = document.select("div[id=J_goodsList] li[class=gl-item]");
        page.getResultItems().put("goods_elements",elements);
    }
}
