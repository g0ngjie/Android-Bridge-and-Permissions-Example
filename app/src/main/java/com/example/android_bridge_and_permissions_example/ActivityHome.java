package com.example.android_bridge_and_permissions_example;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ActivityHome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        View bridgeBtn = findViewById(R.id.bridge_jump);
        bridgeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == bridgeBtn.getId()) {
                    Intent intent = new Intent();
                    intent.setClass(ActivityHome.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });

        View mapBtn = findViewById(R.id.map_jump);
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == mapBtn.getId()) {
                    Intent intent = new Intent();
                    intent.setClass(ActivityHome.this, ActivityMap.class);
                    startActivity(intent);
                }
            }
        });
    }
}