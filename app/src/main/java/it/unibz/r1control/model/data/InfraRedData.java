package it.unibz.r1control.model.data;

public class InfraRedData {
	private int range;
	
	public InfraRedData(byte signedHighByte, byte signedLowByte) {
		// http://stackoverflow.com/questions/7401550/how-to-convert-int-to-unsigned-byte-and-back
		int highByte = ((int) signedHighByte) & 0xFF;
		int lowByte  = ((int) signedLowByte ) & 0xFF;
		// merging high and low bytes to 16-bit integer
		range = (highByte << 8) + lowByte;
	}
	
	public int getValue() {
		return range;
	}
}
