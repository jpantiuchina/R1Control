package it.unibz.r1control.controller.watch;

import android.content.Context;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import it.unibz.r1control.controller.Consumer;
import it.unibz.r1control.controller.ErrorConsumer;

/**
 * Builder for a Watcher
 * Created by Matthias on 04.02.2016.
 */
public class WatcherBuilder {

    private final Context ctx;
    private ExecutorService executor;
    private ErrorConsumer onError;

    private long timeout;
    private TimeUnit unit;

    public WatcherBuilder(Context ctx) {
        this.ctx = ctx;
    }

    public WatcherBuilder executor(ExecutorService executor) {
        this.executor = executor;
        return this;
    }

    public WatcherBuilder onError(ErrorConsumer onError) {
        this.onError = onError;
        return this;
    }

    public WatcherBuilder timeout(long timeout, TimeUnit unit) {
        this.timeout = timeout;
        this.unit = unit;
        return this;
    }

    public <R> Watcher<R> watch(Callable<R> watched, Consumer<R> onResult) {
        return new ExecutorWatcher<>(ctx, onResult, onError, timeout, unit, executor, watched);
    }

    public <R> Watcher<R> build(Callable<Future<R>> getFuture, Consumer<R> onResult) {
        return new DefaultWatcher<>(ctx, onResult, onError, timeout, unit, getFuture);
    }

}
