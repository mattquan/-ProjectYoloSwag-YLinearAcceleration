package com.quan.robotics.accelerometerdemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Image;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import static java.lang.Math.pow;
import static java.lang.StrictMath.atan;
import static java.lang.StrictMath.sqrt;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private double ax,ay,az;
    private int counter;
    private float pitch, roll;
    private ArrayList<String[]> mData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mData = new ArrayList<>();
        mData.add(new String[]{"test","test"});
        final SensorManager mSM = (SensorManager)( getSystemService(SENSOR_SERVICE));
        final Sensor mAccel = mSM.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        final SensorEventListener context= this;

        Log.e("mAccel",mAccel.toString());
        counter=0;
        ImageButton play = (ImageButton) findViewById(R.id.play);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mSM.registerListener(context, mAccel, SensorManager.SENSOR_DELAY_NORMAL);
                Toast.makeText(getApplicationContext(),"Playing",Toast.LENGTH_SHORT).show();

            }
        });
        ImageButton pause = (ImageButton) findViewById(R.id.pause);
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               mSM.unregisterListener(context, mAccel);
            }
        });
        ImageButton save = (ImageButton) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)  {
                try {

                    Toast.makeText(getApplicationContext(),"Saved in Downloads Folder",Toast.LENGTH_SHORT).show();
                    SharedPreferences prefs = getApplicationContext().getSharedPreferences(
                            "com.quan.companion", Context.MODE_PRIVATE);
                    int savedInt = prefs.getInt("MyKey",0);
                    prefs.edit().putInt("MyKey",savedInt+1).apply();
                    save(savedInt);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

    }
    public void save(int saved) throws IOException {
        File  mF = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"Test"+saved);
        CSVWriter writer =  new CSVWriter(new FileWriter(mF));
        writer.writeAll(mData);
        writer.close();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.e("change","change");
        if (event.sensor.getType()==Sensor.TYPE_LINEAR_ACCELERATION){
            ax=event.values[0];
            ay=event.values[1];
            az=event.values[2];
            counter++;
            mData.add(new String[] {"y"+counter,String.valueOf(ay)});
            updateTextview();
        }
    }

    private void updateTextview() {
        TextView display = (TextView) findViewById(R.id.textview);
        // not sure about this pitch = (atan(xAxis/sqrt(pow(yAxis,2) + pow(zAxis,2)))) * (180.0/Math.PI) ;
        // or this roll = (atan(yAxis/sqrt(pow(xAxis,2) + pow(zAxis,2)))) * (180.0/Math.PI);
        display.setText(counter+" y: "+ay+"\n"+display.getText());
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
