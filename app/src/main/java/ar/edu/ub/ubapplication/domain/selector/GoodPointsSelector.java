package ar.edu.ub.ubapplication.domain.selector;

import android.util.Log;

import org.opencv.core.DMatch;
import org.opencv.core.KeyPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.List;

import ar.edu.ub.ubapplication.domain.matcher.PatternMatchingResult;

/**
 * <p>Selects good reference and scene points based on good matches</p>
 * Created by Silvana Olmedo on 21/05/2018.
 */

public class GoodPointsSelector {

    private static final String TAG = GoodPointsSelector.class.getSimpleName();
    private static final int MIN_POINTS_COUNT = 4;

    public GoodPointsSelectionResult select(PatternMatchingResult patternMatchingResult){
        Log.d(TAG, "Selecting good points of reference and scene images");

        List<DMatch> matches = patternMatchingResult.getMatches().toList();
        List<KeyPoint> referenceKeyPoints = patternMatchingResult.getReferenceKeyPoints().toList();
        List<KeyPoint> sceneKeyPoints = patternMatchingResult.getSceneKeyPoints().toList();

        return this.selectGoodPoints(matches, referenceKeyPoints, sceneKeyPoints);
    }

    private GoodPointsSelectionResult selectGoodPoints(List<DMatch> matches,
                                                       List<KeyPoint> referenceKeyPoints,
                                                       List<KeyPoint> sceneKeyPoints) {

        GoodPointsSelectionResult result = new GoodPointsSelectionResult();

        List<Point> goodReferencePoints = new ArrayList<>();
        List<Point> goodScenePoints = new ArrayList<>();

        double minDist = this.calculateMinimumDistance(matches);
        double maxGoodMatchDist = 1.75 * minDist;

        Log.d(TAG, "Selecting good reference and scene points based on distance: " + maxGoodMatchDist);
        for (DMatch match : matches) {
            if (match.distance < maxGoodMatchDist) {
                goodReferencePoints.add(referenceKeyPoints.get(match.trainIdx).pt);
                goodScenePoints.add(sceneKeyPoints.get(match.queryIdx).pt);
            }
        }

        result.setAreEnoughGoodPoints(goodReferencePoints.size() >= MIN_POINTS_COUNT
                                        && goodScenePoints.size() >= MIN_POINTS_COUNT);
        result.setGoodReferencePoints(this.transform(goodReferencePoints));
        result.setGoodScenePoints(this.transform(goodScenePoints));

        return result;
    }

    private MatOfPoint2f transform(List<Point> points){
        MatOfPoint2f matOfPoint2f = new MatOfPoint2f();
        matOfPoint2f.fromList(points);
        return matOfPoint2f;
    }

    private double calculateMinimumDistance(List<DMatch> matches) {
        Log.d(TAG, "Calculating the match with the minimum distance");
        double minDist = Double.MAX_VALUE;
        for (DMatch match : matches) {
            double distance = match.distance;
            if (distance < minDist) {
                minDist = distance;
            }
        }
        return minDist;
    }
}
