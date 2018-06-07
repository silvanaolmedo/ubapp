package ar.edu.ub.ubapplication.domain.detection.model;

import org.opencv.core.Mat;

/**
 * <p>Stores the estimated corners of a pattern in the scene</p>
 * Created by Silvana Olmedo on 20/05/2018.
 */

public class PatternDetectionResult {

    private boolean isPatternDetected;
    private Mat sceneCorners;

    public boolean isPatternDetected() {
        return isPatternDetected;
    }

    public void setPatternDetected(boolean patternDetected) {
        isPatternDetected = patternDetected;
    }

    public Mat getSceneCorners() {
        return sceneCorners;
    }

    public void setSceneCorners(Mat sceneCorners) {
        this.sceneCorners = sceneCorners;
    }
}
