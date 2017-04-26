package ch.fhnw.edu.emoba.spheropanther;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class PairingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pairing);
    }

    public void onGoOnClicked(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
