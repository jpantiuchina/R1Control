package it.unibz.r1control.model.data;

public class InfraRedData {
	private short value;
	
	public InfraRedData(byte highbyte, byte lowbyte) {
		value = (short)(highbyte << 8);
		value += lowbyte;
	}
	
	public short getValue() {
		return value;
	}
}
