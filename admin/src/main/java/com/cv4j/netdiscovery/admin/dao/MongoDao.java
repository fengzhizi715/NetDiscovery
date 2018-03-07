package com.cv4j.netdiscovery.admin.dao;

import com.cv4j.netdiscovery.admin.LaunchAdmin;
import com.cv4j.netdiscovery.admin.common.CommonUtil;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

public class MongoDao {

    private MongoClient mongoClient;

    private MongoDao(){ }

    public static MongoDao getInstance(){

        return Holder.INSTANCE;
    }

    private static class Holder {
        private static final MongoDao INSTANCE = new MongoDao();
    }

    public void init() {
        if(mongoClient == null) {
            mongoClient = MongoClient.createShared(LaunchAdmin.vertx, CommonUtil.getDatabaseConfig());
        }
    }

    public void findAll(String colName, Handler handler) {
        mongoClient.find(colName, new JsonObject(), handler);
    }

    public void insert(String colName, JsonObject newDoc, Handler handler) {
        mongoClient.insert(colName, newDoc, handler);
    }

    public void update(String colName,  JsonObject queryObj, JsonObject updateDoc, Handler handler) {
        mongoClient.findOneAndReplace(colName, queryObj, updateDoc, handler);
    }

    public void remove(String colName, JsonObject queryObj, Handler handler) {
        mongoClient.removeDocument(colName, queryObj, handler);
    }

}