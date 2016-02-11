package it.unibz.r1control.controller;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * A Handler that wraps a Consumer and executes it on the main thread.
 *
 * Created by Matthias on 03.02.2016.
 */
public class ResultHandler<R> extends Handler implements Consumer<R> {

    private final int what;                 // message code
    private final Consumer<R> consumer;     // wrapped consumer

    private R result;       // current result

    /** Creates a new ResultHandler for the given message code and consumer. */
    public ResultHandler(int what, Consumer<R> consumer) {
        super(Looper.getMainLooper());
        this.what = what;
        if (consumer == null)
            throw new NullPointerException("Consumer is null");
        this.consumer = consumer;
    }

    /** Wraps the given consumer in a ResultHandler. */
    public static <R> ResultHandler<R> wrap(int what, Consumer<R> consumer) {
        return new ResultHandler<>(what, consumer);
    }

    /** Saves a new result to be processed. */
    protected void pushResult(R result) {
        this.result = result;
    }

    /** Takes a result for being processed. */
    protected R takeResult() {
        R r = result;
        result = null;
        return r;
    }

    /** Sends an empty message to process the given result. */
    public boolean sendResult(R result) {
        pushResult(result);
        return sendEmptyMessage(what);
    }

    @Override
    public void handleMessage(Message msg) {
        if (msg.what == what)
            consumer.accept(takeResult());
    }

    @Override
    public void accept(R value) {
        sendResult(value);
    }

}
