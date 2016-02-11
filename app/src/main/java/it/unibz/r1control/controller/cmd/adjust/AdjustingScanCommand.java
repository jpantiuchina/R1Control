package it.unibz.r1control.controller.cmd.adjust;

import android.util.Log;

import it.unibz.r1control.controller.BluetoothConnection;
import it.unibz.r1control.controller.cmd.ScanCommand;
import it.unibz.r1control.model.MotorSpeed;
import it.unibz.r1control.model.RobotState;

/**
 * Implements a SCAN command that adjusts the current requested speed by an Adjuster before sending
 * it to the robot.
 *
 * Created by Matthias on 25.01.2016.
 */
public class AdjustingScanCommand extends ScanCommand {

    private static final int ANSWER_SIZE = 60;

    private final Adjuster adjuster;
    private long lastMillis;

    public AdjustingScanCommand(BluetoothConnection conn, Adjuster adjuster) {
        super(conn, ANSWER_SIZE);
        this.adjuster = adjuster;
    }

    /** Adjusts the requested speed using {@link #adjuster}. */
    @Override
    protected MotorSpeed getRequestedSpeed() {
        MotorSpeed speed = super.getRequestedSpeed();
        long millis = System.currentTimeMillis();
        Log.d("requestedSpeed", String.valueOf(millis - lastMillis));
        lastMillis = millis;
        if (adjuster != null)
            speed = adjuster.adjust(RobotState.instance.sensorValues(), speed);
        return speed;
    }

}
