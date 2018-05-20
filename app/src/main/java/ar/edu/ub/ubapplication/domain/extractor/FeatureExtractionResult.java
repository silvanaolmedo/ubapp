package ar.edu.ub.ubapplication.domain.extractor;

import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;

/**
 * <p>Store keypoints and descriptors computed with ORB</p>
 * Created by Silvana Olmedo on 20/05/2018.
 */

public class FeatureExtractionResult {

    private MatOfKeyPoint keyPoints;
    private Mat descriptors;

    public MatOfKeyPoint getKeyPoints() {
        return keyPoints;
    }

    public void setKeyPoints(MatOfKeyPoint keyPoints) {
        this.keyPoints = keyPoints;
    }

    public Mat getDescriptors() {
        return descriptors;
    }

    public void setDescriptors(Mat descriptors) {
        this.descriptors = descriptors;
    }
}
