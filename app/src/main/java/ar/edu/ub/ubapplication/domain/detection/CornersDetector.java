package ar.edu.ub.ubapplication.domain.detection;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import ar.edu.ub.ubapplication.domain.estimator.HomographyEstimationInput;
import ar.edu.ub.ubapplication.domain.estimator.HomographyEstimationResult;
import ar.edu.ub.ubapplication.domain.estimator.HomographyEstimator;
import ar.edu.ub.ubapplication.domain.matcher.PatternMatchingResult;
import ar.edu.ub.ubapplication.domain.selector.GoodPointsSelectionResult;
import ar.edu.ub.ubapplication.domain.selector.GoodPointsSelector;

/**
 * <p>Detects corners of the scene based on reference's and scene's good points
 * by finding the homography and using it to project the reference
 * corner coordinates into the scene coordinates.</p>
 * Created by Silvana Olmedo on 21/05/2018.
 */

public class CornersDetector {

    private static final String TAG = CornersDetector.class.getSimpleName();
    private GoodPointsSelector goodPointsSelector;
    private HomographyEstimator homographyEstimator;

    public CornersDetector(GoodPointsSelector goodPointsSelector, HomographyEstimator homographyEstimator) {
        this.goodPointsSelector = goodPointsSelector;
        this.homographyEstimator = homographyEstimator;
    }

    public CornersDetectionResult detect(CornersDetectionInput cornersDetectionInput){
        CornersDetectionResult result = new CornersDetectionResult();
        PatternMatchingResult patternDetectionResult = cornersDetectionInput.getPatternMatchingResult();

        GoodPointsSelectionResult goodPointsSelected = this.goodPointsSelector.select(patternDetectionResult);

        HomographyEstimationInput input = new HomographyEstimationInput();
        input.setGoodPointsSelectionResult(goodPointsSelected);
        input.setReferenceCorners(cornersDetectionInput.getReferenceCorners());

        HomographyEstimationResult homographyEstimationResult = this.homographyEstimator.estimate(input);

        Mat sceneCorners = homographyEstimationResult.getCandidateSceneCorners();
        if(Imgproc.isContourConvex(this.convertToMatOfPoint(sceneCorners))){
            result.setAreCornersDetected(true);
            result.setSceneCorners(this.convertToMatOfPoint2f(sceneCorners));
        }

        return result;
    }

    private MatOfPoint convertToMatOfPoint(Mat mat){
        MatOfPoint matOfPoint = new MatOfPoint();
        mat.convertTo(matOfPoint, CvType.CV_32S);
        return matOfPoint;
    }

    private MatOfPoint2f convertToMatOfPoint2f(Mat mat){
        MatOfPoint2f matOfPoint2f = new MatOfPoint2f();
        double[] sceneCorner0 =
                mat.get(0, 0);
        double[] sceneCorner1 =
                mat.get(1, 0);
        double[] sceneCorner2 =
                mat.get(2, 0);
        double[] sceneCorner3 =
                mat.get(3, 0);
        matOfPoint2f.fromArray(
                new Point(sceneCorner0[0], sceneCorner0[1]),
                new Point(sceneCorner1[0], sceneCorner1[1]),
                new Point(sceneCorner2[0], sceneCorner2[1]),
                new Point(sceneCorner3[0], sceneCorner3[1]));
        return matOfPoint2f;
    }
}
