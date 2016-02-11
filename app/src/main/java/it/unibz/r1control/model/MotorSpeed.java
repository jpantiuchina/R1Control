package it.unibz.r1control.model;

import android.util.Log;

/**
 * Represents the speed of the robot. Internally works with logical speeds from -127 (backward) to
 * +128 (forward), where 0 means not turning. Provides methods {@link #leftRaw()} and
 * {@link #rightRaw()} for mapping logical values to raw values as requested by the robot.
 * Created by Matthias on 16.12.2015.
 */
public class MotorSpeed {

    public static final int STAY = 0x80;                   // raw speed for 0.
    public static final byte MAX_ABS_VAL = (byte)0x7F;     // maximum absolute value.

    private volatile int left;      // The left speed, from -127 to +127
    private volatile int right;     // The right speed, from -127 to +127

    /** Returns the speed of the left wheel. */
    public int left() {
        return left;
    }

    /** Returns the speed of the right wheel. */
    public int right() {
        return right;
    }

    /** Sets the given speeds. */
    public MotorSpeed set(int left, int right) {
        this.left = left;
        this.right = right;
        return this;
    }

    /** Returns the raw value for the left wheel. */
    public byte leftRaw() {
        byte res = (byte)(STAY + left);
        Log.d("leftRaw", left + " -> " + res);
        return res;
    }

    /** Returns the raw value for the right wheel. */
    public byte rightRaw() {
        return (byte)(STAY + right);
    }

    /** Move at the given speed. Equivalent to set(speed, speed). */
    public MotorSpeed move(int speed) {
        return set(speed, speed);
    }

    /** Rotate by the given speed. Positive is CW. */
    public MotorSpeed turn(int speed) {
        return set(speed, -speed);
    }

    /** Indicates whether some wheel is turning. */
    public boolean isMoving() {
        return left != 0 || right != 0;
    }

    /**
     * Indicates in the direction in which the robot is moving. Positive means forward, negative
     * means backward, and zero means standing or rotating.
     */
    public int getDirection() {
        int leftDir = Integer.signum(left);
        int rightDir = Integer.signum(right);
        return leftDir == rightDir ? leftDir : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MotorSpeed that = (MotorSpeed) o;

        return left == that.left && right == that.right;
    }

    @Override
    public int hashCode() {
        int result = left;
        result = 31 * result + right;
        return result;
    }

    public String toString() {
        return "MotorSpeed: (" + left + "," + right + ")";
    }

}
