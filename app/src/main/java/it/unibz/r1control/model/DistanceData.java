package it.unibz.r1control.model;

/**
 * Represents a distance value from sensors such as ultrasonic or infrared sensors.
 * Uses a DistanceInfo object to provide further information on the distance.
 * Created by Matthias on 14.01.2016.
 */
public class DistanceData {

    private final DistanceInfo info;    // Info object

    private volatile int value;         // distance value in cm.

    /** Creates a new DistanceData object with the default info object. */
    public DistanceData() {
        this(DistanceInfo.getDefault());
    }

    /** Creates a new DistanceData object with the given info object. */
    public DistanceData(DistanceInfo info) {
        this.info = info;
    }

    /** Returns a distance in cm. */
    public int get() {
        return value;
    }

    /** Returns the DistanceInfo objects. */
    public DistanceInfo info() {
        return info;
    }

    /** Sets the given value. */
    public void set(byte hi, byte lo) {
        set(SensorValues.toInt(hi, lo));
    }

    /** Sets the given value. */
    public void set(int value) {
        this.value = value;
    }

    /** Returns a value from 0 to 1 indicating how dangerous the distance is. */
    public float getDanger() {
        return info.getDanger(this);
    }

    /** Indicates whether the distance is too close. */
    public boolean isTooClose() {
        return info.isTooClose(this);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
