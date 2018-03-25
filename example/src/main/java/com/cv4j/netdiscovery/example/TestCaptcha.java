package com.cv4j.netdiscovery.example;

import com.cv4j.netdiscovery.core.utils.Utils;

/**
 * Created by tony on 2018/3/25.
 */
public class TestCaptcha {

    public static void main(String[] args) {

        System.out.println(Utils.getCaptcha("http://47.97.7.119/qianmou/images/captcha/3.png"));

        System.out.println(Utils.getCaptcha("http://47.97.7.119/qianmou/images/captcha/4.png"));
    }
}
