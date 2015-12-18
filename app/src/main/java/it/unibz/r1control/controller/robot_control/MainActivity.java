package it.unibz.r1control.controller.robot_control;

import it.unibz.r1control.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {
    private BluetoothConnection myConnection;
    private SpeedController speedCtrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        speedCtrl = new TouchSpeedController(findViewById(R.id.root));
        myConnection = new BluetoothConnection(this, speedCtrl);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myConnection.closeBluetoothConnection();
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