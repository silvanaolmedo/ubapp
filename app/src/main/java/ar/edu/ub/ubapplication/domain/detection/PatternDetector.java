package ar.edu.ub.ubapplication.domain.detection;

import android.util.Log;

import org.opencv.core.Mat;

import ar.edu.ub.ubapplication.domain.detection.model.CornersDetectionInput;
import ar.edu.ub.ubapplication.domain.detection.model.CornersDetectionResult;
import ar.edu.ub.ubapplication.domain.detection.model.Pattern;
import ar.edu.ub.ubapplication.domain.detection.model.PatternDetectionResult;
import ar.edu.ub.ubapplication.domain.matcher.PatternMatcher;
import ar.edu.ub.ubapplication.domain.matcher.PatternMatchingResult;

/**
 * <p>Detects a pattern in an scene by finding their matches
 * and detecting the corners of the scene</p>
 * Created by Silvana Olmedo on 20/05/2018.
 */

public class PatternDetector {

    private static final String TAG = PatternDetector.class.getSimpleName();
    private PatternMatcher patternMatcher;
    private CornersDetector cornersDetector;

    public PatternDetector(PatternMatcher patternMatcher, CornersDetector cornersDetector) {
        this.patternMatcher = patternMatcher;
        this.cornersDetector = cornersDetector;
    }

    public PatternDetectionResult detect(Pattern pattern, Mat mat){
        PatternDetectionResult result = new PatternDetectionResult();

        Log.d(TAG, "Detecting pattern in scene");
        PatternMatchingResult matchingResult = patternMatcher.match(pattern, mat);

        CornersDetectionInput cornersDetectionInput = new CornersDetectionInput();
        cornersDetectionInput.setPatternMatchingResult(matchingResult);
        cornersDetectionInput.setReferenceCorners(pattern.getReferenceCornersIn2d());

        CornersDetectionResult cornersDetectionResult = cornersDetector.detect(cornersDetectionInput);

        result.setPatternDetected(cornersDetectionResult.areCornersDetected());
        result.setSceneCorners(cornersDetectionResult.getSceneCorners());

        return result;
    }

}
