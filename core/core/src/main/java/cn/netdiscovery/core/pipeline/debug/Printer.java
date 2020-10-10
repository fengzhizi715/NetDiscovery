package cn.netdiscovery.core.pipeline.debug;

import cn.netdiscovery.core.domain.Request;
import cn.netdiscovery.core.utils.SerializableUtils;
import com.safframework.tony.common.utils.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

/**
 * Created by tony on 2018/2/5.
 */

public class Printer {

    private static Logger log = LoggerFactory.getLogger(Printer.class);

//    private static final int JSON_INDENT = 3;

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static final String DOUBLE_SEPARATOR = LINE_SEPARATOR + LINE_SEPARATOR;

    private static final String N = "\n";
    private static final String T = "\t";
    private static final String REQUEST_UP_LINE = "┌───────────────────────────────────────────────────────────────────────────────────────";
    private static final String END_LINE = "└───────────────────────────────────────────────────────────────────────────────────────";
    private static final String BODY_TAG = "Body:";
    private static final String URL_TAG = "URL: ";
    private static final String METHOD_TAG = "Method: @";
    private static final String HEADERS_TAG = "Headers:";
    private static final String DEFAULT_LINE = "│ ";

    protected Printer() {
        throw new UnsupportedOperationException();
    }

    private static boolean isEmpty(String line) {
        return Preconditions.isBlank(line) || N.equals(line) || T.equals(line);
    }

    protected static void printJsonRequest(Request request) {

        log.info(REQUEST_UP_LINE);

        logLines(new String[]{URL_TAG + request.getUrl()}, false);
        logLines(getRequest(request, Level.HEADERS),  true);

        if (request.getHttpRequestBody()!=null){

            String requestBody = LINE_SEPARATOR + BODY_TAG + LINE_SEPARATOR + bodyToString(request);
            logLines(requestBody.split(LINE_SEPARATOR),  true);
        }

        log.info(END_LINE);
    }

    private static String[] getRequest(Request request, Level level) {

        String header = SerializableUtils.toJson(request.getHeader());
        boolean loggableHeader = level == Level.HEADERS || level == Level.BASIC;
        String logStr = METHOD_TAG + request.getHttpMethod().name() + DOUBLE_SEPARATOR +
                (isEmpty(header) ? "" : loggableHeader ? HEADERS_TAG + LINE_SEPARATOR + dotHeaders(header) : "");
        return logStr.split(LINE_SEPARATOR);
    }

    private static String dotHeaders(String header) {
        String[] headers = header.split(LINE_SEPARATOR);
        StringBuilder builder = new StringBuilder();
        String tag = "─ ";
        if (headers.length > 1) {
            for (int i = 0; i < headers.length; i++) {
                builder.append(headers[i]).append("\n");
            }
        } else {
            for (String item : headers) {
                builder.append(tag).append(item).append("\n");
            }
        }
        return builder.toString();
    }

    private static void logLines(String[] lines, boolean withLineSize) {
        for (String line : lines) {

            int lineLength = line.length();
            int MAX_LONG_SIZE = withLineSize ? 110 : lineLength;

            for (int i = 0; i <= lineLength / MAX_LONG_SIZE; i++) {
                int start = i * MAX_LONG_SIZE;
                int end = (i + 1) * MAX_LONG_SIZE;
                end = end > line.length() ? line.length() : end;
                log.info(DEFAULT_LINE + line.substring(start, end));
            }
        }
    }

    private static String bodyToString(final Request request) {

        if (request.getHttpRequestBody()!=null) {

            byte[] body = request.getHttpRequestBody().getBody();
            try {
                return new String(body,"UTF-8");
            } catch (UnsupportedEncodingException e) {
                return "{\"err\": \"" + e.getMessage() + "\"}";
            }
        }

        return "";
    }
}
