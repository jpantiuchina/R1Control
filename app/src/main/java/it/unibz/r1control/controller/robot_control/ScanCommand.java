package it.unibz.r1control.controller.robot_control;

import android.util.Log;

import java.io.IOException;

import it.unibz.r1control.model.data.InfraRedData;
import it.unibz.r1control.model.data.MagnetometerData;
import it.unibz.r1control.model.data.MotorControlData;
import it.unibz.r1control.model.data.MotorSpeed;
import it.unibz.r1control.model.data.RobotState;
import it.unibz.r1control.model.data.SensorValues;
import it.unibz.r1control.model.data.TemperatureData;
import it.unibz.r1control.model.data.UltrasonicData;

/**
 * Implements the "scan" command. Sends the current requested speed to the device and parses the
 * response data as {@link SensorValues}. Updates the {@link RobotState} with requested speed and
 * sensor data.
 * {@link RobotState}.
 *
 * Created by Matthias on 18.12.2015.
 */
public class ScanCommand extends Command<SensorValues> {

    private static final int ANSWER_SIZE = 60;

    private static final boolean DEBUG = false;

    private final byte[] data = {0x5A, 0x04, MotorSpeed.STAY, MotorSpeed.STAY};
    private final SpeedController speedCtrl;
    private MotorSpeed requestedSpeed;

    public ScanCommand(BluetoothConnection conn, Consumer<SensorValues> onResult, SpeedController speedCtrl) throws IOException {
        super(conn, ANSWER_SIZE, onResult);
        this.speedCtrl = speedCtrl;
    }

    @Override
    protected byte[] getData() {
        requestedSpeed = speedCtrl.getRequestedSpeed();
        data[2] = requestedSpeed.left();
        data[3] = requestedSpeed.right();
        return data;
    }

    @Override
    protected SensorValues onAnswerReceived(byte[] answer) {
        SensorValues values = new SensorValues();

        if (DEBUG) {
            answer[6] = 0;
            answer[7] = 1;
        }

        values.setUsData(0, new UltrasonicData(answer[0], answer[1]));
        values.setUsData(1, new UltrasonicData(answer[2], answer[3]));
        values.setUsData(2, new UltrasonicData(answer[4], answer[5]));
        values.setUsData(3, new UltrasonicData(answer[6], answer[7]));
        values.setUsData(4, new UltrasonicData(answer[8], answer[9]));
        values.setUsData(5, new UltrasonicData(answer[10], answer[11]));
        values.setUsData(6, new UltrasonicData(answer[12], answer[13]));
        values.setUsData(7, new UltrasonicData(answer[14], answer[15]));
        //values.setLockBits(answer[16]);
        values.setTmpData(new TemperatureData(
                answer[17], answer[18], answer[19], answer[20], answer[21],
                answer[22], answer[23], answer[24], answer[25]
        ));
        values.setMgData(new MagnetometerData(answer[26], answer[27], answer[28], answer[29]));
        values.setMcData(new MotorControlData(
                answer[30], answer[31], answer[32], answer[33], answer[34], answer[35],
                answer[36], answer[37], answer[38], answer[39], answer[40]
        ));
        values.setIrData(0, new InfraRedData(answer[41], answer[42]));
        values.setIrData(1, new InfraRedData(answer[43], answer[44]));
        RobotState.instance.changeTo(requestedSpeed, values);
        Log.d("SensorValues", values.toString());
        return values;
    }
}
