package cn.netdiscovery.pipeline.couchbase;

import cn.netdiscovery.core.domain.ResultItems;
import cn.netdiscovery.core.pipeline.Pipeline;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;

import java.util.Map;

/**
 * Created by tony on 2018/2/17.
 */
public class CouchbasePipeline extends Pipeline{

    private CouchbaseCluster cluster;
    private Bucket bucket;
    private String documentId;

    public CouchbasePipeline(CouchbaseCluster cluster, Bucket bucket, String documentId){

        this(cluster,bucket,documentId,0);
    }

    public CouchbasePipeline(CouchbaseCluster cluster, Bucket bucket, String documentId, long pipelineDelay){

        super(pipelineDelay);
        this.cluster = cluster;
        this.bucket = bucket;
        this.documentId = documentId;
    }

    public CouchbasePipeline(CouchbaseCluster cluster, String bucketName, String documentId){

        this(cluster,bucketName,documentId,0);
    }

    public CouchbasePipeline(CouchbaseCluster cluster, String bucketName, String documentId, long pipelineDelay){

        super(pipelineDelay);
        this.cluster = cluster;
        this.bucket = cluster.openBucket(bucketName);
        this.documentId = documentId;
    }

    @Override
    public void process(ResultItems resultItems) {

        JsonObject content = JsonObject.create();
        for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()) {

            content.put(entry.getKey(),entry.getValue());
        }

        bucket.upsert(JsonDocument.create(documentId, content));

        // Close all buckets and disconnect
        cluster.disconnect();
    }
}
