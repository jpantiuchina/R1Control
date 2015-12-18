package it.unibz.r1control.controller.robot_control;

import it.unibz.r1control.model.data.InfraRedData;
import it.unibz.r1control.model.data.MagnetometerData;
import it.unibz.r1control.model.data.MotorControlData;
import it.unibz.r1control.model.data.MotorSpeed;
import it.unibz.r1control.model.data.SensorValues;
import it.unibz.r1control.model.data.TemperatureData;
import it.unibz.r1control.model.data.UltrasonicData;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class BluetoothConnection {

	private final int HISTORY_CAPACITY = 1000;

	private final int DELAY_MILLIS = 100;

	private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

	private final String bluetoothName = "HC-06";
	private BluetoothDevice r1Device;
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothSocket mmSocket;
	private InputStream btInput;
	private OutputStream btOutput;
	private Activity myActivity;
	private SpeedController speedCtrl;

	private final List<SensorValues> history;
	private SensorValues currentValues;

	private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

	// Buffers for IO
	private final byte[] cmdVersion = {0x5A, 0x02, 0x00, 0x00};
	private final byte[] cmdScan    = {0x5A, 0x04, MotorSpeed.STAY, MotorSpeed.STAY};
	private final byte[] r1Data     = new byte[60];
	
	public BluetoothConnection(Activity myActivity, SpeedController speedCtrl) {
		this.myActivity = myActivity;
		this.speedCtrl = speedCtrl;
		this.history = new LinkedList<>();
		this.setupBluetooth();
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

					r1Device = device;
					connect(device);
				}
			}
		}
	};

	// Sends a command to retrieve the device's version and writes the answer to the command line.
	private final Runnable getVersion = new Runnable() {
		public void run() {
			try {
				writeCommand(cmdVersion);
				System.out.println("command sent to R1");

				int numBytesRead = readAnswer(12);
				for (int i = 0; i < numBytesRead; i++)
					System.out.println(i + " " + Byte.toString(r1Data[i]));
				System.out.println(numBytesRead + " bytes received from R1");
			} catch (IOException e) {
				closeBluetoothConnection();
			}
		}
	};

	// Sends a command to set the current speed to the device and writes the answer to the history.
	private final Runnable scanCycle = new Runnable() {
		public void run() {
			try {
				writeCommand(speedCtrl.getRequestedSpeed());
				//System.out.println("command sent to R1");
				//System.out.println("left: " + cmdScan[2] + ", right: " + cmdScan[3]);

				currentValues = new SensorValues();
				int numBytesRead = readValues();
				//System.out.println(numBytesRead + " bytes received from R1");

				if (history.size() >= HISTORY_CAPACITY)
					history.remove(0);
				history.add(currentValues);
			} catch (IOException e) {
				// ignore
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
			btInput = null;
			btOutput = null;
			return;
		}

		mmSocket = tmp;

		try {
			// Connect the device through the socket. This will block
			// until it succeeds or throws an exception
			mmSocket.connect();
			btInput = mmSocket.getInputStream();
			btOutput = mmSocket.getOutputStream();
			System.out.println("CONNECTED!");

			speedCtrl.start();

			scheduler.execute(getVersion);
			scheduler.scheduleAtFixedRate(scanCycle, DELAY_MILLIS, DELAY_MILLIS, TimeUnit.MILLISECONDS);

		} catch (IOException connectException) {
			closeBluetoothConnection();
		}
	}

	/** Returns the history of the last HISTORY_CAPACITY received sensor values. */
	public List<SensorValues> getHistory() {
		return history;
	}

	/** Returns the most recent sensor values. */
	public SensorValues getCurrentValues() {
		return currentValues;
	}

	/** Sends the given command to the device. */
	private void writeCommand(byte[] cmd) throws IOException {
		btOutput.write(cmd);
		btOutput.flush();
	}

	/** Sends the given input data to the device. */
	public void writeCommand(MotorSpeed data) throws IOException {
		cmdScan[2] = data.getLeftSpeed();
		cmdScan[3] = data.getRightSpeed();
		writeCommand(cmdScan);
	}

	/** Reads count bytes to the buffer and returns the number of bytes read. */
	private int readAnswer(int count) throws IOException {
		int pos = 0;
		while (pos < count) {
			pos += btInput.read(r1Data, pos, count - pos);
		}
		return pos;
	}

	/**
	 * Reads the most recent sensor values and writes them to currentValues.
	 * Returns the number of bytes read.
	 */
	public int readValues() throws IOException {
		int numBytesRead = readAnswer(60);

		currentValues.setUsData(0, new UltrasonicData(r1Data[0], r1Data[1]));
		currentValues.setUsData(1, new UltrasonicData(r1Data[2], r1Data[3]));
		currentValues.setUsData(2, new UltrasonicData(r1Data[4], r1Data[5]));
		currentValues.setUsData(3, new UltrasonicData(r1Data[6], r1Data[7]));
		currentValues.setUsData(4, new UltrasonicData(r1Data[8], r1Data[9]));
		currentValues.setUsData(5, new UltrasonicData(r1Data[10], r1Data[11]));
		currentValues.setUsData(6, new UltrasonicData(r1Data[12], r1Data[13]));
		currentValues.setUsData(7, new UltrasonicData(r1Data[14], r1Data[15]));
		currentValues.setTmpData(new TemperatureData(r1Data[17], r1Data[18], r1Data[19], r1Data[20], r1Data[21], r1Data[22], r1Data[23], r1Data[24], r1Data[25]));
		currentValues.setMgData(new MagnetometerData(r1Data[26], r1Data[27], r1Data[28], r1Data[29]));
		currentValues.setMcData(new MotorControlData(r1Data[30], r1Data[31], r1Data[32], r1Data[33], r1Data[34], r1Data[35], r1Data[36], r1Data[37], r1Data[38], r1Data[39], r1Data[40]));
		currentValues.setIrData(0, new InfraRedData(r1Data[41], r1Data[42]));
		currentValues.setIrData(1, new InfraRedData(r1Data[43], r1Data[44]));

		return numBytesRead;
	}

	/** Will cancel an in-progress connection, and close the socket */
	public void closeBluetoothConnection() {
		try {
			mmSocket.close();
			myActivity.unregisterReceiver(mReceiver);
			scheduler.shutdown();
		} catch (IOException e) {
			// ignore
		}
	}

	private void setupBluetooth() {
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

			r1Device = null;
			mBluetoothAdapter.startDiscovery();
		}
	}
}
