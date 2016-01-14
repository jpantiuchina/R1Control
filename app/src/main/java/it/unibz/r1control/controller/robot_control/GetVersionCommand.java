package it.unibz.r1control.controller.robot_control;

import java.io.IOException;
import java.util.Arrays;

import it.unibz.r1control.model.data.MotorSpeed;

/**
 * Created by Matthias on 18.12.2015.
 */
public class GetVersionCommand extends Command<Void> {

    private static final int ANSWER_SIZE = 12;

    private final byte[] data = {0x5A, 0x04, 0x00, 0x00};

    public GetVersionCommand(BluetoothConnection conn, Consumer<Void> onResult) throws IOException {
        super(conn, ANSWER_SIZE, onResult);
    }

    @Override
    public byte[] getData() {
        return data;
    }

    @Override
    protected Void onAnswerReceived(byte[] answer) {
        System.out.println("Got Version " + Arrays.toString(answer));
        return null;
    }

}
