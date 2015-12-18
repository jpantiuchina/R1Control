package it.unibz.r1control.controller.robot_control;

import it.unibz.r1control.model.data.MotorSpeed;

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
