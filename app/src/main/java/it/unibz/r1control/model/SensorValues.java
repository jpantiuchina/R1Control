package it.unibz.r1control.model;

import java.util.Arrays;

/**
 * Represents the set of all kinds of sensor values at a specific time.
 */
public class SensorValues {
	public static final int IR_COUNT = 2;
	public static final int US_COUNT = 8;

    // Flag constants
    public static final int US_BACK_LEFT   = 0x01;
    public static final int US_FRONT_LEFT  = 0x02;
    public static final int US_FRONT_RIGHT = 0x04;
    public static final int US_BACK_RIGHT  = 0x08;
    public static final int US_LEFT  = US_BACK_LEFT  | US_FRONT_LEFT;
    public static final int US_RIGHT = US_BACK_RIGHT | US_FRONT_RIGHT;
    public static final int US_BACK  = US_BACK_LEFT  | US_BACK_RIGHT;
    public static final int US_FRONT = US_FRONT_LEFT | US_FRONT_RIGHT;
    public static final int US_ALL   = US_LEFT | US_RIGHT;

    public static final int IR_LEFT  = 0x10;
    public static final int IR_RIGHT = 0x20;
    public static final int IR_ALL   = IR_LEFT | IR_RIGHT;

    private DistanceData[] irData;
	private DistanceData[] usData;

	private MagnetometerData mgData;
	private MotorControlData mcData;
	private TemperatureData tmpData;

	public SensorValues() {
		irData = new DistanceData[IR_COUNT];
		usData = new DistanceData[US_COUNT];
		init(irData);
		init(usData);
		mgData  = new MagnetometerData();
		mcData  = new MotorControlData();
		tmpData = new TemperatureData();
	}

    /** Initializes the given distance sensor values with new objects. */
	private static void init(DistanceData[] data) {
		for (int i = 0; i < data.length; i++)
			data[i] = new DistanceData();
	}

    /** Converts two bytes into an integer. */
	public static int toInt(byte high, byte low) {
		// http://stackoverflow.com/questions/7401550/how-to-convert-int-to-unsigned-byte-and-back
		int hi = (int)high & 0xFF;
		int lo = (int)low  & 0xFF;
		// merging high and low bytes to 16-bit integer
		return (hi << 8) + lo;
	}

    /** Returns the infrared sensor values. */
	public DistanceData[] getIrData() {
		return irData;
	}

    /** Returns the infrared sensor value at the given index. */
	public DistanceData getIrData(int i) {
		return irData[i];
	}

    /** Returns the magnetometer sensor value. */
	public MagnetometerData getMgData() {
		return mgData;
	}

    /** Returns the motor control sensor value. */
	public MotorControlData getMcData() {
		return mcData;
	}

    /** Returns the temperature sensor value. */
	public TemperatureData getTmpData() {
		return tmpData;
	}

    /** Returns the ultrasonic sensor values. */
	public DistanceData[] getUsData() {
		return usData;
	}

    /** Returns the ultrasonic sensor value at the given index. */
	public DistanceData getUsData(int i) {
		return usData[i];
	}

    /** Returns the index of the first US sensor value indicated by the given flags. */
    public static int firstUsIndex(int flags) {
        return 2 * Integer.numberOfTrailingZeros(flags);
    }

    /** Returns the index of the next US sensor value indicated by the given flags. */
    public static int nextUsIndex(int prev, int flags) {
        int next = prev + 1;
        if (next % 2 == 0)
            next += 2 * Integer.numberOfTrailingZeros(flags >>> (next >> 1));
        return next;
    }

    /** Returns a bit vector indicating which of the infrared sensor values changed. */
    public int getIrObstacles() {
        return (irData[0].isTooClose() ? IR_LEFT : 0)
                | (irData[1].isTooClose() ? IR_RIGHT : 0);
    }

    /**
     * Returns a bit vector indicating which of the ultrasonic sensor values is too close.
     * Each bit is a disjunction of two subsequent values, and thus represents a quarter of the
     * robots shape, such as US_FRONT_LEFT or US_BACK_RIGHT.
     */
    public int getUsObstacles() {
        int obstacles = 0;
        for (int i = 0, o = 0; i < US_COUNT; i += 2, o++) {
            if (usData[i].isTooClose() || usData[i + 1].isTooClose())
                obstacles |= 1 << o;
        }
        return obstacles;
    }

	@Override
	public String toString() {
		return "SensorValues: { "
			+ "usData: " + Arrays.toString(usData) + ", "
			+ "irData: " + Arrays.toString(irData)
			+ "}";
	}

}
