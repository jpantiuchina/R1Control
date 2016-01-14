package it.unibz.r1control.controller.robot_control;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import it.unibz.r1control.model.data.SensorValues;

public class BluetoothConnection {

	private final int DELAY_MILLIS = 200;

	private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

	private final String bluetoothName = "HC-06";
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothSocket mmSocket;
	private MainActivity myActivity;

	private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
	
	public BluetoothConnection(MainActivity myActivity) {
		this.myActivity = myActivity;
	}
	// Create a BroadcastReceiver for ACTION_FOUND
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			// When discovery finds a device
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// Get the BluetoothDevice object from the Intent
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				// Add the name and address to an array adapter to show in a ListView
				if (bluetoothName.equals(device.getName())) {
					// Cancel discovery because it will slow down the connection
					mBluetoothAdapter.cancelDiscovery();
					connect(device);
				}
			}
		}
	};

	/** Connects to the given device and keeps communicating with it */
	private void connect(BluetoothDevice device) {
		BluetoothSocket tmp;
		// Get a BluetoothSocket to connect with the given BluetoothDevice
		try {
			System.out.println("Connecting to: " + device.getName() + " at " + device.getAddress());

			// MY_UUID is the app's UUID string, also used by the server code
			tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
		} catch (IOException e) {
			System.out.println("ConnectThread: " + e);
			mmSocket = null;
			return;
		}

		mmSocket = tmp;

		try {
			// Connect the device through the socket. This will block
			// until it succeeds or throws an exception
			mmSocket.connect();
			System.out.println("CONNECTED!");

			SpeedController speedCtrl = myActivity.getSpeedController();
			speedCtrl.start();

			Command<Void> getVersion = new GetVersionCommand(this, null);
			Command<SensorValues> scan = new ScanCommand(this, myActivity.getSensorView(), speedCtrl);

			scheduler.execute(getVersion);
			scheduler.scheduleAtFixedRate(scan, DELAY_MILLIS, DELAY_MILLIS, TimeUnit.MILLISECONDS);

		} catch (IOException connectException) {
			closeBluetoothConnection();
		}
	}

	public BluetoothSocket getSocket() {
		return mmSocket;
	}

	/** Will cancel an in-progress connection, and close the socket */
	public void closeBluetoothConnection() {
		try {
			scheduler.shutdown();
			if (mmSocket != null)
				mmSocket.close();
			myActivity.unregisterReceiver(mReceiver);
		} catch (IOException e) {
			// ignore
		}
	}

	public void setupBluetooth() {
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			// Device does not support Bluetooth
			System.out.println("Device does not support Bluetooth");
		}
		else if (!mBluetoothAdapter.isEnabled()) {
			System.out.println("Bluetooth is not enabled");
		}
		else {
			IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
			myActivity.registerReceiver(mReceiver, filter);
			mBluetoothAdapter.startDiscovery();
		}
	}
}
