package it.unibz.r1control.controller.cmd;

import it.unibz.r1control.controller.BluetoothConnection;
import it.unibz.r1control.model.DistanceData;
import it.unibz.r1control.model.MagnetometerData;
import it.unibz.r1control.model.MotorControlData;
import it.unibz.r1control.model.MotorSpeed;
import it.unibz.r1control.model.RobotState;
import it.unibz.r1control.model.SensorValues;
import it.unibz.r1control.model.TemperatureData;

/**
 * Basic implementation of the SCAN command. Sends the current requested speed to the device and
 * parses the response data as {@link SensorValues}. Updates the {@link RobotState} with requested
 * speed and sensor data.
 * Provides several hook methods for subclasses to modify its behavior:<ul>
 *     <li>getRequestedSpeed: Returns the MotorSpeed to be sent.</li>
 *     <li>getSensorValues: Returns the SensorValues object to be updated by a SCAN command.</li>
 *     <li>update: Parses the answer and writes them into a SensorValues object.</li>
 * </ul>
 * {@link RobotState}.
 *
 * Created by Matthias on 18.12.2015.
 */
public class ScanCommand extends Command<SensorValues> {

    public ScanCommand(BluetoothConnection conn, int answerSize) {
        super(conn, answerSize);
        setSub(0x04);
    }

    protected MotorSpeed getRequestedSpeed() {
        return RobotState.instance.requestedSpeed();
    }

    protected SensorValues getSensorValues() {
        return RobotState.instance.sensorValues();
    }

    @Override
    protected void onInvoked() {
        MotorSpeed requestedSpeed = getRequestedSpeed();
        setCmdData(requestedSpeed.leftRaw(), requestedSpeed.rightRaw());
    }

    @Override
    protected SensorValues toResult(byte[] answer) {
        SensorValues values = getSensorValues();
        update(values, answer);
        return values;
    }

    protected void update(SensorValues values, byte[] answer) {
        int pos = 0;
        //Log.d("Answer", Arrays.toString(answer));
        //Log.d("SensorValues", values.toString());
        pos = readDist(answer, pos, values.getUsData(), false);
        pos++;      // Lock bits
        pos = readTemp(answer, pos, values.getTmpData());
        pos = readMagn(answer, pos, values.getMgData());
        pos = readMCtr(answer, pos, values.getMcData());
        readDist(answer, pos, values.getIrData(), true);
    }

    public static int irToDistance(int irVal) {
        return irVal > 3 ? 6787 / (irVal - 3) - 4 : 0;
    }

    private static int readDist(byte[] data, int pos, DistanceData[] objs, boolean ir) {
        for (int i = 0; i < objs.length; i++, pos += 2) {
            int val = SensorValues.toInt(data[pos], data[pos + 1]);
            objs[i].set(ir ? irToDistance(val) : val);
        }
        return pos;
    }

    private static int readTemp(byte[] data, int pos, TemperatureData obj) {
        byte b0 = data[pos++];
        byte b1 = data[pos++];
        byte b2 = data[pos++];
        byte b3 = data[pos++];
        byte b4 = data[pos++];
        byte b5 = data[pos++];
        byte b6 = data[pos++];
        byte b7 = data[pos++];
        byte b8 = data[pos++];
        obj.set(b0, b1, b2, b3, b4, b5, b6, b7, b8);
        return pos;
    }

    private static int readMagn(byte[] data, int pos, MagnetometerData obj) {
        byte b0 = data[pos++];
        byte b1 = data[pos++];
        byte b2 = data[pos++];
        byte b3 = data[pos++];
        obj.set(b0, b1, b2, b3);
        return pos;
    }

    private static int readMCtr(byte[] data, int pos, MotorControlData obj) {
        byte b0  = data[pos++];
        byte b1  = data[pos++];
        byte b2  = data[pos++];
        byte b3  = data[pos++];
        byte b4  = data[pos++];
        byte b5  = data[pos++];
        byte b6  = data[pos++];
        byte b7  = data[pos++];
        byte b8  = data[pos++];
        byte b9  = data[pos++];
        byte b10 = data[pos++];
        obj.set(b0, b1, b2, b3, b4, b5, b6, b7, b8, b9, b10);
        return pos;
    }
}
