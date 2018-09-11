package ar.edu.ub.ubapplication.rendering;

import android.opengl.GLES10;
import android.util.Log;

import com.threed.jpct.Camera;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Matrix;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.World;

import java.util.Arrays;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import ar.edu.ub.ubapplication.ARActivity;
import ar.edu.ub.ubapplication.DefaultARProcessor;

public class JPCTRenderer implements ARRenderer{

    private static final String TAG = JPCTRenderer.class.getSimpleName();
    private ARActivity arActivity;
    private FrameBuffer frameBuffer;
    private World world;
    private Camera camera;
    private Matrix projMatrix = new Matrix();
    private List<TrackableObject3d> trackableObjects;
    private boolean fovSet;

    public JPCTRenderer(ARActivity arActivity) {
        this.arActivity = arActivity;
    }

    @Override
    public boolean configureARScene() {
        world = new World();
        camera = world.getCamera();

        android.hardware.Camera.Parameters params = arActivity.getCameraParameters();
        // Setting the FOV based on the camera params, this seems to work fine with 640x480
        Log.i(TAG, "Setting FOV based on camera parameters");
        float fov = params.getHorizontalViewAngle();
        float yfov = params.getVerticalViewAngle();
        Log.i(TAG, "FOV "+fov);
        Log.i(TAG, "FOVY "+yfov);
        camera.setFOV(camera.convertDEGAngleIntoFOV(fov));
        camera.setYFOV(camera.convertDEGAngleIntoFOV(yfov));

        world.setAmbientLight(150, 150, 150);
        Log.i(TAG, "Get trackable objects from activity");
        trackableObjects = arActivity.getTrackableObjects3D();
        Log.i(TAG, String.format("Adding %s trackable object(s)", trackableObjects.size()));
        //
        Log.i(TAG, "Load all objects to the world");
        for (TrackableObject3d trackableObject: trackableObjects) {
            // Load the marker
            // Add the object to the world, note that mWorld.addObject is not recursive
            trackableObject.addToWorld(world);
        }

        world.buildAllObjects();
        return true;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES10.glClearColor(0.0f, 0.0f, 0.0f, 0.f);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if(frameBuffer != null){
            frameBuffer.dispose();
        }

        frameBuffer = new FrameBuffer(gl, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        frameBuffer.clear();

        if(DefaultARProcessor.getInstance().isRunning()){
            Log.i(TAG, "Setting the correct position and orientation from the camera");
            float[] projection = DefaultARProcessor.getInstance().getProjectionMatrix();
            Log.i(TAG, "ProjectionGL "+ Arrays.toString(projection));
            projMatrix.setDump(projection);
            projMatrix.transformToGL();
            SimpleVector translation = projMatrix.getTranslation();
            if (!fovSet) {
                // Calculate FOV based on projection values, but do it only once
                Log.i(TAG, "Calculating FOV based on projection values");
                float value1 = projection[5];
                float vFov = (float) Math.atan2(1, value1)*2;
                camera.setYFovAngle(vFov);
                float aspect = projection[5] / projection[0];
                float fov = (float) (2 * Math.atan2(camera.getYFOV() , 2 ) * aspect);
                camera.setFovAngle(fov);
                fovSet = true;
            }

            SimpleVector dir = projMatrix.getZAxis();
            SimpleVector up = projMatrix.getYAxis();
            camera.setPosition(translation);
            camera.setOrientation(dir, up);

            for(TrackableObject3d trackableObj: trackableObjects){
                Log.i(TAG, "Updating trackable objects");
                trackableObj.updateTransformation();
            }

            Log.i(TAG, "Rendering scene");
            world.renderScene(frameBuffer);
            world.draw(frameBuffer);
            frameBuffer.display();
        }

    }
}
