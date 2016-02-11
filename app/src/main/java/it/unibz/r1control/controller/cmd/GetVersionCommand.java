package it.unibz.r1control.controller.cmd;

import android.util.Log;

import java.util.Arrays;

import it.unibz.r1control.controller.BluetoothConnection;

/**
 * Implements the SCAN VERSIONS command
 * Created by Matthias on 18.12.2015.
 */
public class GetVersionCommand extends Command<byte[]> {

    private static final int ANSWER_SIZE = 12;

    public GetVersionCommand(BluetoothConnection conn) {
        super(conn, ANSWER_SIZE);
        setSub(0x02);
        // setCmdData(0x00, 0x00);
    }

    @Override
    protected byte[] toResult(byte[] answer) {
        Log.d("Version", Arrays.toString(answer));
        return answer;
    }

}
