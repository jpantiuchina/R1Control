package it.unibz.r1control.controller.cmd.special;

import it.unibz.r1control.controller.BluetoothConnection;
import it.unibz.r1control.controller.cmd.GetVersionCommand;

/**
 * Implements the SPECIAL SCAN VERSIONS command
 * Created by Matthias on 02.02.2016.
 */
public class SpecialGetVersionCommand extends GetVersionCommand {

    public SpecialGetVersionCommand(BluetoothConnection conn) {
        super(conn);
        // Use a GET_VERSION command with all data bits set to 1.
        setCmdData(0xFF, 0xFF);
    }

    public static boolean isSpecial(byte[] answer) {
        boolean res = true;
        byte b = (byte)0xFF;
        for (int i = 0; res && i < answer.length; i++, b = (byte)~b)
            res = answer[i] == b;
        return res;
    }
}
