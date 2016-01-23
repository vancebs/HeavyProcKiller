package com.hf.heavyprockiller;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.kill);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startKillService();
            }
        });
    }

    private void startKillService() {
        Intent serviceIntent = new Intent(this, KillerService.class);
        serviceIntent.setAction(KillerService.ACTION_KILL);
        startService(serviceIntent);
    }
}
