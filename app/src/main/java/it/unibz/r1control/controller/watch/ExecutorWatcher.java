package it.unibz.r1control.controller.watch;

import android.content.Context;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import it.unibz.r1control.controller.Consumer;
import it.unibz.r1control.model.ErrorConsumer;

/**
 * Watcher using an ExecutorService and a Callable
 * Created by Matthias on 04.02.2016.
 */
class ExecutorWatcher<R> extends Watcher<R> {

    private final ExecutorService service;
    private final Callable<R> watched;

    protected ExecutorWatcher(Context ctx, Consumer<R> onResult, ErrorConsumer onError,
                              long timeout, TimeUnit unit,
                              ExecutorService service, Callable<R> watched)
    {
        super(ctx, onResult, onError, timeout, unit);
        this.service = service;
        this.watched = watched;
    }

    @Override
    protected Future<R> startWatching() throws Exception {
        return service.submit(watched);
    }
}
