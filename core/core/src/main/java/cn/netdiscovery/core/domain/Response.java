package cn.netdiscovery.core.domain;

import lombok.Data;

import java.io.InputStream;
import java.io.Serializable;

/**
 * Created by tony on 2018/1/20.
 */
@Data
public class Response implements Serializable {

    private static final long serialVersionUID = 7518499261167741602L;

    private byte[] content;
    private int statusCode;
    private String contentType;
    private InputStream is;
}
