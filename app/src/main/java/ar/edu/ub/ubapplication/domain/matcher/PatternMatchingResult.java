package ar.edu.ub.ubapplication.domain.matcher;

import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;

/**
 * Stores matches, reference and scene keypoints
 * Created by Silvana Olmedo on 20/05/2018.
 */

public class PatternMatchingResult {

    private MatOfDMatch matches;
    private MatOfKeyPoint referenceKeyPoints;
    private MatOfKeyPoint sceneKeyPoints;

    public MatOfDMatch getMatches() {
        return matches;
    }

    public void setMatches(MatOfDMatch matches) {
        this.matches = matches;
    }

    public MatOfKeyPoint getSceneKeyPoints() {
        return sceneKeyPoints;
    }

    public void setSceneKeyPoints(MatOfKeyPoint sceneKeyPoints) {
        this.sceneKeyPoints = sceneKeyPoints;
    }

    public MatOfKeyPoint getReferenceKeyPoints() {
        return referenceKeyPoints;
    }

    public void setReferenceKeyPoints(MatOfKeyPoint referenceKeyPoints) {
        this.referenceKeyPoints = referenceKeyPoints;
    }

}
