package ar.edu.ub.ubapplication.domain.detection.model;

import org.opencv.core.Mat;

import ar.edu.ub.ubapplication.domain.matcher.PatternMatchingResult;

/**
 * Stores the corners of the reference image and an object of {@link PatternMatchingResult}
 * Created by Silvana Olmedo on 21/05/2018.
 */

public class CornersDetectionInput {

    private Mat referenceCorners;
    private PatternMatchingResult patternMatchingResult;

    public Mat getReferenceCorners() {
        return referenceCorners;
    }

    public void setReferenceCorners(Mat referenceCorners) {
        this.referenceCorners = referenceCorners;
    }

    public PatternMatchingResult getPatternMatchingResult() {
        return patternMatchingResult;
    }

    public void setPatternMatchingResult(PatternMatchingResult patternMatchingResult) {
        this.patternMatchingResult = patternMatchingResult;
    }
}
