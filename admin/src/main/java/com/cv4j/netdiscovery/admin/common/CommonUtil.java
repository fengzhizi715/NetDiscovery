package com.cv4j.netdiscovery.admin.common;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.web.RoutingContext;

public class CommonUtil {
    
    public final static String DB_SERVER = "mongodb://127.0.0.1:27017";
    public final static String DB_NAME = "proxypool";
    public final static String DB_USER = "root";
    public final static String DB_PASSWORD = "123456";
    public final static String ENGINE_URI= "http://127.0.0.1:8012";  //爬虫数据的来源

    public static JsonObject getDatabaseConfig() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put("connection_string", DB_SERVER);
        jsonObject.put("db_name", DB_NAME);
        jsonObject.put("username", DB_USER);
        jsonObject.put("password", DB_PASSWORD);
        return jsonObject;
    }

    public static FindOptions getFindOptionsForLast(JsonObject sort) {
        FindOptions options = new FindOptions();
        if(sort != null) {
            options.setSort(sort);
        }
        options.setLimit(1);
        return options;
    }

    public static FindOptions getFindOptions(JsonObject sort, JsonObject projection, int limitCount, int skipNum) {
        FindOptions options = new FindOptions();
        if(sort != null) {
            options.setSort(sort);
        }
        options.setFields(projection);
        if(limitCount >= 1) {
            options.setLimit(limitCount);
        }
        options.setSkip(skipNum);
        return options;
    }

    public static void sendJsonToResponse(RoutingContext routingContext, Object contentObj) {
        HttpServerResponse response = routingContext.response();
        response.setChunked(true);
        response.putHeader("content-type", "application/json; charset=utf-8");
        response.end(Json.encode(contentObj));
    }

}