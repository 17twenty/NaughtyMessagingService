package com.htc.MessageService;

import com.htc.MessageService.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class SMSActivity extends Activity {
    /** Called when the activity is first created. */
	static final String TAG = "SMSOutboundService";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

    	Log.w(TAG, "Starting service");
        startService(new Intent(this, SMSOutboundService.class));
    }
}