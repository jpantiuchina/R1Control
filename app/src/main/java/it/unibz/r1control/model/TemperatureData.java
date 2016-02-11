package it.unibz.r1control.model;

public class TemperatureData {
	private volatile byte[] value;

	public TemperatureData() {
		value = new byte[9];
	}

	public void set(byte v0, byte v1, byte v2, byte v3, byte v4, byte v5, byte v6, byte v7, byte v8) {
		value[0] = v0;
		value[1] = v1;
		value[2] = v2;
		value[3] = v3;
		value[4] = v4;
		value[5] = v5;
		value[6] = v6;
		value[7] = v7;
		value[8] = v8;
	}
}