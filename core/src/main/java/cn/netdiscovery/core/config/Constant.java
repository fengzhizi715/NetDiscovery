package cn.netdiscovery.core.config;

/**
 * Created by tony on 2018/2/4.
 */
public class Constant {

    public static final String[] uaList = new String[]{
            "/ua/Baiduspider.txt","/ua/Chrome.txt","/ua/Edge.txt","/ua/Firefox.txt","/ua/Googlebot.txt","/ua/Mozilla.txt","/ua/Opera.txt","/ua/Safari.txt"
    };


    public static final String RESPONSE_JSON = "RESPONSE_JSON";

    public static final String RESPONSE_JSONP = "RESPONSE_JSONP";

    public static final String RESPONSE_RAW = "RESPONSE_RAW";



    public static final String UTF_8 = "UTF-8";

    public static final int OK_STATUS_CODE = 200;


    public static final String CONTENT_TYPE= "Content-Type";

    public static final String CONTENT_TYPE_JSON = "application/json";

    public static final String SET_COOKIES_HEADER = "Set-Cookie";

    public static final String SUCCESS = "success";



    public static final String QUEUE_TYPE_DEFAULT = "default";

    public static final String QUEUE_TYPE_DISRUPTOR = "disruptor";

    public static final String DOWNLOAD_TYPE_VERTX = "vertx";

    public static final String DOWNLOAD_TYPE_URL_CONNECTION = "urlConnection";

    public static final String DOWNLOAD_TYPE_FILE = "file";


    public static final String JOB_NAME = "spider_job";

    public static final String TRIGGER_NAME = "trigger";

    public static final String JOB_GROUP_NAME = "netdiscovery";

    public static final String TRIGGER_GROUP_NAME = "netdiscovery";
}
