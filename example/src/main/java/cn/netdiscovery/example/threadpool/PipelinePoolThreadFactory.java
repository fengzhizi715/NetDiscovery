package cn.netdiscovery.example.threadpool;

/**
 * @FileName: cn.netdiscovery.example.threadpool.DownloadThreadPoolThreadFactory
 * @author: Tony Shen
 * @date: 2020-04-04 10:21
 * @version: V1.0 <描述当前版本功能>
 */
public class PipelinePoolThreadFactory extends AbstractDefaultThreadFactory {

    @Override
    String getNamePrefix() {
        return "NetDiscovery-" + poolNumber.getAndIncrement() + "-thread-";
    }
}
