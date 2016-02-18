package it.unibz.r1control.controller;

/**
 * Consumes an error
 * Created by Matthias on 04.02.2016.
 */
public interface ErrorConsumer {
    void onError(Exception e);
}
