package com.abeer.censusapp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class settings extends Activity{

	Button save;
	EditText noOfBytes;
	EditText byteSize;
	EditText callSetupTime;
	EditText callDisconnectTime;
	EditText stateTime;
	EditText phoneNo;
	
	
	public void writeToFile(String fileName,String str){
		FileOutputStream fos;
		try {
			fos=openFileOutput(fileName, Context.MODE_PRIVATE);
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
			fin = openFileInput(fileName);
			byte[] b = new byte[fin.available()];
			fin.read(b);
			String s = new String(b);
			temp = s;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return temp;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		
		save=(Button) findViewById(R.id.bSave);
		noOfBytes=(EditText)findViewById(R.id.etNumberOfBytes);
		byteSize=(EditText)findViewById(R.id.etByteSize);
		callSetupTime=(EditText)findViewById(R.id.etCallSetupTime);
		callDisconnectTime=(EditText)findViewById(R.id.etCallDisconnectTime);
		stateTime=(EditText)findViewById(R.id.etStateTime);
		phoneNo=(EditText)findViewById(R.id.etPhoneNumber);
		
		noOfBytes.setText(readFromFile("noOfBytes"));
		byteSize.setText(readFromFile("byteSize"));
		callSetupTime.setText(readFromFile("callSetupTime"));
		callDisconnectTime.setText(readFromFile("callDisconnectTime"));
		stateTime.setText(readFromFile("stateTime"));
		phoneNo.setText(readFromFile("phoneNo"));
		
		save.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				writeToFile("noOfBytes", noOfBytes.getText().toString());
				writeToFile("byteSize", byteSize.getText().toString());
				writeToFile("callSetupTime", callSetupTime.getText().toString());
				writeToFile("callDisconnectTime", callDisconnectTime.getText().toString());
				writeToFile("stateTime", stateTime.getText().toString());
				writeToFile("phoneNo", phoneNo.getText().toString());
				
			}
		});
		
	}

}
