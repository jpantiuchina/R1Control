package it.unibz.r1control.model.data;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Matthias on 18.12.2015.
 */
public enum RobotState {
    instance;

    private volatile MotorSpeed currentSpeed;
    private volatile SensorValues currentSensorValues;

    public synchronized MotorSpeed getCurrentSpeed() {
        return currentSpeed;
    }

    public synchronized SensorValues getCurrentSensorValues() {
        return currentSensorValues;
    }

    public synchronized void changeTo(MotorSpeed requestedSpeed, SensorValues values) {
        currentSpeed = requestedSpeed;
        currentSensorValues = values;
    }

}
