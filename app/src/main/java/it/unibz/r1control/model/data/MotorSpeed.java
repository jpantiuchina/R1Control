package it.unibz.r1control.model.data;

/**
 *
 * Created by Matthias on 16.12.2015.
 */
public class MotorSpeed {

    public static final byte STAY = (byte)0x80;

    private volatile byte leftSpeed = STAY;
    private volatile byte rightSpeed = STAY;

    public byte getLeftSpeed() {
        return leftSpeed;
    }

    public void setLeftSpeed(byte speed) {
        leftSpeed = speed;
    }

    public byte getRightSpeed() {
        return rightSpeed;
    }

    public void setRightSpeed(byte speed) {
        rightSpeed = speed;
    }

    public void reset() {
        setLeftSpeed(STAY);
        setRightSpeed(STAY);
    }
}
