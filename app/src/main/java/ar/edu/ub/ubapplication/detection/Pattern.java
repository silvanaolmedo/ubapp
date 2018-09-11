package ar.edu.ub.ubapplication.detection;

public interface Pattern {

    int getId();

    boolean isVisible();

    float[] getPose();

    float getX();

    float getY();

    void setVisible(boolean visible);

    void setPose(float[] pose);

    void setX(float x);

    void setY(float y);

}
