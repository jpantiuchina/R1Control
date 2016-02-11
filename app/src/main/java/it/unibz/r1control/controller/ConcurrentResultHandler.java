package it.unibz.r1control.controller;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * ResultHandler that is safe for concurrent use
 * Created by Matthias on 04.02.2016.
 */
public class ConcurrentResultHandler<R> extends ResultHandler<R> {

    private final ConcurrentLinkedQueue<R> results;

    public ConcurrentResultHandler(int what, Consumer<R> consumer) {
        super(what, consumer);
        results = new ConcurrentLinkedQueue<>();
    }

    @Override
    protected void pushResult(R result) {
        results.add(result);
    }

    @Override
    protected R takeResult() {
        return results.poll();
    }
}
