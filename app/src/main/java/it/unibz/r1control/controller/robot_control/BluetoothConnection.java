package it.unibz.r1control.controller.robot_control;

import it.unibz.r1control.model.data.InfraRedData;
import it.unibz.r1control.model.data.MagnetometerData;
import it.unibz.r1control.model.data.MotorControlData;
import it.unibz.r1control.model.data.SensorValues;
import it.unibz.r1control.model.data.TemperatureData;
import it.unibz.r1control.model.data.UltrasonicData;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.view.MotionEventCompat;

public class BluetoothConnection {
	private final String bluetoothName = "HC-06";
	private BluetoothDevice r1Device;
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothSocket mmSocket;
	private InputStream btInput;
	private OutputStream btOutput;
	private Activity myActivity;
	
	public BluetoothConnection(Activity myActivity) {
		this.myActivity = myActivity;
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
				if (device.getName().equals(bluetoothName)) {
					// Cancel discovery because it will slow down the connection
					mBluetoothAdapter.cancelDiscovery();

					r1Device = device;
					connect(device);
				}
			}
		}

		private void connect(BluetoothDevice device) {
			BluetoothSocket tmp = null;

			// Get a BluetoothSocket to connect with the given BluetoothDevice
			try {
				System.out.println("Connecting to: " + device.getName() + " at " + device.getAddress());

				// MY_UUID is the app's UUID string, also used by the server code
				tmp = device.createRfcommSocketToServiceRecord(device.getUuids()[0].getUuid());
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

				byte [] command = new byte[4];
				byte [] r1Data = new byte[1024];
				byte [] b = new byte[1];
				int numBytesRead;

				command[0] = 0x5A;
				command[1] = 0x02;
				command[2] = 0x00;
				command[3] = 0x00;

				btOutput.write(command);

				System.out.println("command sent to R1");

				numBytesRead = 0;
				while ((numBytesRead < 12) && (btInput.read(b) > 0)) {
					r1Data[numBytesRead++] = b[0];
					System.out.println(numBytesRead + " " + Byte.toString(b[0]));
				}
				System.out.println(numBytesRead + " bytes received from R1");

				Random r = new Random();

				while (true) {
					command[0] = 0x5A;
					command[1] = 0x04;
					command[2] = -1; // set to the recent speed value
					command[3] = -1; // set to the recent speed value

					btOutput.write(command);

					System.out.println("command sent to R1");

					numBytesRead = 0;
					while ((numBytesRead < 60) && (btInput.read(b) > 0)) {
						r1Data[numBytesRead++] = b[0];
					}
					System.out.println(numBytesRead + " bytes received from R1");

					SensorValues values = new SensorValues();
					values.setUsData(0, new UltrasonicData(r1Data[0], r1Data[1]));
					values.setUsData(1, new UltrasonicData(r1Data[2], r1Data[3]));
					values.setUsData(2, new UltrasonicData(r1Data[4], r1Data[5]));
					values.setUsData(3, new UltrasonicData(r1Data[6], r1Data[7]));
					values.setUsData(4, new UltrasonicData(r1Data[8], r1Data[9]));
					values.setUsData(5, new UltrasonicData(r1Data[10], r1Data[11]));
					values.setUsData(6, new UltrasonicData(r1Data[12], r1Data[13]));
					values.setUsData(7, new UltrasonicData(r1Data[14], r1Data[15]));
					values.setTmpData(new TemperatureData(r1Data[17], r1Data[18], r1Data[19], r1Data[20], r1Data[21], r1Data[22], r1Data[23], r1Data[24], r1Data[25]));
					values.setMgData(new MagnetometerData(r1Data[26], r1Data[27], r1Data[28], r1Data[29]));
					values.setMcData(new MotorControlData(r1Data[30], r1Data[31], r1Data[32], r1Data[33], r1Data[34], r1Data[35], r1Data[36], r1Data[37], r1Data[38], r1Data[39], r1Data[40]));
					values.setIrData(0, new InfraRedData(r1Data[41], r1Data[42]));
					values.setIrData(1, new InfraRedData(r1Data[43], r1Data[44]));
					
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} catch (IOException connectException) {
				// Unable to connect; close the socket and get out
				try {
					mmSocket.close();
				} catch (IOException closeException) { }
				return;
			}
		}
	};

	/** Will cancel an in-progress connection, and close the socket */
	public void closeBluetoothConnection() {
		try {
			mmSocket.close();
			myActivity.unregisterReceiver(mReceiver);
		} catch (IOException e) { }
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
