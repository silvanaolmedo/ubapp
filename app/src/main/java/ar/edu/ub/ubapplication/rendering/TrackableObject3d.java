package ar.edu.ub.ubapplication.rendering;

import android.util.Log;

import com.threed.jpct.Matrix;
import com.threed.jpct.Object3D;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.World;

import ar.edu.ub.ubapplication.DefaultARProcessor;

public class TrackableObject3d {

    private static final String TAG = TrackableObject3d.class.getSimpleName();
    private int id;
    private Object3D model;
    private TrackableLight light;
    private boolean previousVisibility;
    private Matrix projMatrix = new Matrix();

    public TrackableObject3d(int id, Object3D model) {
        this.id = id;
        this.model = model;
        previousVisibility = false;
    }

    public void addToWorld(World world){
        world.addObject(model);
        light.addToWorld(world);
    }

    public void addLight(TrackableLight light) {
        this.light = light;
    }

    public void updateTransformation(){
        Log.i(TAG, String.format("Object3d %s checking visibility", id));
        boolean markerVisible = DefaultARProcessor.getInstance().isPatternFound(id);
        setVisibility(markerVisible);
        previousVisibility = markerVisible;
        if (markerVisible) {
            Log.i(TAG, String.format("Object3d %s checking pose %s", id, projMatrix));
            float[] transformation = DefaultARProcessor.getInstance().getPose(id);
            projMatrix.setDump(transformation);
            projMatrix.transformToGL();
            clearTranslation();
            translate(projMatrix.getTranslation());
            setRotationMatrix(projMatrix);
            // Also, update all the lights
            Log.i(TAG, String.format("Object3d %s updating light", id));
            light.update(projMatrix.getTranslation());
            light.setVisibility(true);
        }
    }

    private void setRotationMatrix(Matrix projMatrix) {
        Log.i(TAG, String.format("Object3d %s updating rotation matrix %s", id, projMatrix));
        this.model.setRotationMatrix(projMatrix);
    }

    private void translate(SimpleVector translation) {
        Log.i(TAG, String.format("Object3d %s updating translation %s", id, translation));
        this.model.translate(translation);
    }

    private void setVisibility(boolean visibility) {
        Log.i(TAG, String.format("Object3d %s updating visibility %s", id, visibility));
        this.model.setVisibility(visibility);
    }

    private void clearTranslation() {
        Log.i(TAG, String.format("Object3d %s clearing translation", id));
        this.model.clearTranslation();
    }
}
