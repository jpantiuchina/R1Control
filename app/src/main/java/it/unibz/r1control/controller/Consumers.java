package it.unibz.r1control.controller;

import it.unibz.r1control.model.ErrorConsumer;

/**
 * Static methods for Consumers
 * Created by Matthias on 04.02.2016.
 */
public class Consumers {

    public static ErrorConsumer toErrorConsumer(final Consumer<Exception> consumer) {
        return new ErrorConsumer() {
            @Override
            public void onError(Exception e) {
                consumer.accept(e);
            }
        };
    }

    public static Consumer<Exception> from(final ErrorConsumer errorConsumer) {
        return new Consumer<Exception>() {
            @Override
            public void accept(Exception e) {
                errorConsumer.onError(e);
            }
        };
    }
}
