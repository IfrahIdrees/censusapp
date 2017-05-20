package com.abeer.censusapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;

public class Main2Activity extends Activity {

    int noOfBytes=10;
    int byteSize=15;
    int callSetupTime=2000;//new encode time(Change ASAP)
    int callDisconnectTime=4000;
    int stateTime=1000;
    String phoneNo="03004622096";//03084762381//03459307283//03124707981//03130440626

    //variables
    int encodeTime0=callSetupTime;
    int ensure = 0;
    int closed = 0;
    int callsLeft=noOfBytes*byteSize+1;
    //int data_time[] = {1000,1500,2000, 2500, 3000, 3500, 4000, 4500, 5000, 5500, 6000, 6500, 7000, 7500, 8000, 8500};
    //int data_time[] = {1000,1600,2200,2800,3400,4000,4600,5200,5800,6400,7000,7600,8200,8800,9400,10000};
    //int data_time[] = {1000,1700,2400,3100,3800,4500,5200,5900,6600,7300,8000,8700,9400,10100,10800,11500};
    int data_time[] = {1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000, 9000, 10000, 11000, 12000, 13000, 14000, 15000, 16000};
    //int data_time[] = {1000, 2500, 4000, 5500, 7000, 8500, 10000, 11500, 13000, 14500, 16000, 17500, 19000, 20500, 22000, 23500};
    int err_cor_sam_code[]={0,8,3,4,3,2,0,9,7,0,5,2,4,10,0,2,2,11,12,11,4,6,1,3,8,14,4,3,6,6,14,7,8,6,13,5,10,3,13,12,9,12,12,1,13,15,14,13,1,4,13,5,6,6,15,1,9,4,6,2,11,7,12,2,1,13,9,0,8,0,3,13,2,7,9,12,12,15,3,5,2,9,1,15,14,8,10,9,12,8,10,9,3,0,4,10,11,10,15,15,12,5,5,13,0,10,8,8,6,7,2,10,13,9,4,13,13,3,11,7,6,9,13,4,5,10,10,4,14,5,5,15,0,12,0,1,4,8,13,5,5,0,8,5};
    String freq_char_set="! etoainshrdlcumwfgypbvkjxqz0123456789****";
    String status="null";
    int call_locked=0;
    int waiting_for_call_connect;
    Method telephonyEndCall;
    Object telephonyObject;
    int receiverState=0;
    String quiz = "Suppose this is the question paper with answers: abcdabcd";
    String solution = "abcdabcd";
    ProgressBar progress;
    //utility functions
    public void askPermissions(){
        int permissionCheck = ContextCompat.checkSelfPermission(
                this, Manifest.permission.MODIFY_AUDIO_SETTINGS);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.MODIFY_AUDIO_SETTINGS)) {
                //showExplanation("Permission Needed", "Rationale", Manifest.permission.READ_PHONE_STATE, 1);
            } else {
                ActivityCompat.requestPermissions(Main2Activity.this,
                        new String[]{Manifest.permission.MODIFY_AUDIO_SETTINGS},
                        1);
            }
        } else {
            Toast.makeText(Main2Activity.this, "Permission (already) Granted!", Toast.LENGTH_SHORT).show();
        }

    }
    public int waitForCall(){
        //Log.e("->>1",""+System.currentTimeMillis()+"\n");
        waiting_for_call_connect=0;
        Visualizer mVisualizer = new Visualizer(0);
        mVisualizer.setEnabled(false);
        int capRate = Visualizer.getMaxCaptureRate();
        int capSize = Visualizer.getCaptureSizeRange()[1];
        mVisualizer.setCaptureSize(capSize);
        Visualizer.OnDataCaptureListener captureListener = new Visualizer.OnDataCaptureListener() {
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes,
                                              int samplingRate) {
                for (int i=0;i<bytes.length;i++) {
                    if (bytes[i]!=-128) {
                        //yes detected
                        waiting_for_call_connect=1;
                        //Log.e("*********",closed+" break "+waiting_for_call_connect+" "+bytes.length);
                        break;
                    }
                }

		    /*if(waiting_for_call_connect==1 && closed!=1){
		    	String s=new String(bytes);
		    	writeToFile("audio",s);
		    	File myDir=makeTimeDir();
		    	makeFile("audio", myDir);
		    	Log.e("**888888**","**888888**");
		    }*/

            }

            public void onFftDataCapture(Visualizer visualizer, byte[] bytes,
                                         int samplingRate) {
            }
        };

        int status2 = mVisualizer.setDataCaptureListener(captureListener,
                capRate, true/*wave*/, false/*no fft needed*/);
        mVisualizer.setEnabled(true);
        //Log.e("->>2",""+System.currentTimeMillis()+"\n");
        while(true){if(waiting_for_call_connect==1){break;}}
        //Log.e("->>3",""+System.currentTimeMillis()+"\n");
        mVisualizer.setEnabled(false);
        mVisualizer.release();
        return 1;

    }
    public void clearFile(String fileName){
        FileOutputStream fos;
        try {
            fos = openFileOutput(fileName, Context.MODE_PRIVATE);//"lastOffHook"
            String str = "";
            byte[] b = str.getBytes();
            fos.write(b);
            fos.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    public void makeFile(String fileName,File myDir){
        //make dir
        String temp = null;


        FileInputStream fin;
        try {
            fin = openFileInput(fileName);
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

        File file = new File(myDir, fileName+".txt");
        try {
            FileOutputStream out = new FileOutputStream(file);
            out.write(temp.getBytes());
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public File makeTimeDir(){
        String root = Environment.getExternalStorageDirectory().toString();
        long name=System.currentTimeMillis();
        File myDir = new File(root + "/experiemnts/"+name);
        myDir.mkdirs();
        return myDir;
    }

    public Method disconnectInitializer(){

        try {
            String serviceManagerName = "android.os.ServiceManager";
            String serviceManagerNativeName = "android.os.ServiceManagerNative";
            String telephonyName = "com.android.internal.telephony.ITelephony";
            Class<?> telephonyClass;
            Class<?> telephonyStubClass;
            Class<?> serviceManagerClass;
            Class<?> serviceManagerNativeClass;

            Object serviceManagerObject;
            telephonyClass = Class.forName(telephonyName);
            telephonyStubClass = telephonyClass.getClasses()[0];
            serviceManagerClass = Class.forName(serviceManagerName);
            serviceManagerNativeClass = Class.forName(serviceManagerNativeName);
            Method getService = // getDefaults[29];
                    serviceManagerClass.getMethod("getService", String.class);
            Method tempInterfaceMethod = serviceManagerNativeClass.getMethod(
                    "asInterface", IBinder.class);
            Binder tmpBinder = new Binder();
            tmpBinder.attachInterface(null, "fake");
            serviceManagerObject = tempInterfaceMethod.invoke(null, tmpBinder);
            IBinder retbinder = (IBinder) getService.invoke(
                    serviceManagerObject, "phone");
            Method serviceMethod = telephonyStubClass.getMethod("asInterface",
                    IBinder.class);
            telephonyObject = serviceMethod.invoke(null, retbinder);
            telephonyEndCall = telephonyClass.getMethod("endCall");


        } catch (Exception e) {
            e.printStackTrace();

        }
        return telephonyEndCall;
    }
    public void disconnectCall() {
        try {
            telephonyEndCall.invoke(telephonyObject);
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void call(View v) {
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+phoneNo));
                status="not done";

                //UI
                progress.setProgress(0);

                for (int test = 0; test < noOfBytes; test++) {
                    for (int count = 0; count < byteSize; count++) {
                        if (closed == 1) {
                            break;
                        }
                        long l=System.currentTimeMillis();
                        Log.e("here","1: "+(System.currentTimeMillis()-l));
                        startActivity(callIntent);
                        Log.e("here","2: "+(System.currentTimeMillis()-l));
						/*Runnable showDialogRun2 = new Runnable() {
				            public void run(){
				                Intent showDialogIntent2 = new Intent(getBaseContext(), temp.class);
				                startActivity(showDialogIntent2);
				            }
				        };
				        Handler h2 = new Handler(Looper.getMainLooper());
				        h2.postDelayed(showDialogRun2, 1000);*/
                        Log.e("here","3: "+(System.currentTimeMillis()-l));
                        waitForCall();
                        String str0=readFromFile("connect");
                        writeToFile("connect", str0+System.currentTimeMillis()+"\n");
                        Log.e("here","4: "+(System.currentTimeMillis()-l));

                        try {
                            //Thread.sleep(data_time[count]);
                            Thread.sleep(stateTime);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        Log.e("here","5: "+(System.currentTimeMillis()-l));

                        String str=readFromFile("disconnect");
                        writeToFile("disconnect", str+System.currentTimeMillis()+"\n");
                        Log.e("here","6: "+(System.currentTimeMillis()-l));
                        disconnectCall();
                        Log.e("here","7: "+(System.currentTimeMillis()-l));
                        progress.setProgress(progress.getProgress()+1);
                        callsLeft--;
                        Log.e("here","8: "+(System.currentTimeMillis()-l));
                        try {
                            Thread.sleep(callDisconnectTime);//+data_time[count]
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        //count=count+4;
                        Log.e("here","9: "+(System.currentTimeMillis()-l));

                    }

                }
                status="done";
                call_locked=0;
            }
        });
        t.start();
    };
    public void call2(View v) {
        writeToFile("lastState", "r");
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+phoneNo));
                status="not done";

                //UI
                progress.setProgress(0);
                //int last_export=0;
                for (int test = 0; test < noOfBytes; test++) {
                    for (int count = 0; count < byteSize; count++) {
                        if (closed == 1) {
                            break;
                        }
                        long l=System.currentTimeMillis();
                        Log.e("here","1: "+(System.currentTimeMillis()-l));
                        startActivity(callIntent);
                        Log.e("here","2: "+(System.currentTimeMillis()-l));
						/*Runnable showDialogRun2 = new Runnable() {
				            public void run(){
				                Intent showDialogIntent2 = new Intent(getBaseContext(), temp.class);
				                startActivity(showDialogIntent2);
				            }
				        };
				        Handler h2 = new Handler(Looper.getMainLooper());
				        h2.postDelayed(showDialogRun2, 1000);*/

                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                        Log.e("here","3: "+(System.currentTimeMillis()-l));
                        waitForCall();
                        Log.e("here","4: "+(System.currentTimeMillis()-l));
                        String str0=readFromFile("connect");
                        writeToFile("connect", str0+System.currentTimeMillis()+"\n");

                        try {
                            //Thread.sleep(5000);
                            //Thread.sleep(data_time[count]);
                            Random r = new Random();
                            int i1 = r.nextInt(16);
                            Thread.sleep(data_time[i1]);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        Log.e("here","5: "+(System.currentTimeMillis()-l));

                        String str=readFromFile("disconnect");
                        writeToFile("disconnect", str+System.currentTimeMillis()+"\n");
                        Log.e("here","6: "+(System.currentTimeMillis()-l));
                        disconnectCall();
                        Log.e("here","7: "+(System.currentTimeMillis()-l));
                        progress.setProgress(progress.getProgress()+1);
                        callsLeft--;
                        Log.e("here","8: "+(System.currentTimeMillis()-l));
                        try {
                            Thread.sleep(callDisconnectTime);//+data_time[count]
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        //count=count+4;
                        Log.e("here","9: "+(System.currentTimeMillis()-l));

						/*while(true){
							String st=readFromFile("lastState");
							if(st!=null && st.equals("i")){
								break;
							}else{
								try {
									Thread.sleep(5);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}*/

                    }
                    //if(last_export==9){export();last_export=0;}else{last_export++;}
					/*if(last_export==9){
						stateTime=stateTime-100;
						for(int i=0; i<byteSize; i=i+2){
							data_time[i]=encodeTime0;
							data_time[i+1]=stateTime;
						}
						last_export=0;
					}else{last_export++;}*/

                }
                status="done";
                call_locked=0;
            }
        });
        t.start();
    };
    public void call3(View v) {
        writeToFile("lastState", "r");
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+phoneNo));
                status="not done";

                //UI
                progress.setProgress(0);
                //int last_export=0;
                for (int test = 0; test < err_cor_sam_code.length; test++) {
                    Log.e("884848484", ""+err_cor_sam_code.length);
                    if (closed == 1) {
                        break;
                    }
                    long l=System.currentTimeMillis();
                    Log.e("here","1: "+(System.currentTimeMillis()-l));
                    startActivity(callIntent);
                    Log.e("here","2: "+(System.currentTimeMillis()-l));
						/*Runnable showDialogRun2 = new Runnable() {
				            public void run(){
				                Intent showDialogIntent2 = new Intent(getBaseContext(), temp.class);
				                startActivity(showDialogIntent2);
				            }
				        };
				        Handler h2 = new Handler(Looper.getMainLooper());
				        h2.postDelayed(showDialogRun2, 1000);*/

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    Log.e("here","3: "+(System.currentTimeMillis()-l));
                    waitForCall();
                    Log.e("here","4: "+(System.currentTimeMillis()-l));
                    String str0=readFromFile("connect");
                    writeToFile("connect", str0+System.currentTimeMillis()+"\n");

                    try {
                        //Thread.sleep(5000);
                        //Thread.sleep(data_time[count]);
                        //Random r = new Random();
                        //int i1 = r.nextInt(16);
                        Log.e("655454",""+test);
                        Log.e("655454",""+err_cor_sam_code[test]);
                        Log.e("655454",""+data_time[err_cor_sam_code[test]]);
                        int i1=err_cor_sam_code[test];
                        Thread.sleep(data_time[i1]);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    Log.e("here","5: "+(System.currentTimeMillis()-l));

                    String str=readFromFile("disconnect");
                    writeToFile("disconnect", str+System.currentTimeMillis()+"\n");
                    Log.e("here","6: "+(System.currentTimeMillis()-l));
                    disconnectCall();
                    Log.e("here","7: "+(System.currentTimeMillis()-l));
                    progress.setProgress(progress.getProgress()+1);
                    callsLeft--;
                    Log.e("here","8: "+(System.currentTimeMillis()-l));
                    try {
                        Thread.sleep(callDisconnectTime);//+data_time[count]
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    //count=count+4;
                    Log.e("here","9: "+(System.currentTimeMillis()-l));




                }
                status="done";
                call_locked=0;
            }
        });
        t.start();
    };
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
    public void saveParameters(){
        writeToFile("parameters", "\nnoOfBytes= "+noOfBytes+"\nbyteSize= "+byteSize+
                "\ncallSetupTime= "+callSetupTime+ "\ncallDisconnectTime= " +
                callDisconnectTime+"\nstateTime= "+stateTime);

    }
    public void writeParametersInSeparateFiles(){
        writeToFile("noOfBytes", ""+noOfBytes);
        writeToFile("byteSize", ""+byteSize);
        writeToFile("callSetupTime", ""+callSetupTime);
        writeToFile("callDisconnectTime", ""+callDisconnectTime);
        writeToFile("stateTime", ""+stateTime);
        writeToFile("phoneNo", ""+phoneNo);
    }
    public void myToast(String str){
        Toast.makeText(getApplicationContext(), str,Toast.LENGTH_LONG).show();
    }
    public void export(){
        File myDir=makeTimeDir();
        makeFile("lastOffHook",myDir);
        makeFile("lastIdeal",myDir);
        makeFile("lastRinging",myDir);
        makeFile("parameters",myDir);
        writeToFile(status, "");
        makeFile(status, myDir);
        makeFile("disconnect", myDir);
        makeFile("connect", myDir);
        makeFile("output", myDir);

        try {
            File file = new File(myDir, "_log.txt");
            Runtime.getRuntime().exec("logcat -d -v time -f " + file.getAbsolutePath());}catch (IOException e){}

    }
    public void send_func(View v, final int[] symbols){
        Log.e("here","s");
        writeToFile("lastState", "r");
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+phoneNo));
                status="not done";

                //UI
                progress.setProgress(0);
                progress.setMax(symbols.length);
                //int last_export=0;
                for (int test = 0; test < 1; test++) {
                    for (int count = 0; count < symbols.length; count++) {
                        if (closed == 1) {
                            break;
                        }
                        long l=System.currentTimeMillis();
                        //Log.e("here","1: "+(System.currentTimeMillis()-l));
                        startActivity(callIntent);
                        //Log.e("here","2: "+(System.currentTimeMillis()-l));

                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                        //Log.e("here","3: "+(System.currentTimeMillis()-l));
                        waitForCall();
                        //Log.e("here","4: "+(System.currentTimeMillis()-l));
                        String str0=readFromFile("connect");
                        writeToFile("connect", str0+System.currentTimeMillis()+"\n");
                        Log.e("******",""+symbols[count]+"~~"+data_time[symbols[count]]);

                        try {
                            //Thread.sleep(data_time[count]);
                            Thread.sleep(data_time[symbols[count]]);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        //Log.e("here","5: "+(System.currentTimeMillis()-l));

                        String str=readFromFile("disconnect");
                        writeToFile("disconnect", str+System.currentTimeMillis()+"\n");
                        //Log.e("here","6: "+(System.currentTimeMillis()-l));
                        disconnectCall();
                        //Log.e("here","7: "+(System.currentTimeMillis()-l));
                        progress.setProgress(progress.getProgress()+1);
                        callsLeft--;
                        //Log.e("here","8: "+(System.currentTimeMillis()-l));
                        try {
                            Thread.sleep(callDisconnectTime);//+data_time[count]
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        //count=count+4;
                        //Log.e("here","9: "+(System.currentTimeMillis()-l));

                    }

                }
                status="done";
                call_locked=0;
            }
        });
        t.start();
    }
    public int decode(long num,long corr){
        num=num-corr+(stateTime/2)-data_time[0];
        int num2=(int) (num/stateTime);
        //num2=(num2-1);
        if(num2<0){num2=0;}
        if(num2>15){num2=15;}//recheck this
        return num2;
    }


    // start of my code
    EditText name;
    EditText age;
    Spinner gender;
    Spinner religion;
    EditText members;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        progress = (ProgressBar) findViewById(R.id.progress);
        writeParametersInSeparateFiles();
        disconnectInitializer();
        askPermissions();

        name = (EditText) findViewById(R.id.name);
        age = (EditText) findViewById(R.id.age);
        gender = (Spinner) findViewById(R.id.gender);
        religion = (Spinner) findViewById(R.id.religion);
        members = (EditText) findViewById(R.id.members);
        submit = (Button) findViewById(R.id.submit);

        String[] genders = new String[]{"M","F"};
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, genders);
        gender.setAdapter(adapter);

        String[] religions = new String[]{"Islam","Christianity","Hinduism"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, religions);
        religion.setAdapter(adapter2);


