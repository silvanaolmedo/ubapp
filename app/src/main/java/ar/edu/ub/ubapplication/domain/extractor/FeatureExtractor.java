package ar.edu.ub.ubapplication.domain.extractor;

import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.FeatureDetector;

/**
 * <p>This class detects features in an image and extract their descriptors using ORB</p>
 * Created by Silvana Olmedo on 20/05/2018.
 */

public class FeatureExtractor {

    private FeatureDetector featureDetector;
    private DescriptorExtractor descriptorExtractor;

    public FeatureExtractor() {
        featureDetector = FeatureDetector.create(FeatureDetector.ORB);
        descriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.ORB);
    }

    /**
     * Detects features in an image and extract their descriptors.
     * @param mat in gray scale
     * @return an featureExtractionResult object with the keypoints and descriptors computed.
     */
    public FeatureExtractionResult extract(Mat mat){
        FeatureExtractionResult result = new FeatureExtractionResult();
        MatOfKeyPoint keyPoints = this.getKeyPoints(mat);
        result.setKeyPoints(keyPoints);
        result.setDescriptors(this.getDescriptors(mat, keyPoints));
        return result;
    }

    private MatOfKeyPoint getKeyPoints(Mat mat){
        MatOfKeyPoint matOfKeyPoint = new MatOfKeyPoint();
        featureDetector.detect(mat, matOfKeyPoint);
        return matOfKeyPoint;
    }

    private Mat getDescriptors(Mat mat, MatOfKeyPoint keyPoints){
        Mat descriptors = new Mat();
        descriptorExtractor.compute(mat, keyPoints, descriptors);
        return descriptors;
    }
}
