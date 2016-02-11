package it.unibz.r1control.controller;

import it.unibz.r1control.model.MotorSpeed;

/**
 * Interface for a speed controller. Allows to check for the speed requested by the user.
 *
 * Created by Matthias on 17.12.2015.
 */
public interface SpeedController {
    /** Starts listening to user input. */
    void start();
    /** Returns the speed requested by the user most recently. */
    MotorSpeed getRequestedSpeed();
}
