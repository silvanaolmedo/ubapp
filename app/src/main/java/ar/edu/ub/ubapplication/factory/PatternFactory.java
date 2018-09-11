package ar.edu.ub.ubapplication.factory;

import org.opencv.core.Mat;

import ar.edu.ub.ubapplication.detection.Pattern;

public interface PatternFactory<P extends Pattern> {

    /**
     * Creates a pattern object based on a Mat of the reference image
     * @param image reference image in gray scale
     * @param idResource an id for the reference image
     * @return a pattern object of the reference image
     */
    P create(int idResource, Mat image);
}
