package ar.edu.ub.ubapplication;

import fr.arnaudguyon.smartgl.opengl.SmartGLViewController;

/**
 * Created by Silvana Olmedo on 27/08/2018.
 */

public interface ARRenderer {

    void isPatternFound(boolean patternFound);

    void setPosition(float x, float y);

}
