package ch.fhnw.edu.emoba.spheropantherapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.BoringLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import layout.PairingFragment;

public class PairingActivity extends AppCompatActivity
    implements PairingFragment.OnFragmentInteractionListener {

    private static String TAG = PairingActivity.class.toString();

    private Boolean showStart = false;
    private BlueToothConnectThread connectionThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pairing);

        Button startButton = (Button) findViewById(R.id.startButton);
        startButton.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        connectionThread = new BlueToothConnectThread();
        connectionThread.execute();
    }

    private void handleBlueToothConnection(Boolean connected) {
        Log.i(TAG, "BlueTooth Connected: " + connected);

        if (connected) {
            Button startButton = (Button) findViewById(R.id.startButton);
            startButton.setVisibility(View.VISIBLE);

            ProgressBar connectionProgress = (ProgressBar) findViewById(R.id.connectionProgress);
            connectionProgress.setVisibility(View.INVISIBLE);

            TextView connectionText = (TextView) findViewById(R.id.connectionText);
            connectionText.setText("Successfully Connected");
        } else {
            AlertDialog dialog = connectionFailedDialog();
            dialog.show();
        }
    }

    private AlertDialog connectionFailedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.connectionFailedTitle);
        builder.setMessage(R.string.connectionFailedText);

        // Add the buttons
        builder.setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                connectionThread = new BlueToothConnectThread();
                connectionThread.execute();
            }
        });
        builder.setNegativeButton(R.string.quit, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });

        // Create the AlertDialog
        return builder.create();
    }

    @Override
    public void onStartListener() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        ActivityCompat.finishAffinity(this);
    }

    class BlueToothConnectThread extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            Boolean connected = false;
            Log.i(TAG, "Start Pairing BlueTooth...");

            try {
                // TODO: Connect BlueThooth
                Thread.sleep(1000);
                connected = true;
            } catch (InterruptedException e) {
                throw new RuntimeException("Could not connect to BlueTooth");
            }

            return connected;
        }

        @Override
        protected void onPostExecute(Boolean connected) {
            super.onPostExecute(connected);
            handleBlueToothConnection(connected);
        }
    }
}
