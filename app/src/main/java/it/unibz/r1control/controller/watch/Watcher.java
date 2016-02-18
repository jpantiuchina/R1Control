package it.unibz.r1control.controller.watch;

import android.content.Context;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import it.unibz.r1control.controller.Consumer;
import it.unibz.r1control.controller.ErrorConsumer;

/**
 * Executes and watches a task
 * Created by Matthias on 03.02.2016.
 */
public abstract class Watcher<V> implements Runnable {

    private final Context ctx;
    private final Consumer<V> onResult;
    private final ErrorConsumer onError;

    private final long timeout;
    private final TimeUnit unit;

    protected Watcher(Context ctx, Consumer<V> onResult, ErrorConsumer onError, long timeout, TimeUnit unit) {
        this.ctx = ctx;
        this.onResult = onResult;
        this.onError = onError;
        this.timeout = timeout;
        this.unit = unit;
    }

    public static WatcherBuilder builder(Context ctx) {
        return new WatcherBuilder(ctx);
    }

    protected abstract Future<V> startWatching() throws Exception;

    @Override
    public void run() {
        try {
            onResult.accept(startWatching().get(timeout, unit));
        } catch (Exception e){
            e.printStackTrace();
            if (onError != null)
                onError.onError(e);
        }
    }

}
