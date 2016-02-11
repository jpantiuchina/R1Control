package it.unibz.r1control.model;

/**
 * Provides information about distances. Indicates how dangerous a given distance is and whether it
 * is too close.
 *
 * Created by Matthias on 14.01.2016.
 */
public class DistanceInfo {

    public static final int DEF_MIN_SAFE_VALUE =  20;               // default minimum safe distance
    public static final float DEF_DECAY_RATE   = decayRate(100);    // default decay rate

    private static final DistanceInfo instance = new DistanceInfo(DEF_MIN_SAFE_VALUE, DEF_DECAY_RATE);

    private final int minSafeValue;     // minimum safe distance
    private final float decayRate;      // decay rate for computing danger values

    /** Creates a new DistanceInfo for the given miminum save distance and decay rate. */
    public DistanceInfo(int minSafeValue, float decayRate) {
        this.minSafeValue = minSafeValue;
        this.decayRate = decayRate;
    }

    /** Returns a default DistanceInfo instance. */
    public static DistanceInfo getDefault() {
        return instance;
    }

    /** Computes a decay rate that maps the given distance value to a danger value of 0.5. */
    public static float decayRate(int halfDanger) {
        return (float)Math.log(2) / halfDanger;
    }

    /** Returns the minimum safe distance. */
    public int minSafeValue() {
        return minSafeValue;
    }

    /** Returns a value from 0 to 1 indicating how dangerous the given distance value is. */
    public float getDanger(DistanceData data) {
        return getDanger(data.get());
    }

    /** Returns a value from 0 to 1 indicating how dangerous the given distance value is. */
    public float getDanger(int value) {
        return (float)Math.exp(-decayRate * value);
    }

    /** Indicates whether the given distance is too close. */
    public boolean isTooClose(DistanceData data) {
        return isTooClose(data.get());
    }

    /** Indicates whether the given distance is too close. */
    public boolean isTooClose(int value) {
        return value < (minSafeValue);
    }

}
