package it.unibz.r1control.model.data;

public class MagnetometerData {
	private short bearing;
	private byte pitch;
	private byte roll;
	
	public MagnetometerData(byte highbyte, byte lowbyte, byte pitch, byte roll) {
		bearing = (short)(highbyte << 8);
		bearing += lowbyte;
		
		this.pitch = pitch;
		this.roll = roll;
	}
	
	public short getBearing() {
		return bearing;
	}
	
	public byte getPitch() {
		return pitch;
	}
	
	public byte getRoll() {
		return roll;
	}
}