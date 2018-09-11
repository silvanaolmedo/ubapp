package ar.edu.ub.ubapplication.detection;

import org.opencv.core.Mat;

import java.util.List;

public interface PatternDetector<P extends Pattern> {

    void detect(List<P> patterns, Mat src, Mat dsp);

}
