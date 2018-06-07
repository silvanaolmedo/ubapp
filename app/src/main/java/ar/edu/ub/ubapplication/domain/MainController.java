package ar.edu.ub.ubapplication.domain;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ar.edu.ub.ubapplication.domain.detection.CornersDetector;
import ar.edu.ub.ubapplication.domain.detection.PatternDetector;
import ar.edu.ub.ubapplication.domain.detection.model.Pattern;
import ar.edu.ub.ubapplication.domain.detection.model.PatternDetectionResult;
import ar.edu.ub.ubapplication.domain.estimator.HomographyEstimator;
import ar.edu.ub.ubapplication.domain.extractor.FeatureExtractor;
import ar.edu.ub.ubapplication.domain.factory.PatternFactory;
import ar.edu.ub.ubapplication.domain.matcher.PatternMatcher;
import ar.edu.ub.ubapplication.domain.selector.GoodPointsSelector;

/**
 * Created by Silvana Olmedo on 24/05/2018.
 */

public class MainController {

    private static final String TAG = MainController.class.getSimpleName();
    private Scalar lineColor = new Scalar(0, 255, 0);
    private Context context;
    private PatternDetector patternDetector;
    private List<Pattern> pattersToDetect;
    private PatternFactory patternFactory;
    private boolean detectionIsRunning;

    public MainController(Context context) {
        FeatureExtractor featureExtractor = new FeatureExtractor();
        PatternMatcher patternMatcher = new PatternMatcher(featureExtractor);
        GoodPointsSelector goodPointsSelector = new GoodPointsSelector();
        HomographyEstimator homographyEstimator = new HomographyEstimator();
        CornersDetector cornersDetector = new CornersDetector(goodPointsSelector, homographyEstimator);
        patternDetector = new PatternDetector(patternMatcher, cornersDetector);
        this.context = context;
        pattersToDetect = new ArrayList<>();
        patternFactory = new PatternFactory(featureExtractor);
    }

    public void addPattern(int idPattern){
        try {
            Mat mat = Utils.loadResource(context, idPattern, Imgcodecs.CV_LOAD_IMAGE_COLOR);
            pattersToDetect.add(patternFactory.create(mat, idPattern));
        } catch (IOException e) {
            Log.w(TAG, e);
        }
    }

    @SuppressLint("staticfieldleak")
    public void processFrame(final Mat frame){
        final Mat frameToProcess = this.convertToGray(frame);
        for(final Pattern pattern: pattersToDetect){
            PatternDetectionResult patternDetectionResult = this.patternDetector.detect(pattern, frameToProcess);
            if(patternDetectionResult.isPatternDetected()){
                Mat sceneCorners = patternDetectionResult.getSceneCorners();
                Imgproc.line(frame, new Point(sceneCorners.get(0, 0)),
                        new Point(sceneCorners.get(1, 0)), lineColor, 4);
                Imgproc.line(frame, new Point(sceneCorners.get(1, 0)),
                        new Point(sceneCorners.get(2, 0)), lineColor, 4);
                Imgproc.line(frame, new Point(sceneCorners.get(2, 0)),
                        new Point(sceneCorners.get(3, 0)), lineColor, 4);
                Imgproc.line(frame, new Point(sceneCorners.get(3, 0)),
                        new Point(sceneCorners.get(0, 0)), lineColor, 4);
            }
        }
    }

    private Mat convertToGray(Mat frame){
        Mat mat = new Mat();
        Imgproc.cvtColor(frame, mat, Imgproc.COLOR_RGBA2GRAY);
        return mat;
    }
}
