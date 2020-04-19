package cn.netdiscovery.imageprocess.tesseract;

import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.lept;
import org.bytedeco.javacpp.tesseract;

/**
 * @FileName: cn.netdiscovery.imageprocess.tesseract.TesseractClient
 * @author: Tony Shen
 * @date: 2020-04-04 17:57
 * @version: V1.0 <描述当前版本功能>
 */
@Slf4j
public class TesseractClient {

    private static final String LANGUAGE = "eng";

    private final tesseract.TessBaseAPI tessBaseAPI;

    public TesseractClient() {
        tesseract.TessBaseAPI tessBaseAPI = new tesseract.TessBaseAPI();
        tessBaseAPI.Init("target/resources-external/tessdata/", LANGUAGE, 1);
        this.tessBaseAPI = tessBaseAPI;
        log.info("Created new TesseractClient Instance");
    }

    public String getTextFrom(lept.PIX image) {
        tessBaseAPI.SetImage(image);
        BytePointer pointer = tessBaseAPI.GetUTF8Text();
        return pointer == null ? "" : pointer.getString();
    }

    public void deallocate() {
        tessBaseAPI.End();
    }

    public void release() {
        tessBaseAPI.Clear();
        tessBaseAPI.ClearAdaptiveClassifier();
    }
}
