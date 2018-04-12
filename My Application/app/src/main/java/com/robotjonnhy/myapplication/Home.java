package com.robotjonnhy.myapplication;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.content.Intent;
import android.widget.Toast;


public class Home extends AppCompatActivity {

    private ImageButton btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        btn = (ImageButton)findViewById(R.id.btnBluetooth);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this, DeviceListActivity.class));
            }
        });

//        btn.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
////                startActivity(new Intent(Home.this, DeviceListActivity.class));
//
//                Toast.makeText(getBaseContext(), "Turn on LED", Toast.LENGTH_SHORT).show();
//            }
//
//
//        });

    }






}
