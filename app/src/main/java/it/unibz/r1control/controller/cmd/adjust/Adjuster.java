package it.unibz.r1control.controller.cmd.adjust;

import it.unibz.r1control.model.MotorSpeed;
import it.unibz.r1control.model.SensorValues;

/**
 * Specifies how to adjust the MotorSpeed based on SensorValues
 * Created by Matthias on 18.12.2015.
 */
public interface Adjuster {
    MotorSpeed adjust(SensorValues values, MotorSpeed speed);
}
