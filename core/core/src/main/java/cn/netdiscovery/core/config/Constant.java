package cn.netdiscovery.core.config;

/**
 * Created by tony on 2018/2/4.
 */
public class Constant {

    // 存放 ua 的文件
    public static final String[] uaFiles = new String[]{
            "/ua/Baiduspider.txt",
            "/ua/Chrome.txt",
            "/ua/Edge.txt",
            "/ua/Firefox.txt",
            "/ua/Googlebot.txt",
            "/ua/Mozilla.txt",
            "/ua/Opera.txt",
            "/ua/Safari.txt",
            "/ua/Android Webkit Browser.txt"
    };

    // http response 相关
    public static final String RESPONSE_JSON = "RESPONSE_JSON";
    public static final String RESPONSE_JSONP = "RESPONSE_JSONP";
    public static final String RESPONSE_RAW = "RESPONSE_RAW";
    public static final String UTF_8 = "UTF-8";
    public static final int OK_STATUS_CODE = 200;
    public static final String CONTENT_TYPE= "Content-Type";
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String SET_COOKIES_HEADER = "Set-Cookie";
    public static final String SUCCESS = "success";

    // SpiderEngine 相关
    public static final String ROUTER_HEALTH = "/netdiscovery/health/";
    public static final String ROUTER_METRICS = "/netdiscovery/metrics/";
    public static final String ROUTER_SPIDERS = "/netdiscovery/spiders/";
    public static final String ROUTER_SPIDER_DETAIL = "/netdiscovery/spider/:spiderName/detail";
    public static final String ROUTER_SPIDER_STATUS = "/netdiscovery/spider/:spiderName/status";
    public static final String ROUTER_SPIDER_PUSH = "/netdiscovery/spider/:spiderName/push";
    public static final String ROUTER_JOBS = "/netdiscovery/jobs/";

    // queue 相关
    public static final String QUEUE_TYPE_DEFAULT = "default";
    public static final String QUEUE_TYPE_DISRUPTOR = "disruptor";

    // downloader 相关
    public static final String DOWNLOAD_TYPE_VERTX = "vertx";
    public static final String DOWNLOAD_TYPE_URL_CONNECTION = "urlConnection";
    public static final String DOWNLOAD_TYPE_FILE = "file";

    // quartz 相关
    public static final String TRIGGER_NAME = "trigger";
    public static final String TRIGGER_GROUP_NAME = "netdiscovery";
    public static final String JOB_GROUP_NAME = "netdiscovery";
    public static final String SPIDER_JOB_NAME = "spider_job_";
    public static final String PROXY_POOL_JOB_NAME = "proxy_pool_job_";

    // 注册中心相关
    public static final String DEFAULT_REGISTRY_PATH = "/netdiscovery";
}
