package it.unibz.r1control.controller.robot_control;

import it.unibz.r1control.model.data.MotorSpeed;
import it.unibz.r1control.model.data.SensorValues;

/**
 * Created by Matthias on 18.12.2015.
 */
public interface SensorReaction {
    MotorSpeed adjust(SensorValues values, byte leftSpeed, byte rightSpeed);
}
