package ar.edu.ub.ubapplication.detection;

public enum ProcessorStatus {

    BASE_INITIALIZED(0), DETECTION_RUNNING(1), NOTHING_INITIALIZED(2);

    private int value;

    ProcessorStatus(int value){
        this.value = value;
    }
}
