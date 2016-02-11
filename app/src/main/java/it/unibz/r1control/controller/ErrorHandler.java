package it.unibz.r1control.controller;

import it.unibz.r1control.model.ErrorConsumer;

/**
 * ResultHandler that wraps an ErrorConsumer and implements the ErrorConsumer interface
 * Created by Matthias on 11.02.2016.
 */
class ErrorHandler extends ResultHandler<Exception> implements ErrorConsumer {

    /** Creates a new ResultHandler for the given message code and consumer. */
    protected ErrorHandler(int what, ErrorConsumer onError) {
        super(what, Consumers.from(onError));
    }

    public static ErrorHandler wrap(int what, ErrorConsumer onError) {
        return new ErrorHandler(what, onError);
    }

    @Override
    public void onError(Exception e) {
        accept(e);
    }

}
