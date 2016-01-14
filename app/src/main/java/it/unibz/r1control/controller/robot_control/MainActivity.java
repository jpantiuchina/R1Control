package it.unibz.r1control.controller.robot_control;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import it.unibz.r1control.R;
import it.unibz.r1control.view.SensorView;

public class MainActivity extends Activity {

    private BluetoothConnection conn;
    private SpeedController speedCtrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        speedCtrl = new TouchSpeedController(findViewById(R.id.root), new RotationSensorReaction());
    }

    public void start(View v) {
        conn = new BluetoothConnection(this);
        conn.setupBluetooth();
        findViewById(R.id.start).setVisibility(View.INVISIBLE);
    }

    public BluetoothConnection getBluetoothConnection() {
        return conn;
    }

    public SpeedController getSpeedController() {
        return speedCtrl;
    }

    public SensorView getSensorView() {
        return (SensorView)findViewById(R.id.sensor);
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