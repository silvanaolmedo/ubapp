package ar.edu.ub.ubapplication.domain.factory;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint3f;

import ar.edu.ub.ubapplication.domain.detection.model.Pattern;
import ar.edu.ub.ubapplication.domain.extractor.FeatureExtractor;

/**
 * <p>Creates a {@link Pattern} object from the target or reference image.</p>
 * <p>This class finds the feature points and extract descriptors from the image
 * and stores them into a {@link Pattern} object.</p>
 * <p>It also builds the 2d and 3d corners of the reference image</p>
 *
 * Created by Silvana Olmedo on 20/05/2018.
 */

public class PatternFactory {

    private static final double DEFAULT_DATA_VALUE = 0.0;
    private FeatureExtractor featureExtractor;

    public PatternFactory(FeatureExtractor featureExtractor) {
        this.featureExtractor = featureExtractor;
    }

    /**
     * Creates a pattern object based on a Mat of the reference image
     * @param input reference image in gray scale
     * @param id an id for the reference image
     * @return a pattern object of the reference image
     */
    public Pattern create(Mat input, int id) {
        Pattern pattern = new Pattern();
        pattern.setId(id);
        pattern.setFeatureExtractionResult(featureExtractor.extract(input));
        pattern.setReferenceCornersIn2d(this.createReferenceCornersIn2d(input));
        pattern.setReferenceCornersIn3d(this.createReferenceCornersIn3d(input));
        return pattern;
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


        referenceCorners.put(0, 0, -unitW, -unitH, DEFAULT_DATA_VALUE);
        referenceCorners.put(1, 0, unitW, -unitH, DEFAULT_DATA_VALUE);
        referenceCorners.put(2, 0, unitW, unitH, DEFAULT_DATA_VALUE);
        referenceCorners.put(3, 0, -unitW, unitH, DEFAULT_DATA_VALUE);

        return referenceCorners;
    }
}
