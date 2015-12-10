package it.unibz.r1control.model.data;

public class TemperatureData {
	private byte [] value;
	
	public TemperatureData(byte v1, byte v2, byte v3, byte v4, byte v5, byte v6, byte v7, byte v8, byte v9) {
		value = new byte[9];
		
		value[0] = v1;
		value[1] = v2;
		value[2] = v3;
		value[3] = v4;
		value[4] = v5;
		value[5] = v6;
		value[6] = v7;
		value[7] = v8;
		value[8] = v9;
	}
}