package com.htc.MessageService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoStartNotifyReceiver extends BroadcastReceiver {
	
	private final String BOOT_COMPLETED_ACTION = "android.intent.action.BOOT_COMPLETED";

	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(BOOT_COMPLETED_ACTION)){
			Intent myIntent = new Intent(context, SMSOutboundService.class);
			context.startService(myIntent);
		}
	}
}
