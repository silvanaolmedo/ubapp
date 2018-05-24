package ar.edu.ub.ubapplication.domain.selector;

import org.opencv.core.MatOfPoint2f;

/**
 * Stores the good points of reference and scene images.
 * Created by Silvana Olmedo on 21/05/2018.
 */

public class GoodPointsSelectionResult {

    private boolean areEnoughGoodPoints;
    private MatOfPoint2f goodReferencePoints;
    private MatOfPoint2f goodScenePoints;

    public boolean areEnoughGoodPoints() {
        return areEnoughGoodPoints;
    }

    public void setAreEnoughGoodPoints(boolean areEnoughGoodPoints) {
        this.areEnoughGoodPoints = areEnoughGoodPoints;
    }

    public MatOfPoint2f getGoodReferencePoints() {
        return goodReferencePoints;
    }

    public void setGoodReferencePoints(MatOfPoint2f goodReferencePoints) {
        this.goodReferencePoints = goodReferencePoints;
    }

    public MatOfPoint2f getGoodScenePoints() {
        return goodScenePoints;
    }

    public void setGoodScenePoints(MatOfPoint2f goodScenePoints) {
        this.goodScenePoints = goodScenePoints;
    }
}
