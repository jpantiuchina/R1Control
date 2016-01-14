package it.unibz.r1control.model.data;

import it.unibz.r1control.util.Util;

public class UltrasonicData {

	public static final int MAX_SAFE_VALUE = 10000;
	public static final int MIN_SAFE_VALUE = 60;

	private volatile int value;
	
	public UltrasonicData(byte high, byte low) {
		value = Util.toInt(high, low);
	}
	
	public int getValue() {
		return value;
	}

	public boolean isTooClose() {
		return value < MIN_SAFE_VALUE;
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}
}
