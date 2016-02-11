package it.unibz.r1control.controller;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import it.unibz.r1control.controller.cmd.ScanCommand;
import it.unibz.r1control.controller.cmd.adjust.AdjustingScanCommand;
import it.unibz.r1control.controller.cmd.special.SpecialGetVersionCommand;
import it.unibz.r1control.controller.cmd.special.SpecialScanCommand;
import it.unibz.r1control.controller.watch.Watcher;
import it.unibz.r1control.controller.watch.WatcherBuilder;

/**
 * Implements the communication protocol between App and robot.
 *
 * Created by Matthias on 25.01.2016.
 */
public class Communicator {

    private final int TIME_INTERVAL = 100;
    private final int CANCEL_TIMEOUT = 15 * TIME_INTERVAL;
    private final int SHUTDOWN_TIMEOUT = CANCEL_TIMEOUT / 10;
    private final int ERROR_OCCURRED = 1;

    private final MainActivity main;
    private WatcherBuilder watcherBuilder;
    private ErrorHandler errorHandler;

    private ScheduledExecutorService scheduler;

    Communicator(final MainActivity main) {
        this.main = main;
        errorHandler = ErrorHandler.wrap(ERROR_OCCURRED, main.getBluetoothConnection());
    }

    private void init() {
        scheduler = Executors.newScheduledThreadPool(2);
        watcherBuilder = Watcher.builder(main)
                .executor(scheduler)
                .timeout(CANCEL_TIMEOUT, TimeUnit.MILLISECONDS)
                .onError(errorHandler);
    }

    /** Starts to periodically invoke the (appropriate) SCAN command. */
    private void scan(boolean special) {
        SpeedController speedCtrl = main.getSpeedController();
        speedCtrl.start();

        ScanCommand cmd = special
                ? new SpecialScanCommand(main.getBluetoothConnection())
                : new AdjustingScanCommand(main.getBluetoothConnection(), main.getAdjuster());
        Runnable task = watcherBuilder.watch(cmd, main.getSensorView());
        scheduler.scheduleAtFixedRate(task, 0, TIME_INTERVAL, TimeUnit.MILLISECONDS);
    }

    /**
     * Starts the communication. Request the RoboSpine version in order to identify an Arduino
     * in between, then scan periodically.
     */
    public void start() {
        if (scheduler == null || scheduler.isShutdown())
            init();
        // N.B.: When the version is received, the view is not updated
        // -> no need to consume  result on main thread
        // -> don't wrap consumer in a ResultHandler.
        scheduler.execute(watcherBuilder.watch(new SpecialGetVersionCommand(main.getBluetoothConnection()), new Consumer<byte[]>() {
            @Override
            public void accept(byte[] answer) {
                scan(SpecialGetVersionCommand.isSpecial(answer));
            }
        }));
    }

    /** Stops the communication. */
    public boolean stop() {
        boolean terminated = true;
        if (scheduler != null) {
            scheduler.shutdownNow();
            try {
                terminated = scheduler.awaitTermination(SHUTDOWN_TIMEOUT, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                terminated = false;
            }
        }
        return terminated;
    }

}
