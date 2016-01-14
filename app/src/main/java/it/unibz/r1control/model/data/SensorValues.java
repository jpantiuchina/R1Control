package it.unibz.r1control.model.data;

import java.util.Arrays;

public class SensorValues {
	public static final int IR_DATA_COUNT = 2;
	public static final int US_DATA_COUNT = 8;


	private InfraRedData [] irData;
	private MagnetometerData mgData;
	private MotorControlData mcData;
	private TemperatureData tmpData;
	private UltrasonicData [] usData;
	
	public SensorValues() {
		irData = new InfraRedData[IR_DATA_COUNT];
		usData = new UltrasonicData[US_DATA_COUNT];
	}

	public InfraRedData getIrData(int i) {
		return irData[i];
	}

	public MagnetometerData getMgData() {
		return mgData;
	}

	public MotorControlData getMcData() {
		return mcData;
	}

	public TemperatureData getTmpData() {
		return tmpData;
	}

	public UltrasonicData getUsData(int i) {
		return usData[i];
	}

	public void setIrData(int i, InfraRedData irData) {
		this.irData[i] = irData;
	}

	public void setMgData(MagnetometerData mgData) {
		this.mgData = mgData;
	}

	public void setMcData(MotorControlData mcData) {
		this.mcData = mcData;
	}

	public void setTmpData(TemperatureData tmpData) {
		this.tmpData = tmpData;
	}

	public void setUsData(int i, UltrasonicData usData) {
		this.usData[i] = usData;
	}

	@Override
	public String toString() {
		return "SensorValues: { "
			+ "usData: " + Arrays.toString(usData)
			+ "}";
	}
}
