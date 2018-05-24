package ar.edu.ub.ubapplication.domain.estimator;

import org.opencv.core.Mat;

import ar.edu.ub.ubapplication.domain.selector.GoodPointsSelectionResult;

/**
 * Created by Silvana Olmedo on 23/05/2018.
 */

public class HomographyEstimationInput {

    private GoodPointsSelectionResult goodPointsSelectionResult;
    private Mat referenceCorners;

    public GoodPointsSelectionResult getGoodPointsSelection() {
        return goodPointsSelectionResult;
    }

    public void setGoodPointsSelectionResult(GoodPointsSelectionResult goodPointsSelectionResult) {
        this.goodPointsSelectionResult = goodPointsSelectionResult;
    }

    public Mat getReferenceCorners() {
        return referenceCorners;
    }

    public void setReferenceCorners(Mat referenceCorners) {
        this.referenceCorners = referenceCorners;
    }
}
