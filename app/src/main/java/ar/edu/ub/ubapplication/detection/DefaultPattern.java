package ar.edu.ub.ubapplication.detection;

import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;

public class DefaultPattern implements FeaturePattern {

    private int id;
    private Mat imageInGray;
    private MatOfKeyPoint keyPoints;
    private Mat descriptors;
    private Mat cornersIn2d;
    private MatOfPoint3f cornersIn3d;
    private boolean visible;
    private float[] pose;
    private MatOfPoint2f sceneCorners;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public float[] getPose() {
        return pose;
    }

    public Mat getImage(){
        return imageInGray;
    }

    @Override
    public MatOfKeyPoint getKeypoints() {
        return this.keyPoints;
    }

    @Override
    public Mat getDescriptors() {
        return this.descriptors;
    }

    @Override
    public Mat getCornersIn2d() {
        return this.cornersIn2d;
    }

    @Override
    public MatOfPoint3f getCornersIn3d() {
        return this.cornersIn3d;
    }

    @Override
    public MatOfPoint2f getSceneCorners() {
        return this.sceneCorners;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMat(Mat mat) {
        this.imageInGray = mat;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setPose(float[] pose) {
        this.pose = pose;
    }

    public void setImageInGray(Mat imageInGray) {
        this.imageInGray = imageInGray;
    }

    public void setKeyPoints(MatOfKeyPoint keyPoints) {
        this.keyPoints = keyPoints;
    }

    public void setDescriptors(Mat descriptors) {
        this.descriptors = descriptors;
    }

    public void setCornersIn2d(Mat cornersIn2d) {
        this.cornersIn2d = cornersIn2d;
    }

    public void setCornersIn3d(MatOfPoint3f cornersIn3d) {
        this.cornersIn3d = cornersIn3d;
    }

    @Override
    public void setSceneCornersIn2d(MatOfPoint2f sceneCorners) {
        this.sceneCorners = sceneCorners;
    }
}