//        final EditText someshit = (EditText) findViewById(R.id.name);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String toSend = "";
                String theage = Integer.toBinaryString(Integer.decode(age.getText().toString()));
                String toAppend = "";
                if (theage.length() < 8) {
                    toAppend = new String(new char[8 - theage.length() - 1]).replace("\0", "0");
                    if (gender.getSelectedItem().toString().equals("M")) {
//                        toAppend = "1" + toAppend;
                        toSend = toSend + toAppend + theage + "1";
                    } else {
                        toSend = toSend + toAppend + theage + "0";
                    }

                } else {
                    Log.e("Error","Should not come here. The length of age is greater than 6 bits");
                }


                String selectedReligion = religion.getSelectedItem().toString();
                String relString = "0000";
                if (selectedReligion.equals("Islam")) {
                    relString = "0000";
                } else if (selectedReligion.equals("Christianity")) {
                    relString = "0001";
                } else if (selectedReligion.equals("Hinduism")) {
                    relString = "0010";
                }

                toSend = toSend + relString;

                String themembers = Integer.toBinaryString(Integer.decode(members.getText().toString()));
                toAppend = "";
                if (themembers.length() < 4) {
                    toAppend = new String(new char[4 - themembers.length()]).replace("\0", "0");


                } else {
                    Log.e("Error","Should not come here. The length of age is greater than 6 bits");
                }

                toSend = toSend + toAppend + themembers;


                String str = name.getText().toString();

                int index = -1;
                for(int i=0; i<str.length(); i++){
                    index=0;
                    for(int j=0; j<freq_char_set.length(); j++){
                        if(str.charAt(i)==freq_char_set.charAt(j)){
                            index=j;
                            break;
                        }
                    }
                    Log.e("->->->","~"+freq_char_set.charAt(index)+"~");
                    String temp=Integer.toString(index, 2);
                    if(temp.length()==7){temp="0"+temp;}
                    if(temp.length()==6){temp="00"+temp;}
                    if(temp.length()==5){temp="000"+temp;}
                    if(temp.length()==4){temp="0000"+temp;}
                    if(temp.length()==3){temp="00000"+temp;}
                    if(temp.length()==2){temp="000000"+temp;}
                    if(temp.length()==1){temp="0000000"+temp;}
                    Log.e("->->->","~"+temp+"~");
                    toSend=toSend+temp;

                }

//                String some = age.getText().toString();
//                Integer.decode(some);
//                Log.e("HAHAHA",some);
                toSend = "00000000000000000000000000000000" + toSend + "0000000000000000";
                String rec = toSend;
                int symbols[]=new int[rec.length()/4];
                int jk=0;
                String writer="";
                for(int k=0; k<symbols.length; k++){
                    int characters=8*Integer.valueOf(""+rec.charAt(jk))+4*Integer.valueOf(""+rec.charAt(jk+1))+2*Integer.valueOf(""+rec.charAt(jk+2))+Integer.valueOf(""+rec.charAt(jk+3));
                    symbols[k]=characters;
                    jk=jk+4;
                    Log.e("String$$",""+symbols[k]);
                }

                writeToFile("notifications", toSend);


                name.setText(Integer.toString(toSend.length()));
                Log.e("TOSEND",toSend);
                send_func(view, symbols);

//                someshit.setText(toSend);
//                someshit.setText(gender.getSelectedItem().toString());
            }
        });


    }
}
