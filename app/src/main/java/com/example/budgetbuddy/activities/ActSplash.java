package com.example.budgetbuddy.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.budgetbuddy.R;

public class ActSplash extends ActBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_splash);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!getUserId().isEmpty()) {
                    startActivity(new Intent(ActSplash.this, ActMain.class));
                } else {
                    startActivity(new Intent(ActSplash.this, ActLogin.class));
                }

                finish();
            }

        }, 2000);

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }


}