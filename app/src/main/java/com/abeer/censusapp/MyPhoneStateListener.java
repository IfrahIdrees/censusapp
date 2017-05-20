package com.abeer.censusapp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class MyPhoneStateListener extends PhoneStateListener {
	public static Boolean phoneRinging = false;
	public static Boolean offhook = false;
	public static Boolean ideal = false;
	Context c;

	public MyPhoneStateListener(Context con) {
		// TODO Auto-generated constructor stub
		c = con;
	}
	
	public void writeToFile(String fileName,String str){
		FileOutputStream fos;
		try {
			fos=c.openFileOutput(fileName, Context.MODE_PRIVATE);
			byte[] b=str.getBytes();
			fos.write(b);
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	};
	public String readFromFile(String fileName){
		String temp=null;
		FileInputStream fin;
		try {
			fin = c.openFileInput(fileName);
			byte[] b = new byte[fin.available()];
			fin.read(b);
			String s = new String(b);
			temp = s;
			fin.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return temp;
	}

	public void onCallStateChanged(int state, String incomingNumber) {
		
		
		switch (state) {
		case TelephonyManager.CALL_STATE_IDLE:
			ideal = true;
			phoneRinging = false;
			offhook = false;
			break;
		case TelephonyManager.CALL_STATE_OFFHOOK:
			ideal = false;
			phoneRinging = false;
			offhook = true;
			break;
		case TelephonyManager.CALL_STATE_RINGING:
			ideal = false;
			phoneRinging = true;
			offhook = false;
			break;
		}

		if (offhook){
			long lastOffHook=System.currentTimeMillis();
			//Log.e("here","FFoffhook: "+(System.currentTimeMillis()));
			String strLastOffHook="\n"+lastOffHook;
			String str=readFromFile("lastState");
			//Log.e("here","FFoffhook: "+str+" "+(System.currentTimeMillis()));
			if(str!=null && str.equals("oh")){}else{Log.e("here2","FFoffhook: "+str+" "+(System.currentTimeMillis()));
				FileOutputStream fos;
				try {
					fos=c.openFileOutput("lastOffHook", Context.MODE_APPEND);
					byte[] b=strLastOffHook.getBytes();
					fos.write(b);
					fos.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				writeToFile("lastState", "oh");
				
			}
			

		}

		if (ideal){
			long lastIdeal=System.currentTimeMillis();
			//Log.e("here","FFideal: "+(System.currentTimeMillis()));
			String strLastIdeal="\n"+lastIdeal;
			String str=readFromFile("lastState");
			//Log.e("here","FFideal: "+str+" "+(System.currentTimeMillis()));
			if(str!=null && str.equals("i")){}else{Log.e("here2","FFideal: "+str+" "+(System.currentTimeMillis()));
				FileOutputStream fos;
				try {
					fos=c.openFileOutput("lastIdeal", Context.MODE_APPEND);
					byte[] b=strLastIdeal.getBytes();
					fos.write(b);
					fos.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				writeToFile("lastState", "i");
			}
			
		}

		if(phoneRinging){
			long lastRinging=System.currentTimeMillis();
			//Log.e("here","FFringing: "+(System.currentTimeMillis()));
			String strLastRinging="\n"+lastRinging;
			String str=readFromFile("lastState");
			//Log.e("here","FFringing: "+str+" "+(System.currentTimeMillis()));
			if(str!=null && str.equals("r")){}else{Log.e("here2","FFringing: "+str+" "+(System.currentTimeMillis()));
			FileOutputStream fos;
				try {
					fos=c.openFileOutput("lastRinging", Context.MODE_APPEND);
					byte[] b=strLastRinging.getBytes();
					fos.write(b);
					fos.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				writeToFile("lastState", "r");
			}
			
			
		}
	}
}
