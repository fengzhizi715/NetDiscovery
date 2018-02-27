package com.cv4j.netdiscovery.core.parser.annotation;

import java.lang.annotation.*;

/**
 * Created by tony on 2018/2/4.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExtractBy {

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface XPath {
        String value();
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Regex {
        String value();

        int group() default 3;
    }
}
