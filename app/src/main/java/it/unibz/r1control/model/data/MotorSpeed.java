package it.unibz.r1control.model.data;

/**
 *
 * Created by Matthias on 16.12.2015.
 */
public class MotorSpeed {

    public static final byte STAY = (byte)0x80;

    private final byte left;
    private final byte right;

    public MotorSpeed(byte left, byte right) {
        this.left = left;
        this.right = right;
    }

    private static byte toUnsigned(int speed) {
        return (byte)(MotorSpeed.STAY + speed);
    }

    public static MotorSpeed fromSigned(int left, int right) {
        return new MotorSpeed(toUnsigned(left), toUnsigned(right));
    }

    public byte left() {
        return left;
    }

    public byte right() {
        return right;
    }

    public static boolean isBackward(byte speed) {
        return speed >= 0;
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
        int result = (int) left;
        result = 31 * result + (int) right;
        return result;
    }

    public String toString() {
        return "MotorSpeed: (" + left + "," + right + ")";
    }

}
