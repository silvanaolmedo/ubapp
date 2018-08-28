package ar.edu.ub.ubapplication;

import android.content.Context;
import android.hardware.Camera;
import android.opengl.Matrix;

import org.opencv.android.JavaCameraView;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;

public class CameraProjectionAdapter extends JavaCameraView implements CameraAdapter, OpenGLProjection {

    private float fovY = 45f; // equivalent in 35mm photography: 28mm lens
    private float fovX = 60f; // equivalent in 35mm photography: 28mm lens
    private int heightPx = 480;
    private int widthPx = 640;
    private float near = 0.1f;
    private float far = 10f;
    private final float[] projectionGL = new float[16];
    private boolean projectionDirtyGL = true;

    private MatOfDouble projectionCV;
    private boolean projectionDirtyCV = true;


    public CameraProjectionAdapter(Context context, int cameraId) {
        super(context, cameraId);
    }

    @Override
    public Mat getCameraMatrix() {
        if (projectionDirtyCV) {
            if (projectionCV == null) {
                projectionCV = new MatOfDouble();
                projectionCV.create(3, 3, CvType.CV_64FC1);
            }


            Camera.Parameters parameters = mCamera.getParameters();
            Camera.Size size = parameters.getSupportedPictureSizes().get(0);

            fovY = parameters.getVerticalViewAngle();
            fovX = parameters.getHorizontalViewAngle();

            widthPx = size.width;
            heightPx = size.height;

            // Note that the FOV, image size, and focal length have
            // the following relationship:
            // diagonalFOV = 2 * atan(0.5 * diagonalPx / focalLengthPx)

            // Solving for the focal length:
            // focalLengthPx = 0.5 * diagonalPx / tan(0.5 * diagonalFOV)

            // Note that tan(0.5 * diagonalFOV) is the hypotenuse of
            // tan(0.5 * fovX) and tan(0.5 * fovY). Thus:
            // focalLengthPx = 0.5 * diagonalPx /
            //         sqrt((tan(0.5 * fovX))^2 + (tan(0.5 * fovY)^2))

            // Calculate focal length using the aspect ratio of the
            // FOV values reported by Camera.Parameters. This is not
            // necessarily the same as the image's current aspect
            // ratio, which might be a crop mode.
            final float fovAspectRatio = fovX / fovY;
            final double diagonalPx = Math.sqrt(
                    (Math.pow(widthPx, 2.0) +
                            Math.pow(widthPx / fovAspectRatio, 2.0)));
            final double focalLengthPx = 0.5 * diagonalPx / Math.sqrt(
                    Math.pow(Math.tan(0.5 * fovX * Math.PI / 180f), 2.0) +
                            Math.pow(Math.tan(0.5 * fovY * Math.PI / 180f), 2.0));

            projectionCV.put(0, 0, focalLengthPx);
            projectionCV.put(0, 1, 0.0);
            projectionCV.put(0, 2, 0.5 * widthPx);
            projectionCV.put(1, 0, 0.0);
            projectionCV.put(1, 1, focalLengthPx);
            projectionCV.put(1, 2, 0.5 * heightPx);
            projectionCV.put(2, 0, 0.0);
            projectionCV.put(2, 1, 0.0);
            projectionCV.put(2, 2, 1.0);
        }
        return projectionCV;
    }

    public float[] getProjectionGL() {
        if (projectionDirtyGL) {
            final float right =
                    (float)Math.tan(0.5f * fovX * Math.PI / 180f) * near;
            // Calculate vertical bounds based on horizontal bounds
            // and the image's aspect ratio. Some aspect ratios will
            // be crop modes that do not use the full vertical FOV
            // reported by Camera.Paremeters.
            final float top = right / getAspectRatio();
            Matrix.frustumM(projectionGL, 0, -right, right, -top, top, near, far);
            projectionDirtyGL = false;
        }
        return projectionGL;
    }

    public float getAspectRatio() {
        return (float)widthPx / (float)heightPx;
    }

}
