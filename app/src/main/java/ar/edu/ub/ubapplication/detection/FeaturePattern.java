package ar.edu.ub.ubapplication.detection;

import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;

public interface FeaturePattern extends Pattern {

    Mat getImage();
    MatOfKeyPoint getKeypoints();
    Mat getDescriptors();
    Mat getCornersIn2d();
    MatOfPoint3f getCornersIn3d();
    MatOfPoint2f getSceneCorners();
    void setSceneCornersIn2d(MatOfPoint2f sceneCorners);
}
