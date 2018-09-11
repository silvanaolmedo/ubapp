package ar.edu.ub.ubapplication.rendering;

import com.threed.jpct.Light;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.World;

public class TrackableLight {

    private Light light;
    private SimpleVector position = new SimpleVector();
    private SimpleVector currentLocation = new SimpleVector();
    private int r, g, b;
    private boolean visible;

    public TrackableLight() {
    }

    public void setIntensity(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
        if (light != null) {
            light.setIntensity(r, g, b);
        }
    }

    public void setPosition(SimpleVector newPosition) {
        position.set(newPosition);
        if (light != null) {
            light.setPosition(position);
        }
    }

    public void setVisibility(boolean visible) {
        this.visible = visible;
        if (light != null) {
            if (visible) {
                light.enable();
            }
            else {
                light.disable();
            }
        }
    }

    public void addToWorld(World world) {
        light = new Light(world);
        setPosition(position);
        setIntensity(r, g, b);
        setVisibility(visible);
    }

    public void update(SimpleVector translation) {
        if (light != null) {
            currentLocation.set(position);
            currentLocation.add(translation);
            light.setPosition(currentLocation);
        }
    }
}
