package it.unibz.r1control.model;

public class MagnetometerData {

	private volatile int bearing;
	private volatile byte pitch;
	private volatile byte roll;

	public void set(byte hi, byte lo, byte pitch, byte roll) {
		this.bearing = SensorValues.toInt(hi, lo);
		this.pitch = pitch;
		this.roll = roll;
	}
	
	public int getBearing() {
		return bearing;
	}
	
	public byte getPitch() {
		return pitch;
	}
	
	public byte getRoll() {
		return roll;
	}

}