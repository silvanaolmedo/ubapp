package ar.edu.ub.ubapplication.detection;

import android.util.Log;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.DMatch;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ar.edu.ub.ubapplication.camera.CameraProjectionAdapter;

public class DefaultPatternDetection implements PatternDetector<FeaturePattern> {

    private static final String TAG = DefaultPatternDetection.class.getSimpleName();
    private MatOfKeyPoint sceneKeypoints = new MatOfKeyPoint();
    // Descriptors of the scene's features.
    private Mat sceneDescriptors = new Mat();
    // Tentative corner coordinates detected in the scene, in pixels.
    private Mat candidatesceneCorners2D = new Mat(4, 1, CvType.CV_32FC2);
    // Good corner coordinates detected in the scene, in pixels.
    private MatOfPoint2f sceneCorners2D = new MatOfPoint2f();
    // The good detected corner coordinates, in pixels, as integers.
    private MatOfPoint intsceneCorners2D = new MatOfPoint();
    // A grayscale version of the scene.
    private Mat graySrc = new Mat();
    // Tentative matches of scene features and reference features.
    private MatOfDMatch matches = new MatOfDMatch();
    // A feature detector, which finds features in images.
    private FeatureDetector featureDetector = FeatureDetector.create(FeatureDetector.ORB);
    // A descriptor extractor, which creates descriptors of features.
    private DescriptorExtractor descriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.ORB);
    // A descriptor matcher, which matches features based on their descriptors.
    private DescriptorMatcher descriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMINGLUT);

    // Whether the target is currently detected.
    private boolean targetFound = false;
    private CameraProjectionAdapter cameraProjectionAdapter;

    // Distortion coefficients of the camera's lens.
    // Assume no distortion.
    private final MatOfDouble distCoeffs = new MatOfDouble(0.0, 0.0, 0.0, 0.0);

    // An adaptor that provides the camera's projection matrix.
    // The Euler angles of the detected target.
    private final MatOfDouble rVec = new MatOfDouble();
    // The XYZ coordinates of the detected target.
    private final MatOfDouble tVec = new MatOfDouble();
    // The rotation matrix of the detected target.
    private final MatOfDouble rotation = new MatOfDouble();
    // The OpenGL pose matrix of the detected target.
    private final float[] gLPose = new float[16];

    private final Scalar lineColor = new Scalar(0, 255, 0);

    private double centerX, centerY;

    public DefaultPatternDetection(CameraProjectionAdapter cameraProjectionAdapter) {
        this.cameraProjectionAdapter = cameraProjectionAdapter;
    }

    @Override
    public void detect(List<FeaturePattern> patterns, Mat src, Mat dst) {
        Log.i(TAG, String.format("Detecting %s pattern(s)", patterns.size()));
        Imgproc.cvtColor(src, graySrc, Imgproc.COLOR_RGBA2GRAY);
        featureDetector.detect(graySrc, sceneKeypoints);
        descriptorExtractor.compute(graySrc, sceneKeypoints, sceneDescriptors);

        for (FeaturePattern pattern : patterns) {
            this.detect(pattern);
            this.draw(pattern, src, dst);
        }
    }

    private void detect(FeaturePattern pattern){
        descriptorMatcher.match(sceneDescriptors, pattern.getDescriptors(), matches);
        this.findPose(pattern);
        int id = pattern.getId();
        Log.i(TAG, String.format("Setting pattern %s visibility %s", id, targetFound));
        pattern.setVisible(targetFound);
        float[] pose = this.getPose();
        Log.i(TAG, String.format("Setting pattern %s pose %s", id, Arrays.toString(pose)));
        pattern.setPose(pose);
    }

    private float[] getPose() {
        float[] pose = null;
        if(targetFound){
            pose = gLPose;
        }
        return pose;
    }

    private synchronized void findPose(FeaturePattern pattern) {
        List<DMatch> matchesList = matches.toList();
        if (matchesList.size() < 4) {
            // There are too few matches to find the homography.
            return;
        }

        List<KeyPoint> referenceKeypointsList = pattern.getKeypoints().toList();
        List<KeyPoint> sceneKeypointsList = sceneKeypoints.toList();

        // Calculate the max and min distances between keypoints.
        double maxDist = 0.0;
        double minDist = Double.MAX_VALUE;
        for (DMatch match : matchesList) {
            double dist = match.distance;
            if (dist < minDist) {
                minDist = dist;
            }
            if (dist > maxDist) {
                maxDist = dist;
            }
        }

        // The thresholds for minDist are chosen subjectively
        // based on testing. The unit is not related to pixel
        // distances; it is related to the number of failed tests
        // for similarity between the matched descriptors.
        if (minDist > 50.0) {
            // The target is completely lost.
            // Discard any previously found corners.
            targetFound = false;
            return;
        } else if (minDist > 25.0) {
            // The target is lost but maybe it is still close.
            // Keep any previously found corners.
            return;
        }

        // Identify "good" keypoints based on match distance.
        List<Point> goodReferencePointsList = new ArrayList<>();
        List<Point> goodScenePointsList = new ArrayList<>();
        double maxGoodMatchDist = 1.75 * minDist;
        for (DMatch match : matchesList) {
            if (match.distance < maxGoodMatchDist) {
                goodReferencePointsList.add(referenceKeypointsList.get(match.trainIdx).pt);
                goodScenePointsList.add(sceneKeypointsList.get(match.queryIdx).pt);
            }
        }

        if (goodReferencePointsList.size() < 4 || goodScenePointsList.size() < 4) {
            // There are too few good points to find the homography.
            return;
        }

        // There are enough good points to find the homography.
        // (Otherwise, the method would have already returned.)
        // Convert the matched points to MatOfPoint2f format, as
        // required by the Calib3d.findHomography function.
        MatOfPoint2f goodReferencePoints = new MatOfPoint2f();
        goodReferencePoints.fromList(goodReferencePointsList);
        MatOfPoint2f goodScenePoints = new MatOfPoint2f();
        goodScenePoints.fromList(goodScenePointsList);

        // Find the homography.
        Log.i(TAG, "Good reference points "+goodReferencePoints.dump());
        Log.i(TAG, "Good scene points "+goodScenePoints.dump());
        Mat homography = Calib3d.findHomography(goodReferencePoints, goodScenePoints, Calib3d.RANSAC, 5.0);
        // Use the homography to project the reference corner coordinates into scene coordinates.
        Log.i(TAG, "Homography "+homography.dump());
        Core.perspectiveTransform(pattern.getCornersIn2d(), candidatesceneCorners2D, homography);

        // Convert the scene corners to integer format, as required by the Imgproc.isContourConvex function.
        candidatesceneCorners2D.convertTo(intsceneCorners2D, CvType.CV_32S);

        // Check whether the corners form a convex polygon. If not,
        // (that is, if the corners form a concave polygon), the
        // detection result is invalid because no real perspective can
        // make the corners of a rectangular image look like a concave polygon!
        if (!Imgproc.isContourConvex(intsceneCorners2D)) {
            Log.i(TAG, "Contour is not concave");
            Log.i(TAG, "intSceneCorners2d "+intsceneCorners2D.dump());
            return;
        }

        final double[] sceneCorner0 = candidatesceneCorners2D.get(0, 0);
        final double[] sceneCorner1 = candidatesceneCorners2D.get(1, 0);
        final double[] sceneCorner2 = candidatesceneCorners2D.get(2, 0);
        final double[] sceneCorner3 = candidatesceneCorners2D.get(3, 0);
        sceneCorners2D.fromArray(
                new Point(sceneCorner0[0], sceneCorner0[1]),
                new Point(sceneCorner1[0], sceneCorner1[1]),
                new Point(sceneCorner2[0], sceneCorner2[1]),
                new Point(sceneCorner3[0], sceneCorner3[1]));

        Log.i(TAG, "Calculating center of pattern");
        Moments moments = Imgproc.moments(sceneCorners2D);
        centerX = (moments.get_m10() / moments.get_m00());
        centerY = (moments.get_m01() / moments.get_m00());
        Log.i(TAG, "CenterX: "+centerX);
        Log.i(TAG, "CenterY: "+centerY);
        pattern.setX((float)centerX);
        pattern.setY((float)centerY);

        final MatOfDouble projection = this.cameraProjectionAdapter.getCameraMatrix();
        Log.i(TAG, "Camera projection "+projection.dump());
        Log.i(TAG, "Scene corners 2d "+ Arrays.toString(sceneCorners2D.toArray()));
        Log.i(TAG, "Reference corners 3d "+ pattern.getCornersIn3d().dump());
        Log.i(TAG, "DistCoeffs "+ distCoeffs.dump());
        Log.i(TAG, "RVec "+ rVec.dump());
        Log.i(TAG, "TVec "+ tVec.dump());
        // Find the target's Euler angles and XYZ coordinates.
        Calib3d.solvePnP(pattern.getCornersIn3d(), sceneCorners2D,
                projection, distCoeffs, rVec, tVec);

        // Positive y is up in OpenGL, down in OpenCV.
        // Positive z is backward in OpenGL, forward in OpenCV.
        // Positive angles are counter-clockwise in OpenGL,
        // clockwise in OpenCV.
        // Thus, x angles are negated but y and z angles are
        // double-negated (that is, unchanged).
        // Meanwhile, y and z positions are negated.

        final double[] rVecArray = rVec.toArray();
        //rVecArray[0] *= -1.0; // negate x angle

        rVecArray[1] *= -1.0f;
        rVecArray[2] *= -1.0f;
        rVec.fromArray(rVecArray);
        Log.i(TAG, "New RVec: "+rVec.dump());

        // Convert the Euler angles to a 3x3 rotation matrix.
        Calib3d.Rodrigues(rVec, rotation);
        Log.i(TAG, "Rotation "+rotation.dump());

        final double[] tVecArray = tVec.toArray();

        // OpenCV's matrix format is transposed, relative to
        // OpenGL's matrix format.

        gLPose[0] = (float) rotation.get(0, 0)[0];
        gLPose[1] = (float) rotation.get(1, 0)[0];
        gLPose[2] = (float) rotation.get(2, 0)[0];
        gLPose[3] = 0f;
        gLPose[4] = (float) rotation.get(0, 1)[0];
        gLPose[5] = (float) rotation.get(1, 1)[0];
        gLPose[6] = (float) rotation.get(2, 1)[0];
        gLPose[7] = 0f;
        gLPose[8] = (float) rotation.get(0, 2)[0];
        gLPose[9] = (float) rotation.get(1, 2)[0];
        gLPose[10] = (float) rotation.get(2, 2)[0];
        gLPose[11] = 0f;
        gLPose[12] = (float) tVecArray[0];
        gLPose[13] = -(float) tVecArray[1]; // negate y position
        gLPose[14] = -(float) tVecArray[2]; // negate z position
        gLPose[15] = 1f;

        Log.i(TAG, "gLPose " +Arrays.toString(gLPose));
        targetFound = true;
    }

    private void draw(FeaturePattern pattern, final Mat src, final Mat dst) {

        if (dst != src) {
            src.copyTo(dst);
        }

        if (!targetFound) {
            // The target has not been found.

            // Draw a thumbnail of the target in the upper-left
            // corner so that the user knows what it is.

            // Compute the thumbnail's larger dimension as half the
            // video frame's smaller dimension.
            int height = pattern.getImage().height();
            int width = pattern.getImage().width();
            final int maxDimension = Math.min(dst.width(),
                    dst.height()) / 2;
            final double aspectRatio = width / (double)height;
            if (height > width) {
                height = maxDimension;
                width = (int)(height * aspectRatio);
            } else {
                width = maxDimension;
                height = (int)(width / aspectRatio);
            }

            // Select the region of interest (ROI) where the thumbnail
            // will be drawn.
            final Mat dstROI = dst.submat(0, height, 0, width);

            // Copy a resized reference image into the ROI.
            Imgproc.resize(pattern.getImage(), dstROI, dstROI.size(),
                    0.0, 0.0, Imgproc.INTER_AREA);

            return;
        }

        // Outline the found target in green.
        Imgproc.line(dst, new Point(sceneCorners2D.get(0, 0)),
                new Point(sceneCorners2D.get(1, 0)), lineColor, 4);
        Imgproc.line(dst, new Point(sceneCorners2D.get(1, 0)),
                new Point(sceneCorners2D.get(2, 0)), lineColor, 4);
        Imgproc.line(dst, new Point(sceneCorners2D.get(2, 0)),
                new Point(sceneCorners2D.get(3, 0)), lineColor, 4);
        Imgproc.line(dst, new Point(sceneCorners2D.get(3,0)),
                new Point(sceneCorners2D.get(0, 0)), lineColor, 4);

        Imgproc.circle(dst, new Point(centerX, centerY), 10, new Scalar(255, 0, 0), -1);
    }
}
