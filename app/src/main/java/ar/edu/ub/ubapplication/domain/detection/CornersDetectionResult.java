package ar.edu.ub.ubapplication.domain.detection;

import org.opencv.core.MatOfPoint2f;

/**
 * <p>Stores the scene corners detected</p>
 * Created by Silvana Olmedo on 21/05/2018.
 */

public class CornersDetectionResult {

    private boolean areCornersDetected;
    private MatOfPoint2f sceneCorners;

    public boolean areCornersDetected() {
        return areCornersDetected;
    }

    public void setAreCornersDetected(boolean areCornersDetected) {
        this.areCornersDetected = areCornersDetected;
    }

    public MatOfPoint2f getSceneCorners() {
        return sceneCorners;
    }

    public void setSceneCorners(MatOfPoint2f sceneCorners) {
        this.sceneCorners = sceneCorners;
    }
}
