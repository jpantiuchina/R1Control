package it.unibz.r1control.model.data;

import it.unibz.r1control.util.Util;

public class InfraRedData {
	private int range;
	
	public InfraRedData(byte high, byte low) {
		range = Util.toInt(high, low);
	}
	
	public int getValue() {
		return range;
	}
}
