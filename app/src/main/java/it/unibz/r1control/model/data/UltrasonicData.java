package it.unibz.r1control.model.data;

public class UltrasonicData {
	private short value;
	
	public UltrasonicData(byte highbyte, byte lowbyte) {
		value = (short)(highbyte << 8);
		value += lowbyte;
	}
	
	public short getValue() {
		return value;
	}
}
