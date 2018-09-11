package ar.edu.ub.ubapplication.factory;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point3;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.FeatureDetector;

import ar.edu.ub.ubapplication.detection.DefaultPattern;
import ar.edu.ub.ubapplication.detection.FeaturePattern;

import static org.opencv.features2d.FeatureDetector.ORB;

public class DefaultPatternFactory implements PatternFactory<FeaturePattern> {

    private static final double DEFAULT_DATA_VALUE = 0.0;
    private FeatureDetector featureDetector;
    private DescriptorExtractor descriptorExtractor;

    public DefaultPatternFactory() {
        featureDetector = FeatureDetector.create(ORB);
        descriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.ORB);
    }

    @Override
    public FeaturePattern create(int id, Mat image) {
        DefaultPattern pattern = new DefaultPattern();
        pattern.setId(id);
        MatOfKeyPoint keyPoints = this.getKeyPoints(image);
        pattern.setKeyPoints(keyPoints);
        pattern.setDescriptors(this.getDescriptors(image, keyPoints));
        pattern.setCornersIn2d(this.createReferenceCornersIn2d(image));
        pattern.setCornersIn3d(this.createReferenceCornersIn3d(image));
        pattern.setImageInGray(image);
        return pattern;
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

    private Mat createReferenceCornersIn2d(Mat input) {
        Mat referenceCorners = new Mat(4, 1, CvType.CV_32FC2);

        double width = input.cols();
        double height = input.rows();

        referenceCorners.put(0, 0, DEFAULT_DATA_VALUE, DEFAULT_DATA_VALUE);
        referenceCorners.put(1, 0, width, DEFAULT_DATA_VALUE);
        referenceCorners.put(2, 0, width, height);
        referenceCorners.put(3, 0, DEFAULT_DATA_VALUE, height);

        return referenceCorners;
    }

    private MatOfPoint3f createReferenceCornersIn3d(Mat input){
        MatOfPoint3f referenceCorners = new MatOfPoint3f();

        double width = input.cols();
        double height = input.rows();

        double maxSize = Math.max(width, height);
        double unitW = width / maxSize;
        double unitH = height / maxSize;

        referenceCorners.fromArray(
                new Point3(-unitW, -unitH, DEFAULT_DATA_VALUE),
                new Point3( unitW, -unitH, DEFAULT_DATA_VALUE),
                new Point3( unitW,  unitH, DEFAULT_DATA_VALUE),
                new Point3(-unitW,  unitH, DEFAULT_DATA_VALUE));

        return referenceCorners;
    }
}
