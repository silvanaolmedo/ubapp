package ar.edu.ub.ubapplication;

import android.content.Context;

import org.opencv.android.LoaderCallbackInterface;
import org.opencv.core.Mat;

import ar.edu.ub.ubapplication.camera.CameraProjectionAdapter;

public interface ARProcessor {

    boolean initialize(Context context, CameraProjectionAdapter cameraProjectionAdapter, LoaderCallbackInterface loaderCallback);

    /**
     * Queries whether pattern detection is up and running. This will be true
     * after a call to initialize, and frames are being sent through. At
     * this point, pattern visibility and transformations can be queried.
     * @return	true if marker detection is running, false if not
     */
    boolean isRunning();

    /**
     * Adds a new pattern to the set of currently active patterns.
     * @param idResource
     * @return The unique identifier (UID) of the new pattern, or -1 on error
     */
    int addPattern(Context context, int idResource);

    /**
     * Takes an incoming frame from the opencv camera for conversion and pattern detection.
     * @param src
     * @param dst
     * @return
     */
    boolean processFrame(Mat src, Mat dst);

    /**
     * Returns whether the pattern with the specified ID is currently visible.
     * @param idPattern The unique identifier (UID) of the pattern to query.
     * @return true if the pattern is visible and tracked in the current video frame.
     */
    boolean isPatternFound(int idPattern);

    /**
     * Returns the transformation matrix for the specified pattern.
     * @param idPattern The unique identifier (UID) of the pattern to query.
     * @return Transformation matrix as an array of floats in OpenGL style.
     */
    float[] getPose(int idPattern);

    /**
     * Returns the projection matrix calculated from camera parameters.
     * @return Projection matrix as an array of floats in OpenGL style.
     */
    float[] getProjectionMatrix();

    void cleanup();
}
