package ar.edu.ub.ubapplication;

import android.content.Context;
import android.util.Log;

import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ar.edu.ub.ubapplication.camera.CameraProjectionAdapter;
import ar.edu.ub.ubapplication.detection.DefaultPatternDetection;
import ar.edu.ub.ubapplication.detection.FeaturePattern;
import ar.edu.ub.ubapplication.detection.PatternDetector;
import ar.edu.ub.ubapplication.detection.ProcessorStatus;
import ar.edu.ub.ubapplication.factory.DefaultPatternFactory;
import ar.edu.ub.ubapplication.factory.PatternFactory;

import static ar.edu.ub.ubapplication.detection.ProcessorStatus.BASE_INITIALIZED;
import static ar.edu.ub.ubapplication.detection.ProcessorStatus.DETECTION_RUNNING;
import static ar.edu.ub.ubapplication.detection.ProcessorStatus.NOTHING_INITIALIZED;
import static org.opencv.imgcodecs.Imgcodecs.CV_LOAD_IMAGE_COLOR;

public class DefaultARProcessor implements ARProcessor {

    private static final String TAG = DefaultARProcessor.class.getSimpleName();
    private static DefaultARProcessor instance = null;
    private static boolean loadedOpencv = false;
    private static boolean initiatedOpencv = false;
    private ProcessorStatus status;
    private List<FeaturePattern> patternsToDetect;
    private CameraProjectionAdapter cameraProjectionAdapter;
    private PatternFactory<FeaturePattern> patternFactory;
    private PatternDetector<FeaturePattern> patternDetector;

    static {
        loadedOpencv = OpenCVLoader.initDebug();
        if (!loadedOpencv)
            Log.d(TAG, "Unable to load OpenCV");
        else
            Log.d(TAG, "OpenCV loaded");
    }

    public static DefaultARProcessor getInstance(){
        if (instance == null) instance = new DefaultARProcessor();
        return instance;
    }

    private DefaultARProcessor(){
        patternsToDetect = new ArrayList<>();
        status = NOTHING_INITIALIZED;
    }

    @Override
    public boolean initialize(Context context, CameraProjectionAdapter cameraProjectionAdapter, LoaderCallbackInterface loaderCallback) {
        if (!loadedOpencv) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            return false;
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            patternFactory = new DefaultPatternFactory();
            this.cameraProjectionAdapter = cameraProjectionAdapter;
            patternDetector = new DefaultPatternDetection(cameraProjectionAdapter);
            loaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        initiatedOpencv = true;
        status = BASE_INITIALIZED;
        return true;
    }

    @Override
    public boolean isRunning() {
        return status == DETECTION_RUNNING;
    }

    @Override
    public int addPattern(Context context, int idResource) {
        int uui = -1;
        if (!initiatedOpencv) {
            return uui;
        }
        try {
            Mat referenceImage = Utils.loadResource(context, idResource, CV_LOAD_IMAGE_COLOR);
            Mat referenceImageGray = new Mat();
            Imgproc.cvtColor(referenceImage, referenceImageGray, Imgproc.COLOR_BGR2GRAY);
            if(!this.contains(idResource)){
                this.patternsToDetect.add(this.patternFactory.create(idResource, referenceImageGray));
                Log.i(TAG, "Pattern created with id: "+idResource);
                uui = idResource;
            }else{
                Log.i(TAG, String.format("Pattern %s already created", idResource));
            }
        } catch (IOException e) {
            Log.e(TAG, "Unable to load resource with id: "+idResource);
        }
        return uui;
    }

    @Override
    public boolean processFrame(Mat src, Mat dst) {
        if(!initiatedOpencv) return false;
        Log.i(TAG, "processFrame");
        status = DETECTION_RUNNING;
        this.patternDetector.detect(patternsToDetect, src, dst);
        return true;
    }

    @Override
    public boolean isPatternFound(int idPattern) {
        if (!initiatedOpencv) return false;
        FeaturePattern pattern = this.find(idPattern);
        Log.i(TAG, String.format("Checking visibility for pattern %s", idPattern));
        return pattern != null && pattern.isVisible();
    }

    @Override
    public float[] getPose(int idPattern) {
        if (!initiatedOpencv) return null;
        FeaturePattern pattern = this.find(idPattern);
        Log.i(TAG, String.format("Checking pose for pattern %s", idPattern));
        return (pattern != null ? pattern.getPose() : null);
    }

    @Override
    public float[] getProjectionMatrix() {
        if (!initiatedOpencv) return null;
        Log.i(TAG, "Obtaining projectionGL");
        return (cameraProjectionAdapter != null ? cameraProjectionAdapter.getProjectionGL() : null);
    }

    public float getX(int idPattern){
        if (!initiatedOpencv) return 0;
        FeaturePattern pattern = this.find(idPattern);
        Log.i(TAG, String.format("Checking centerY for pattern %s", idPattern));
        return (pattern != null ? pattern.getX() : 0);
    }

    public float getY(int idPattern){
        if (!initiatedOpencv) return 0;
        FeaturePattern pattern = this.find(idPattern);
        Log.i(TAG, String.format("Checking centerX for pattern %s", idPattern));
        return (pattern != null ? pattern.getY() : 0);
    }

    public Float getAspectRatio() {
        if (!initiatedOpencv) return null;
        Log.i(TAG, "Obtaining projectionGL");
        return (cameraProjectionAdapter != null ? cameraProjectionAdapter.getAspectRatio() : null);
    }

    @Override
    public void cleanup() {
        //TODO: clean list of patterns or something else
        if (!initiatedOpencv) return;

        initiatedOpencv = false;
    }

    //TODO: refactor equals of pattern object
    private FeaturePattern find(int idPattern){
        FeaturePattern pattern = null;
        for(FeaturePattern featurePattern: patternsToDetect){
            if(featurePattern.getId() == idPattern){
                pattern = featurePattern;
                break;
            }
        }
        return pattern;
    }

    private boolean contains(int idPattern){
        boolean contains = false;
        for(FeaturePattern featurePattern: patternsToDetect){
            if(featurePattern.getId() == idPattern){
                contains = true;
                break;
            }
        }
        return contains;
    }
}
