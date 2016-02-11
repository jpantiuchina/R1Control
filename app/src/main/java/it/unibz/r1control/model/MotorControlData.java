package it.unibz.r1control.model;

public class MotorControlData {
	private int leftEncoder;
	private int rightEncoder;
	private byte voltage;
	private byte leftCurrent;
	private byte rightCurrent;

	public void set(byte le1, byte le2, byte le3, byte le4, byte re1, byte re2, byte re3, byte re4, byte v, byte lc, byte rc) {
		leftEncoder  = (SensorValues.toInt(le1, le2) << 8) | SensorValues.toInt(le3, le4);
		rightEncoder = (SensorValues.toInt(re1, re2) << 8) | SensorValues.toInt(re3, re4);
		voltage      = v;
		leftCurrent  = lc;
		rightCurrent = rc;
	}

	public int getLeftEncoder() {
		return leftEncoder;
	}

	public int getRightEncoder() {
		return rightEncoder;
	}

	public byte getVoltage() {
		return voltage;
	}

	public byte getLeftCurrent() {
		return leftCurrent;
	}

	public byte getRightCurrent() {
		return rightCurrent;
	}

}
