package cn.netdiscovery.imageprocess;

import cn.netdiscovery.imageprocess.utils.ImageUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_java;
import org.opencv.core.*;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.io.File;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @FileName: cn.netdiscovery.imageprocess.ImagePreprocessingService
 * @author: Tony Shen
 * @date: 2020-02-22 23:15
 * @version: V1.0 <描述当前版本功能>
 */
@Slf4j
public class ImagePreprocessingService {

    static {
        Loader.load(opencv_java.class);
    }

    private final Net net;
    private static final List<String> EAST_FEATURES = Arrays.asList("feature_fusion/Conv_7/Sigmoid", "feature_fusion/concat_3");
    private static final int THRESHOLD = 0;

    public ImagePreprocessingService() {
        this.net = Dnn.readNetFromTensorflow("target/resources-external/east/frozen_east_text_detection.pb");
    }

    public Observable<ByteBuffer> process(File file) {

        return convert(file)
                .flatMap(this::detectText)
                .flatMap(this::preprocess);
    }

    private Observable<Mat> convert(File image) {

        byte[] imageArray = ImageUtils.file2Byte(image);

        return Observable.create(new ObservableOnSubscribe<Mat>() {
            @Override
            public void subscribe(ObservableEmitter<Mat> emitter) throws Exception {
                MatOfByte matOfByte = new MatOfByte(imageArray);
                Mat imageMat = Imgcodecs.imdecode(matOfByte, Imgcodecs.IMREAD_UNCHANGED);
                matOfByte.release();

                emitter.onNext(imageMat);
            }
        });
    }

    private Observable<Mat> detectText(Mat image) {
        return Observable.fromCallable(() -> {

            Instant start = Instant.now();

            float scoreThresh = 0.5f;
            float nmsThresh = 0.4f;

            Mat frame = new Mat();
            Imgproc.cvtColor(image, frame, Imgproc.COLOR_RGBA2RGB);
            image.release();

            Size siz = new Size(320, 320);
            int H = (int) (siz.height / 4);
            Mat blob = Dnn.blobFromImage(frame, 1.0, siz, new Scalar(123.68, 116.78, 103.94), true, false);
            net.setInput(blob);

            List<Mat> outs = new ArrayList<>(2);
            net.forward(outs, EAST_FEATURES);

            // Decode predicted bounding boxes.
            Mat scores = outs.get(0).reshape(1, H);
            Mat geometry = outs.get(1).reshape(1, 5 * H);
            outs.forEach(Mat::release);
            List<Float> confidencesList = new ArrayList<>();
            List<RotatedRect> boxesList = decode(scores, geometry, confidencesList, scoreThresh);

            if (confidencesList.isEmpty()) {
                frame.release();
                blob.release();
                scores.release();
                geometry.release();
                log.info("Skipping image. No text was detected. Detection took {} millis", Duration.between(start, Instant.now()).toMillis());
                return null;
            }

            // Apply non-maximum suppression procedure.
            MatOfFloat confidences = new MatOfFloat(Converters.vector_float_to_Mat(confidencesList));
            RotatedRect[] boxesArray = boxesList.toArray(new RotatedRect[0]);
            MatOfRotatedRect boxes = new MatOfRotatedRect(boxesArray);
            MatOfInt indices = new MatOfInt();
            Dnn.NMSBoxesRotated(boxes, confidences, scoreThresh, nmsThresh, indices);

            blob.release();
            scores.release();
            geometry.release();
            confidences.release();
            boxes.release();

            if (indices.toArray().length > THRESHOLD) {
                indices.release();
                log.info("Detected text in image. Detection took {} millis", Duration.between(start, Instant.now()).toMillis());
                return frame;
            }

            log.info("Skipping image. No text was detected. Detection took {} millis", Duration.between(start, Instant.now()).toMillis());
            indices.release();
            frame.release();
            return null;
        });
    }

    private static List<RotatedRect> decode(Mat scores, Mat geometry, List<Float> confidences, float scoreThresh) {
        int W = geometry.cols();
        int H = geometry.rows() / 5;

        List<RotatedRect> detections = new ArrayList<>();
        for (int y = 0; y < H; ++y) {
            Mat scoresData = scores.row(y);
            Mat x0Data = geometry.submat(0, H, 0, W).row(y);
            Mat x1Data = geometry.submat(H, 2 * H, 0, W).row(y);
            Mat x2Data = geometry.submat(2 * H, 3 * H, 0, W).row(y);
            Mat x3Data = geometry.submat(3 * H, 4 * H, 0, W).row(y);
            Mat anglesData = geometry.submat(4 * H, 5 * H, 0, W).row(y);

            for (int x = 0; x < W; ++x) {
                double score = scoresData.get(0, x)[0];
                if (score >= scoreThresh) {
                    double offsetX = x * 4.0;
                    double offsetY = y * 4.0;
                    double angle = anglesData.get(0, x)[0];
                    double cosA = Math.cos(angle);
                    double sinA = Math.sin(angle);
                    double x0 = x0Data.get(0, x)[0];
                    double x1 = x1Data.get(0, x)[0];
                    double x2 = x2Data.get(0, x)[0];
                    double x3 = x3Data.get(0, x)[0];
                    double h = x0 + x2;
                    double w = x1 + x3;
                    Point offset = new Point(offsetX + cosA * x1 + sinA * x2, offsetY - sinA * x1 + cosA * x2);
                    Point p1 = new Point(-1 * sinA * h + offset.x, -1 * cosA * h + offset.y);
                    Point p3 = new Point(-1 * cosA * w + offset.x, sinA * w + offset.y);
                    RotatedRect r = new RotatedRect(new Point(0.5 * (p1.x + p3.x), 0.5 * (p1.y + p3.y)), new Size(w, h), -1 * angle * 180 / Math.PI);
                    detections.add(r);
                    confidences.add((float) score);
                }
            }
            scoresData.release();
            x0Data.release();
            x1Data.release();
            x2Data.release();
            x3Data.release();
            anglesData.release();
        }
        return detections;
    }

    private Observable<ByteBuffer> preprocess(Mat image) {
        return Observable.fromCallable(() -> {

            Instant start = Instant.now();

            Mat resized = new Mat();
            Imgproc.resize(image, resized, new Size(0, 0), 1.2, 1.2, Imgproc.INTER_CUBIC);
            image.release();

            Mat grey = new Mat();
            Imgproc.cvtColor(resized, grey, Imgproc.COLOR_RGB2GRAY);
            resized.release();

            Mat binary = new Mat();
            Imgproc.adaptiveThreshold(grey, binary, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 21, 1);
            grey.release();

            MatOfByte bytes = new MatOfByte();
            Imgcodecs.imencode(".png", binary, bytes);
            binary.release();

            ByteBuffer buffer = ByteBuffer.wrap(bytes.toArray());
            bytes.release();

            log.info("Preprocessing of image took {} millis", Duration.between(start, Instant.now()).toMillis());
            return buffer;
        });
    }
}
