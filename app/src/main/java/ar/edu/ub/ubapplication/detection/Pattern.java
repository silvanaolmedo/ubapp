package ar.edu.ub.ubapplication.detection;

public interface Pattern {

    int getId();

    boolean isVisible();

    float[] getPose();

    void setVisible(boolean visible);

    void setPose(float[] pose);

}
