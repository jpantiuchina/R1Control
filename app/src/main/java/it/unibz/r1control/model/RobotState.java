package it.unibz.r1control.model;

/**
 * Contains the current Robot State
 * Created by Matthias on 18.12.2015.
 */
public enum RobotState {
    instance;

    public static final int MAX_SAFE_SPEED = MotorSpeed.MAX_ABS_VAL / 4;

    private volatile int maxAbsSpeed = MAX_SAFE_SPEED;
    private MotorSpeed requestedSpeed = new MotorSpeed();
    private SensorValues sensorValues = new SensorValues();

    public void setTurboMode(boolean turbo) {
        this.maxAbsSpeed = turbo ? MotorSpeed.MAX_ABS_VAL : MAX_SAFE_SPEED;
    }

    public int maxAbsSpeed() {
        return maxAbsSpeed;
    }

    public MotorSpeed requestedSpeed() {
        return requestedSpeed;
    }

    public SensorValues sensorValues() {
        return sensorValues;
    }

}
