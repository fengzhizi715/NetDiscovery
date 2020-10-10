package cn.netdiscovery.pipeline.mongodb;

import cn.netdiscovery.core.domain.ResultItems;
import cn.netdiscovery.core.pipeline.Pipeline;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by tony on 2018/1/25.
 */
public class MongoDBPipeline extends Pipeline {

    private Logger log = LoggerFactory.getLogger(MongoDBPipeline.class);

    private MongoClient mongoClient;
    private String collectionName;

    public MongoDBPipeline(MongoClient mongoClient, String collectionName){

        this(mongoClient,collectionName,0);
    }

    public MongoDBPipeline(MongoClient mongoClient, String collectionName, long pipelineDelay){

        super(pipelineDelay);
        this.mongoClient = mongoClient;
        this.collectionName = collectionName;
    }
    
    @Override
    public void process(ResultItems resultItems) {

        JsonObject document = new JsonObject();
        for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()) {

            document.put(entry.getKey(),entry.getValue());
        }

        mongoClient.save(collectionName, document, res -> {

            if (res.succeeded()) {

                log.info("saved document with id " + res.result());
            } else {
                res.cause().printStackTrace();
            }

        });
    }
}
