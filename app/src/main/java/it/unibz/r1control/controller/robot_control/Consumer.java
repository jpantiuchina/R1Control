package it.unibz.r1control.controller.robot_control;

/**
 * Created by Matthias on 30.12.2015.
 */
public interface Consumer<V> {
    void accept(V value);
}
