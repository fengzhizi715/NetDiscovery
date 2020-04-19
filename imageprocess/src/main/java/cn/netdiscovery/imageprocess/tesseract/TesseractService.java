package cn.netdiscovery.imageprocess.tesseract;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacpp.lept;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.regex.Pattern;

import static org.bytedeco.javacpp.lept.pixDestroy;
import static org.bytedeco.javacpp.lept.pixReadMem;

/**
 * @FileName: cn.netdiscovery.imageprocess.tesseract.TesseractService
 * @author: Tony Shen
 * @date: 2020-04-04 17:03
 * @version: V1.0 <描述当前版本功能>
 */
@Slf4j
public class TesseractService {

    private static final Pattern UNWANTED_CHARS = Pattern.compile("[^ -~^\\s]");

    public Observable<String> extractTextFromImage(Observable<ByteBuffer> postWithImage) {

        return postWithImage.map(new Function<ByteBuffer, String>() {

            @Override
            public String apply(ByteBuffer byteBuffer) throws Exception {
                Instant start = Instant.now();

                lept.PIX pixImage = pixReadMem(byteBuffer, byteBuffer.capacity());
                byteBuffer.clear();

                TesseractClient tesseractClient = TesseractPool.borrowOne();
                String cleanResult = cleanString(tesseractClient.getTextFrom(pixImage));

                pixDestroy(pixImage);
                tesseractClient.release();

                log.info("OCR took {} millis", Duration.between(start, Instant.now()).toMillis());
                return cleanResult;
            }
        });
    }

    private String cleanString(String output) {
        return UNWANTED_CHARS.matcher(new String(output.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8)).replaceAll("");
    }
}
