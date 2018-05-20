package ar.edu.ub.ubapplication.domain.matcher;

import android.util.Log;

import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.features2d.BFMatcher;
import org.opencv.features2d.DescriptorMatcher;

import ar.edu.ub.ubapplication.domain.detection.Pattern;
import ar.edu.ub.ubapplication.domain.extractor.FeatureExtractionResult;
import ar.edu.ub.ubapplication.domain.extractor.FeatureExtractor;

import static org.opencv.features2d.DescriptorMatcher.BRUTEFORCE_HAMMING;

/**
 * <p>Match an specific pattern in a target image using Brute Force Matcher with cross checked.</p>
 *
 * Created by Silvana Olmedo on 20/05/2018.
 */

public class PatternMatcher {

    private static final String TAG = PatternMatcher.class.getSimpleName();
    private static final int MINIMUM_MATCHES = 15;
    private FeatureExtractor featureExtractor;
    private DescriptorMatcher descriptorMatcher;

    public PatternMatcher(FeatureExtractor featureExtractor) {
        this.featureExtractor = featureExtractor;
        descriptorMatcher = BFMatcher.create(BRUTEFORCE_HAMMING, true);
    }

    public PatternMatchingResult match(Pattern pattern, Mat mat){
        PatternMatchingResult result = new PatternMatchingResult();
        FeatureExtractionResult targetFeatures = featureExtractor.extract(mat);

        Log.d(TAG, "Match scene descriptors to reference descriptors");
        MatOfDMatch matches = this.getMatches(targetFeatures.getDescriptors(), pattern.getReferenceDescriptors());

        result.setAreEnoughMatches(matches.toList().size() > MINIMUM_MATCHES);
        result.setMatches(matches);
        result.setSceneKeyPoints(targetFeatures.getKeyPoints());
        result.setReferenceKeyPoints(pattern.getReferenceKeyPoints());

        return result;
    }

    private MatOfDMatch getMatches(Mat sceneDescriptors, Mat referenceDescriptors){
        MatOfDMatch matches = new MatOfDMatch();
        descriptorMatcher.match(sceneDescriptors, referenceDescriptors, matches);
        return matches;
    }

}
