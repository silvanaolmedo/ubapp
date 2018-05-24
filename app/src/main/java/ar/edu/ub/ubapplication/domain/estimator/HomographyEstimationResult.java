package ar.edu.ub.ubapplication.domain.estimator;

import org.opencv.core.Mat;

/**
 * Stores
 * Created by Silvana Olmedo on 21/05/2018.
 */

public class HomographyEstimationResult {

    private boolean isHomographyEstimated;
    private Mat candidateSceneCorners;

    public boolean isHomographyEstimated() {
        return isHomographyEstimated;
    }

    public void setHomographyEstimated(boolean homographyEstimated) {
        isHomographyEstimated = homographyEstimated;
    }

    public Mat getCandidateSceneCorners() {
        return candidateSceneCorners;
    }

    public void setCandidateSceneCorners(Mat candidateSceneCorners) {
        this.candidateSceneCorners = candidateSceneCorners;
    }
}
