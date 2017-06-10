package ch.fhnw.edu.emoba.spheropantherapp;

import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import ch.fhnw.edu.emoba.spherolib.SpheroRobotDiscoveryListener;
import ch.fhnw.edu.emoba.spherolib.SpheroRobotFactory;
import ch.fhnw.edu.emoba.spherolib.SpheroRobotProxy;

public class PairingActivity extends AppCompatActivity
    implements SpheroRobotDiscoveryListener {

    private static String TAG = PairingActivity.class.toString();

    private SpheroRobotProxy proxy;

    BluetoothAdapter bluetoothAdapter;

    final static int REQUEST_BLUETOOTH = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pairing);
    }

    @Override
    protected void onStart() {
        super.onStart();

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            finish();
        }else{
            if (bluetoothAdapter.isEnabled()) {
                connectToRobot();
            }else{
                Intent bluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(bluetoothIntent, REQUEST_BLUETOOTH);
            }

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_BLUETOOTH){
            if(resultCode==RESULT_OK){
                Toast.makeText(this, "BlueTooth Turned On", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            }
        }

        if (bluetoothAdapter.isEnabled()) {
            connectToRobot();
        }
    }

    private void connectToRobot() {
        Boolean onEmulator = Build.PRODUCT.startsWith("sdk");
        proxy = SpheroRobotFactory.createRobot(true);
        proxy.setDiscoveryListener(this);
        proxy.startDiscovering(getApplicationContext());
    }

    private void connectionFailedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.connectionFailedTitle);
        builder.setMessage(R.string.connectionFailedText);

        // Add the buttons
        builder.setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                proxy.startDiscovering(getApplicationContext());
            }
        });
        builder.setNegativeButton(R.string.quit, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });

        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void handleRobotChangedState(SpheroRobotBluetoothNotification type) {
        Log.i(TAG, type.toString());
        proxy.stopDiscovering();

        if (type == SpheroRobotBluetoothNotification.Online) {

            Log.i(TAG, "BlueTooth Connected. Starting Sheropanther...");

            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            finish();

        } else if(type == SpheroRobotBluetoothNotification.FailedConnect) {

            Log.e(TAG, "Connection failed.");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    connectionFailedDialog();
                }
            });

        }
    }
}
