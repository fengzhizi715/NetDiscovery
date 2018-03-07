package com.cv4j.netdiscovery.admin.verticle;

import com.cv4j.netdiscovery.admin.dao.MongoDao;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.ext.web.Router;

public class JobVerticle extends AbstractVerticle {

    /** 获取job（唯一的）要处理的网页URL及页数 */
    public final static String API_JOB_PAGES = "/jobpages";
    /** 改变网页信息：更新、删除 */
    public final static String API_JOB_PAGE = "/jobpage";

    /** 列出job的运行计划 */
    public final static String API_PROXY_JOBS = "/jobs";
    /** 更新job的运行计划 */
    public final static String API_PROXYJOB_SCHEDULE = "/jobschedule";
    /** 改变job状态：手动启动 */
    public final static String API_PROXYJOB_STATUS = "/jobstatus";

    private MongoDao mongoDao = MongoDao.getInstance();
    private Router router;

    public JobVerticle(Router router) {
        this.router = router;
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        super.start(startFuture);


    }
}
