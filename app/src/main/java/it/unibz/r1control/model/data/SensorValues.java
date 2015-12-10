package it.unibz.r1control.model.data;

public class SensorValues {
	private InfraRedData [] irData;
	private MagnetometerData mgData;
	private MotorControlData mcData;
	private TemperatureData tmpData;
	private UltrasonicData [] usData;
	
	public SensorValues() {
		irData = new InfraRedData[2];
		usData = new UltrasonicData[8];
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
	
	
}
