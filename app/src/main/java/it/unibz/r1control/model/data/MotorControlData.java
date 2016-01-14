package it.unibz.r1control.model.data;

public class MotorControlData {
	private int leftEncoder;
	private int rightEncoder;
	private byte voltage;
	private byte leftCurrent;
	private byte rightCurrent;
	
	public MotorControlData(byte le1, byte le2, byte le3, byte le4, byte re1, byte re2, byte re3, byte re4, byte v, byte lc, byte rc) {
		leftEncoder  = (le1 << 24) + (le2 << 16) + (le3 << 8) + le4;
		rightEncoder = (re1 << 24) + (re2 << 16) + (re3 << 8) + re4;

		voltage = v;
		leftCurrent = lc;
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
