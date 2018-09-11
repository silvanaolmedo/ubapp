package ar.edu.ub.ubapplication;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.WindowManager;

import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.util.BitmapHelper;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.List;

import ar.edu.ub.ubapplication.camera.CameraProjectionAdapter;
import ar.edu.ub.ubapplication.rendering.ARRenderer;
import ar.edu.ub.ubapplication.rendering.JPCTRenderer;
import ar.edu.ub.ubapplication.rendering.TrackableLight;
import ar.edu.ub.ubapplication.rendering.TrackableObject3d;

import static android.graphics.PixelFormat.TRANSPARENT;
import static android.opengl.GLSurfaceView.RENDERMODE_WHEN_DIRTY;
import static android.view.View.VISIBLE;

public class ARActivity extends Activity implements CvCameraViewListener2{

    private static final String TAG = ARActivity.class.getSimpleName();
    private static final int CAMERAPERMISSIONREQUESTCODE = 1;
    private CameraProjectionAdapter cameraView;
    private GLSurfaceView glSurfaceView;
    private ARRenderer renderer;
    private boolean firstUpdate;

    static {
        if (!OpenCVLoader.initDebug())
            Log.d(TAG, "Unable to load OpenCV");
        else
            Log.d(TAG, "OpenCV loaded");
    }

    private BaseLoaderCallback loaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(final int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    Log.d(TAG, "OpenCV loaded successfully");
                    cameraView.enableView();
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        askForPermissions();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        cameraView = findViewById(R.id.openCvView);
        cameraView.setVisibility(VISIBLE);
        cameraView.setCvCameraViewListener(this);

        glSurfaceView = findViewById(R.id.glView);
        glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        renderer = new JPCTRenderer(this);
        glSurfaceView.setRenderer(renderer);
        glSurfaceView.setRenderMode(RENDERMODE_WHEN_DIRTY);
        glSurfaceView.getHolder().setFormat(TRANSPARENT);
        glSurfaceView.setZOrderMediaOverlay(true);
    }

    @Override
    protected void onDestroy() {
        if (cameraView != null) {
            cameraView.disableView();
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        if(cameraView != null){
            cameraView.disableView();
        }
        if(glSurfaceView != null){
            glSurfaceView.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!DefaultARProcessor.getInstance().initialize(ARActivity.this.getApplication(), cameraView, this.loaderCallback)){
            new AlertDialog.Builder(this)
                    .setMessage("The native library is not loaded. The application cannot continue.")
                    .setTitle("Error")
                    .setCancelable(true)
                    .setNeutralButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton){ finish(); }
                            })
                    .show();
            finish();
            return;
        }
        if(glSurfaceView != null){
            glSurfaceView.onResume();
        }
    }

    private void askForPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERAPERMISSIONREQUESTCODE);

        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        firstUpdate = true;
    }

    @Override
    public void onCameraViewStopped() {
        DefaultARProcessor.getInstance().cleanup();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat rgba = inputFrame.rgba();
        Log.i(TAG, "onCameraFrame");
        if (firstUpdate) {
            if (renderer.configureARScene()) {
                Log.i(TAG, "Scene configured successfully");
            } else {
                // Error
                Log.e(TAG, "Error configuring scene. Cannot continue.");
                finish();
            }
            firstUpdate = false;
        }

        if (DefaultARProcessor.getInstance().processFrame(rgba, rgba)) {

            // Update the renderer as the frame has changed
            if (glSurfaceView != null) glSurfaceView.requestRender();

        }
        return rgba;
    }
    public Camera.Parameters getCameraParameters(){
        return cameraView.getCameraParameters();
    }

    public List<TrackableObject3d> getTrackableObjects3D() {
        List<TrackableObject3d> objects = new ArrayList<>();
        this.populateObjects(objects);
        return objects;
    }

    private void populateObjects(List<TrackableObject3d> objects) {
        //TODO: check this method because after onResume state this list is empty.
        int id = DefaultARProcessor.getInstance().addPattern(this, R.drawable.ubcard);
        if(id != -1){
            Log.i(TAG,"Creating trackable light for object " + id);
            TrackableLight light = new TrackableLight();
            light.setIntensity(0, 0, 255);
            light.setPosition(new SimpleVector(0, 0, 100));

            Log.i(TAG,"Creating trackable object with id " + id);
            Object3D object3D = new Object3D(this.getCube());
            TrackableObject3d trackableObject3d = new TrackableObject3d(id, object3D);
            trackableObject3d.addLight(light);

            Log.i(TAG,"Adding trackable object with id " + id);
            objects.add(trackableObject3d);
        }
        else{
            Log.i(TAG,"Unable to create trackable object with id " + id);
        }
    }

    private Object3D getCube() {
        Object3D object3D = Primitives.getCube(1);
        if(!TextureManager.getInstance().containsTexture("texture")){
            Texture texture = new Texture(BitmapHelper.rescale(BitmapHelper.convert(ContextCompat.getDrawable(getApplicationContext(), R.drawable.icon)), 64, 64));
            TextureManager.getInstance().addTexture("texture", texture);
        }
        object3D.setTexture("texture");
        object3D.strip();
        object3D.build();
        // Cubes in jpct are rotated by 45 degrees when created.
        object3D.rotateY((float) Math.PI / 4);
        return object3D;
    }
}
