package com.app.buna.dontdelay.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.app.buna.dontdelay.network.NetworkStatus;
import com.app.buna.dontdelay.R;


public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        if(!NetworkStatus.isConnectedInternet(this)) {
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    @Override
    public void onBackPressed() {

    }
}
