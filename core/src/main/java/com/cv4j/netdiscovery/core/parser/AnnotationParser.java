package com.cv4j.netdiscovery.core.parser;

import com.cv4j.netdiscovery.core.domain.Page;
import com.cv4j.netdiscovery.core.domain.ResultItems;
import com.cv4j.netdiscovery.core.parser.annotation.ExtractBy;
import com.safframework.tony.common.utils.Preconditions;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * Created by tony on 2018/2/4.
 */
@Slf4j
public class AnnotationParser implements Parser{

    @Override
    public void process(Page page) {

        ResultItems resultItems = page.getResultItems();

        Class clazz = this.getClass();

        Field[] fields = clazz.getDeclaredFields();

        if (Preconditions.isNotBlank(fields)) {

            Arrays.asList(fields)
                    .forEach(field->{

                        ExtractBy.XPath xpath = field.getAnnotation(ExtractBy.XPath.class);

                        if (xpath!=null) {

                            resultItems.put(field.getName(),page.getHtml().xpath(xpath.value()));
                        }
                    });
        }
    }
}
