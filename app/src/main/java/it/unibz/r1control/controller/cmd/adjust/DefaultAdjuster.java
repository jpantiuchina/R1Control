package it.unibz.r1control.controller.cmd.adjust;

import android.util.Log;

import it.unibz.r1control.model.DistanceData;
import it.unibz.r1control.model.MotorSpeed;
import it.unibz.r1control.model.SensorValues;

import static it.unibz.r1control.model.SensorValues.firstUsIndex;
import static it.unibz.r1control.model.SensorValues.nextUsIndex;

/**
 * An Adjuster implementation that looks at the current speed and ultrasonic sensors. If no sensor
 * value {@link DistanceData#isTooClose()}, the speed is limited depending on the nearest sensor
 * value in the current speeds direction. Otherwise, the robot tries to turn/move away from the
 * obstacles.
 *
 * Created by Matthias on 18.12.2015.
 */
public class DefaultAdjuster implements Adjuster {

    public static final int SPEED = 10;     // Speed value for adjustments, e.g. rotation

    private final MotorSpeed adjustedSpeed = new MotorSpeed();

    @Override
    public MotorSpeed adjust(SensorValues values, MotorSpeed speed) {
        if (values != null) {
            int us = values.getUsObstacles() & SensorValues.US_ALL;
            int face = face(speed);

            speed = (us & face) != 0
                    ? avoid(us, speed, face)
                    : slowDown(values, speed);
        }
        return speed;
    }

    /** Returns the face in the direction of the given speed. */
    public static int face(MotorSpeed speed) {
        int dir = speed.getDirection();
        return dir > 0 ? SensorValues.US_FRONT
                : dir < 0 ? SensorValues.US_BACK
                : SensorValues.US_ALL;

    }

    /**
     * Slow down the requested speed depending on the nearest sensor value in the current speed's
     * direction in order to have more time to avoid future collisions.
     */
    protected MotorSpeed slowDown(SensorValues values, MotorSpeed speed) {
        int face = face(speed);
        float maxDanger = 0;
        for (int i = firstUsIndex(face); i < SensorValues.US_COUNT; i = nextUsIndex(i, face)) {
            float danger = values.getUsData(i).getDanger();
            if (danger > maxDanger)
                maxDanger = danger;
        }
        if (maxDanger >= 0.5) {
            adjustedSpeed.set(slowDown(speed.left(), maxDanger), slowDown(speed.right(), maxDanger));
            Log.d("slowDown", speed + " -> " + adjustedSpeed);
            speed = adjustedSpeed;
        }
        return speed;
    }

    /** Limits the given speed value based on danger. */
    private static int slowDown(int speed, float danger) {
        int absSpeed = Math.min(Math.abs(speed), (int) ((1 - danger) * MotorSpeed.MAX_ABS_VAL));
        int res = speed < 0 ? -absSpeed : absSpeed;
        Log.d("slowDown", "limit " + speed + " by " + danger + " -> " + res);
        return res;
    }

    /** Avoid the obstacles indicated by bit vector us. */
    protected MotorSpeed avoid(int us, MotorSpeed speed, int face) {
        if (!speed.isMoving() && us != SensorValues.US_ALL) {
            int left = avoid(us, SensorValues.US_LEFT);
            int right = avoid(us, SensorValues.US_RIGHT);
            if (left != 0 || right != 0)
                speed = adjustedSpeed.set(left, right);
        } else if (face != SensorValues.US_ALL && (us & face) != 0){
            // Only move if other face is free
            boolean otherFaceFree = (us & (~face & SensorValues.US_ALL)) == 0;
            speed = adjustedSpeed.set(
                    otherFaceFree ? avoid(us, SensorValues.US_RIGHT) : 0,
                    otherFaceFree ? avoid(us, SensorValues.US_LEFT) : 0
            );
        }
        return speed;
    }

    /**
     * Returns a speed value for the given obstacle and side bit vectors.
     * If only in the front there is an obstacle, the returned value is a backward speed.
     * If only in the back there is an obstacle, the returned value is a forward speed.
     * Otherwise zero is returned.
     */
    private static int avoid(int us, int side) {
        int usSide = us & side;
        int res = usSide == (side & SensorValues.US_BACK) ? SPEED
                : usSide == (side & SensorValues.US_FRONT) ? -SPEED
                : 0;
        Log.d("avoid", Integer.toBinaryString(us) + "," + " -> " + Integer.toBinaryString(side)
                + " -> " + Integer.toBinaryString(usSide) + " -> " + res);
        return res;
    }

}
