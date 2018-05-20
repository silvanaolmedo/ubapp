package ar.edu.ub.ubapplication.domain.detection;

import android.util.Log;

import org.opencv.core.Mat;

import ar.edu.ub.ubapplication.domain.matcher.PatternMatcher;
import ar.edu.ub.ubapplication.domain.matcher.PatternMatchingResult;

/**
 * <p>Detects a pattern in an scene by finding their matches and calculating the homography</p>
 * Created by Silvana Olmedo on 20/05/2018.
 */

public class PatternDetection {

    private static final String TAG = PatternDetection.class.getSimpleName();
    private PatternMatcher patternMatcher;

    //TODO: update constructor with HomographyEstimator object
    public PatternDetection(PatternMatcher patternMatcher) {
        this.patternMatcher = patternMatcher;
    }

    public PatternDetectionResult detect(Pattern pattern, Mat mat){
        PatternDetectionResult result = new PatternDetectionResult();

        Log.d(TAG, "Detecting pattern in scene");
        PatternMatchingResult matchingResult = patternMatcher.match(pattern, mat);
        //TODO: differentiate between source points and destination points
        //TODO: calculate homography and update detection result
        return result;
    }

}
