package ar.edu.ub.ubapplication.domain.estimator;

import android.util.Log;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;

import ar.edu.ub.ubapplication.domain.selector.GoodPointsSelectionResult;

/**
 * <p>Estimates the homography (with RANSAC) between reference and scene images</p>
 * Created by Silvana Olmedo on 21/05/2018.
 */

public class HomographyEstimator {

    private static final String TAG = HomographyEstimator.class.getSimpleName();

    public HomographyEstimationResult estimate(HomographyEstimationInput input){
        HomographyEstimationResult result = new HomographyEstimationResult();
        GoodPointsSelectionResult goodPointsSelectionResult = input.getGoodPointsSelection();

        if(goodPointsSelectionResult.areEnoughGoodPoints()){
            Mat homography = this.estimateHomography(goodPointsSelectionResult);
            result.setHomographyEstimated(true);
            Mat referenceCorners = input.getReferenceCorners();
            result.setCandidateSceneCorners(this.calculateCandidateSceneCorners(referenceCorners, homography));
        }

        return result;
    }

    private Mat estimateHomography(GoodPointsSelectionResult goodPointsSelectionResult) {
        MatOfPoint2f srcPoints = goodPointsSelectionResult.getGoodReferencePoints();
        MatOfPoint2f dstPoints = goodPointsSelectionResult.getGoodScenePoints();

        return Calib3d.findHomography(srcPoints, dstPoints, Calib3d.RANSAC, 5.0);
    }

    private Mat calculateCandidateSceneCorners(Mat referenceCorners, Mat homography){
        Log.d(TAG, "Calculating candidate scene corners");
        Mat candidateSceneCorners = new Mat(4, 1, CvType.CV_32FC2);

        Core.perspectiveTransform(referenceCorners, candidateSceneCorners, homography);

        return candidateSceneCorners;
    }
}

