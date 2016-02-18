package it.unibz.r1control.controller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import it.unibz.r1control.R;

/**
 * Opens and manages a bluetooth connection to the robot.
 */
public class BluetoothConnection implements ErrorConsumer {

	private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

	private final String bluetoothName = "HC-06";
	private BluetoothAdapter btAdapter;
	private BluetoothSocket btSocket;
	private MainActivity main;
	
	public BluetoothConnection(MainActivity main) {
		this.main = main;
	}
	// Create a BroadcastReceiver for ACTION_FOUND
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if (bluetoothName.equals(device.getName())) {
					btAdapter.cancelDiscovery();
					connect(device);
				}
			}
		}
	};

	public BluetoothSocket getSocket() {
		return btSocket;
	}

	/**
	 * Enables bluetooth if necessary and scans for the target device.
	 * @see MainActivity#requestEnableBT()
	 * @see #scanForDevice()
	 */
	public void setupBluetooth() {
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		if (btAdapter == null)
			System.out.println("Device does not support Bluetooth");
		else if (!btAdapter.isEnabled())
			main.requestEnableBT();
		else
			scanForDevice();
	}

	/**
	 * Scans for the target device.
	 * @see #connect(BluetoothDevice)
	 */
	public void scanForDevice() {
		main.registerReceiver(mReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
		btAdapter.startDiscovery();
	}

	/**
	 * Connects to the given device and keeps communicating with it.
	 * @see Communicator#start()
	 */
	private void connect(BluetoothDevice device) {
		try {
			System.out.println("Connecting to: " + device.getName() + " at " + device.getAddress());
			btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
			btSocket.connect(); // Blocks until success or exception
			System.out.println("CONNECTED!");
			main.getCommunicator().start();
		} catch (IOException e) {
			e.printStackTrace();
			onError(e);
		}
	}

    private String errorMsg(Exception e) {
        return e instanceof InterruptedException ? main.getString(R.string.cmd_interrupted)
                : e instanceof TimeoutException ? main.getString(R.string.cmd_timeout)
                : e != null ? main.getString(R.string.cmd_error)
                : null;
    }

    /** Cancels the connection and closes the socket. */
    public synchronized void closeBluetoothConnection() {
        closeBluetoothConnection(null);
    }

    /** Cancels the connection and closes the socket. */
    public synchronized void closeBluetoothConnection(String reason) {
        Communicator c = main.getCommunicator();
        if (c != null) {
            c.stop();
        }
        if (btSocket != null && btSocket.isConnected()) {
            try {
                btSocket.close();
                btSocket = null;
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            main.unregisterReceiver(mReceiver);
        }
        main.stopped(reason);
    }

    @Override
    public void onError(Exception e) {
        closeBluetoothConnection(errorMsg(e));
    }
}
