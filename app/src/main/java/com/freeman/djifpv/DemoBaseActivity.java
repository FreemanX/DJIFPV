package com.freeman.djifpv;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import dji.midware.data.manager.P3.ServiceManager;

public class DemoBaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_base);
    }

    @Override
    protected void onResume(){
        super.onResume();
        ServiceManager.getInstance().pauseService(false); // Resume the service
    }

    @Override
    protected void onPause() {
        super.onPause();
        ServiceManager.getInstance().pauseService(true); // Pause the service
    }

}
