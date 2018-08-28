package ar.edu.ub.ubapplication;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.support.v4.content.ContextCompat;

import com.threed.jpct.Camera;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Light;
import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;
import com.threed.jpct.util.BitmapHelper;
import com.threed.jpct.util.MemoryHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class DefaultRenderer implements ARRenderer, GLSurfaceView.Renderer{

    private Context context;
    private FrameBuffer frameBuffer;
    private World world;
    private Object3D cube = null;
    private Light sun = null;
    private boolean isPatternFound = false;

    public DefaultRenderer(Context context) {
        this.context = context;
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if(frameBuffer != null){
            frameBuffer.dispose();
        }

        frameBuffer = new FrameBuffer(gl, width, height);

        world = new World();
        world.setAmbientLight(20, 20, 20);

        sun = new Light(world);
        sun.setIntensity(250, 250, 250);

        Texture texture = new Texture(BitmapHelper.rescale(BitmapHelper.convert(ContextCompat.getDrawable(context, R.drawable.icon)), 64, 64));
        TextureManager.getInstance().addTexture("texture", texture);

        cube = Primitives.getCube(10);
        cube.calcTextureWrapSpherical();
        cube.setTexture("texture");
        cube.strip();
        cube.build();

        world.addObjects(cube);

        Camera cam = world.getCamera();
        cam.moveCamera(Camera.CAMERA_MOVEOUT, 50);
        cam.lookAt(cube.getTransformedCenter());

        SimpleVector sv = new SimpleVector();
        sv.set(cube.getTransformedCenter());
        sv.y -= 100;
        sv.z -= 100;
        sun.setPosition(sv);
        MemoryHelper.compact();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if(isPatternFound){
            frameBuffer.clear();
            world.renderScene(frameBuffer);
            world.draw(frameBuffer);
            frameBuffer.display();
        }
    }

    @Override
    public void isPatternFound(boolean patternFound) {
        this.isPatternFound = patternFound;
    }

    @Override
    public void setPosition(float x, float y) {

    }
}
