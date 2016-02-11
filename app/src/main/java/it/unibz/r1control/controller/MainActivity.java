package it.unibz.r1control.controller;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import it.unibz.r1control.R;
import it.unibz.r1control.controller.cmd.adjust.Adjuster;
import it.unibz.r1control.controller.cmd.adjust.DefaultAdjuster;
import it.unibz.r1control.model.RobotState;
import it.unibz.r1control.view.SensorView;

/**
 * Main activity for the application.
 */
public class MainActivity extends Activity {

    private static final int REQUEST_ENABLE_BT = 1234;
    private BluetoothConnection conn;
    private Communicator communicator;
    private TouchSpeedController speedCtrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        conn = new BluetoothConnection(this);
        communicator = new Communicator(this);
        speedCtrl = new TouchSpeedController(findViewById(R.id.root));
    }

    private void setVisibility(int id, int visibility) {
        View v = findViewById(id);
        if (v != null)
            v.setVisibility(visibility);
    }

    public void start(View v) {
        conn.setupBluetooth();
        setVisibility(R.id.start, View.INVISIBLE);
    }

    public void toggleTurbo(View v) {
        if (v instanceof Switch)
            RobotState.instance.setTurboMode(((Switch) v).isChecked());
    }

    public void stopped(String reason) {
        Toast.makeText(this, reason != null ? reason : getString(R.string.error), Toast.LENGTH_LONG).show();
        setVisibility(R.id.start, View.VISIBLE);
    }

    public void requestEnableBT() {
        startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK)
            conn.scanForDevice();
    }

    public SpeedController getSpeedController() {
        return speedCtrl;
    }

    public SensorView getSensorView() {
        return (SensorView)findViewById(R.id.sensor);
    }

    public Adjuster getAdjuster() {
        return new DefaultAdjuster();
    }

    public BluetoothConnection getBluetoothConnection() {
        return conn;
    }

    public Communicator getCommunicator() {
        return communicator;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (conn != null)
            conn.closeBluetoothConnection();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

}