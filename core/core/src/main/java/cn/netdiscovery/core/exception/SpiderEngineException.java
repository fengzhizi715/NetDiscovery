package cn.netdiscovery.core.exception;

/**
 * @FileName: cn.netdiscovery.core.exception.SpiderEngineException
 * @author: Tony Shen
 * @date: 2020-04-04 00:35
 * @version: V1.0 <描述当前版本功能>
 */
public class SpiderEngineException extends RuntimeException {

    private static final long serialVersionUID = 4132319253343364085L;

    public SpiderEngineException(String message) {
        super(message);
    }

    public SpiderEngineException(Throwable throwable) {
        super(throwable);
    }

    public SpiderEngineException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
