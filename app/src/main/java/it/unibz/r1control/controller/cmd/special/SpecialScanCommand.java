package it.unibz.r1control.controller.cmd.special;

import android.util.Log;

import java.io.IOException;
import java.util.Arrays;

import it.unibz.r1control.controller.BluetoothConnection;
import it.unibz.r1control.controller.cmd.ScanCommand;
import it.unibz.r1control.model.DistanceData;
import it.unibz.r1control.model.SensorValues;

/**
 * Implements the SPECIAL SCAN command
 * Created by Matthias on 23.01.2016.
 */
public class SpecialScanCommand extends ScanCommand {

    private static final int ANSWER_SIZE = 22;  // 2 bit-sets + 8 * 2 US bytes + 2 * 2 IR bytes

    private int count;
    private int sum;

    public SpecialScanCommand(BluetoothConnection conn) {
        super(conn, ANSWER_SIZE);
        setSub(0x03);   // Use the unused sub-command slot 3
    }

    @Override
    protected byte[] receiveAnswer() throws IOException {
        byte[] answer = receiveAnswer(0, 2);
        int dataBytes = 2 * (Integer.bitCount(0xFF & answer[0]) + Integer.bitCount(0xFF & answer[1]));
        updateStats(2 + dataBytes);
        return receiveAnswer(2, dataBytes);
    }

    private void updateStats(int byteCount) {
        count++;
        sum += byteCount;
        Log.d("Stats", "last: " + byteCount + ", AVG: " + String.valueOf((float)sum / count));
    }

    @Override
    protected void update(SensorValues values, byte[] answer) {
        int pos = 2;
        pos = updateValues(values.getUsData(), answer, 0, pos);
        updateValues(values.getIrData(), answer, 1, pos);
        Log.d("Answer", Arrays.toString(answer));
        Log.d("SensorValues", values.toString());
    }

    private int updateValues(DistanceData[] values, byte[] answer, int bitSetPos, int dataPos) {
        for (int i = 0; i < values.length; i++) {
            if ((answer[bitSetPos] & (1 << i)) != 0) {
                values[i].set(answer[dataPos], answer[dataPos + 1]);
                dataPos += 2;
            }
        }
        return dataPos;
    }
}
