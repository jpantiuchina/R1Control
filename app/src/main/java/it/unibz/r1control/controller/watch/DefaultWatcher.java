package it.unibz.r1control.controller.watch;

import android.content.Context;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import it.unibz.r1control.controller.Consumer;
import it.unibz.r1control.controller.ErrorConsumer;

/**
 * General Watcher implementation
 * Created by Matthias on 04.02.2016.
 */
class DefaultWatcher<R> extends Watcher<R> {

    private final Callable<Future<R>> getFuture;

    protected DefaultWatcher(Context ctx, Consumer<R> onResult, ErrorConsumer onError,
                          long timeout, TimeUnit unit, Callable<Future<R>> getFuture)
    {
        super(ctx, onResult, onError, timeout, unit);
        this.getFuture = getFuture;
    }

    @Override
    protected Future<R> startWatching() throws Exception {
        return getFuture.call();
    }
}
