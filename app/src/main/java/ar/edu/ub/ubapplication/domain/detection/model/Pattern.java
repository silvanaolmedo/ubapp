package ar.edu.ub.ubapplication.domain.detection.model;

import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint3f;

import ar.edu.ub.ubapplication.domain.extractor.FeatureExtractionResult;

/**
 * <p>Store the image data and computed descriptors of target pattern.</p>
 *
 * Created by Silvana Olmedo on 20/05/2018.
 */

public class Pattern {

    private int id;
    private FeatureExtractionResult featureExtractionResult;
    private Mat referenceCornersIn2d;
    private MatOfPoint3f referenceCornersIn3d;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public MatOfKeyPoint getReferenceKeyPoints() {
        return featureExtractionResult.getKeyPoints();
    }

    public Mat getReferenceDescriptors() {
        return featureExtractionResult.getDescriptors();
    }

    public Mat getReferenceCornersIn2d() {
        return referenceCornersIn2d;
    }

    public void setReferenceCornersIn2d(Mat referenceCornersIn2d) {
        this.referenceCornersIn2d = referenceCornersIn2d;
    }

    public MatOfPoint3f getReferenceCornersIn3d() {
        return referenceCornersIn3d;
    }

    public void setReferenceCornersIn3d(MatOfPoint3f referenceCornersIn3d) {
        this.referenceCornersIn3d = referenceCornersIn3d;
    }

    public void setFeatureExtractionResult(FeatureExtractionResult featureExtractionResult) {
        this.featureExtractionResult = featureExtractionResult;
    }
}
