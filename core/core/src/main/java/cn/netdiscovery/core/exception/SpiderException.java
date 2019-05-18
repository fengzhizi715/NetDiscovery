package cn.netdiscovery.core.exception;

/**
 * Created by tony on 2018/5/28.
 */
public class SpiderException extends RuntimeException {

    private static final long serialVersionUID = -8721389972071500975L;

    public SpiderException(String message) {
        super(message);
    }

    public SpiderException(Throwable throwable) {
        super(throwable);
    }

    public SpiderException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
