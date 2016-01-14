package it.unibz.r1control.controller.robot_control;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import it.unibz.r1control.util.Util;

/**
 * Abstract Command implementation. Takes care of the communication between the app and the device
 * for a single command. Implementing classes need to provide the actual command data and process
 * the answer by implementing the methods {@link #getData} and {@link #onAnswerReceived},
 * respectively. Further, the byte count of the answer must be given to this class' constructor.
 *
 * Created by Matthias on 18.12.2015.
 */
public abstract class Command<R> implements Runnable {

    public class ResultHandler extends Handler {

        final Consumer<R> consumer;

        public ResultHandler(Consumer<R> consumer) {
            super(Looper.getMainLooper());
            this.consumer = consumer;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (consumer != null && msg.what == RESULT_AVAILABLE)
                consumer.accept(getResult());
        }

    }

    public static final int RESULT_AVAILABLE = 1;

    private BluetoothConnection conn;
    private InputStream btInput;
    private OutputStream btOutput;

    private final int answerSize;
    private final byte[] answer;
    private final Handler handler;

    private R result;

    public Command(BluetoothConnection conn, int answerSize, final Consumer<R> onResult) throws IOException {
        this.conn = conn;
        this.answerSize = answerSize;
        this.handler = onResult == null ? null : new ResultHandler(onResult);
        BluetoothSocket socket = conn.getSocket();
        this.btInput = socket.getInputStream();
        this.btOutput = socket.getOutputStream();
        this.answer = new byte[answerSize];
    }

    protected abstract byte[] getData();
    protected abstract R onAnswerReceived(byte[] answer);

    @Override
    public void run() {
        try {
            send(getData());
            receiveAnswer();
            result = onAnswerReceived(answer);
            if (handler != null)
                handler.sendEmptyMessage(RESULT_AVAILABLE);
        } catch (IOException e) {
            conn.closeBluetoothConnection();
        }
    }

    /** Sends the given command to the device. */
    protected void send(byte[] cmd) throws IOException {
        btOutput.write(cmd);
        btOutput.flush();
    }

    /** Reads count bytes to the buffer and returns the number of bytes read. */
    protected void receiveAnswer() throws IOException {
        int pos = 0;
        while (pos < answerSize)
            pos += btInput.read(answer, pos, answerSize - pos);
    }

    /** Returns and resets the most recent result. */
    protected R getResult() {
        R res = result;
        result = null;
        return res;
    }
}
