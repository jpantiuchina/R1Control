package it.unibz.r1control.controller.cmd;

import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;

import it.unibz.r1control.controller.BluetoothConnection;

/**
 * Abstract Command implementation. Takes care of the communication between the app and the device
 * for a single command. Implementing classes need to provide the actual command data and process
 * the answer by implementing the methods {@link #onInvoked} and {@link #toResult},
 * respectively. Further, the maximum byte count of the answer must be given to this class'
 * constructor.
 *
 * Created by Matthias on 18.12.2015.
 */
public abstract class Command<R> implements Callable<R> {

    private BluetoothConnection conn;
    private InputStream btIn;
    private OutputStream btOut;

    private final byte[] cmdData;
    private final byte[] answerData;

    public Command(BluetoothConnection conn, int answerSize) {
        this.conn = conn;
        this.cmdData = new byte[] {0x5A, 0x00, 0x00, 0x00};
        this.answerData = new byte[answerSize];
    }

    protected void setSub(int sub) {
        cmdData[1] = (byte)sub;
    }

    protected void setCmdData(int b1, int b2) {
        cmdData[2] = (byte)b1;
        cmdData[3] = (byte)b2;
    }

    /** Returns the data to send to the robot. */
    protected void onInvoked() {}

    /** Receives the answer from the robot and returns the corresponding result. */
    protected R toResult(byte[] answer) {
        return null;
    }

    @Override
    public R call() throws Exception {
        if (btIn == null || btOut == null) {
            BluetoothSocket socket = conn.getSocket();
            btIn = socket.getInputStream();
            btOut = socket.getOutputStream();
        }
        onInvoked();
        send(cmdData);
        receiveAnswer();
        return toResult(answerData);
    }

    /** Sends the given command to the device. */
    protected void send(byte[] cmd) throws IOException {
        //Log.d("Send", Arrays.toString(cmd));
        btOut.write(cmd);
        btOut.flush();
    }

    /**
     * Attempts to read count bytes and writes them to the buffer.
     */
    protected byte[] receiveAnswer() throws IOException {
        return receiveAnswer(0, answerData.length);
    }

    /**
     * Attempts to read count bytes and writes them to the buffer at position pos.
     */
    protected final byte[] receiveAnswer(int pos, int count) throws IOException {
        int relPos = 0;
        while (relPos < count)
            relPos += btIn.read(answerData, pos + relPos, count - relPos);
        return answerData;
    }

}
