package com.sungjae.cokaru.whereami;

import android.Manifest;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;



public class MainActivity extends AppCompatActivity implements OnButtonPressListener ,SensorEventListener
{

    SensorManager  sensorManager;
    Sensor         pressureSensor;

    final private int REQUEST_CODE_ASK_PERMISSIONS = 990001;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int version = Build.VERSION.SDK_INT;
        if(version >= 23)
            checkPermissions();

        sensorManager   = (SensorManager)getSystemService(Context.SENSOR_SERVICE);

        PackageManager packageManager = getPackageManager();
        boolean availableAirePressure = packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_BAROMETER);
        if(availableAirePressure) {
            pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        }
        else
        {
            Toast.makeText(this, "This Device doesn't has barometer sensor", Toast.LENGTH_LONG);
        }

    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if(sensorManager != null)
            sensorManager.registerListener(this, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        if(sensorManager != null)
            sensorManager.unregisterListener(this);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor,int accuracy)
    {
        if(pressureSensor==null) return;
        CommandFragment cmdFragment = (CommandFragment)getSupportFragmentManager().findFragmentById(R.id.frag_command);
        if(cmdFragment != null)
        {
            cmdFragment.setAccuracy(accuracy);
        }
    }

    @Override
    public final void onSensorChanged(SensorEvent event)
    {
        if(pressureSensor==null) return;
        CommandFragment cmdFragment = (CommandFragment)getSupportFragmentManager().findFragmentById(R.id.frag_command);
        if(cmdFragment != null)
        {
            cmdFragment.setAirPressure(event.values[0]);
        }
    }

    private void checkPermissions()
    {
        int hasWriteContactsPermission = checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(MainActivity.this, "checkPermissions function is denied!!", Toast.LENGTH_SHORT).show();
            requestPermissions(new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},  REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case REQUEST_CODE_ASK_PERMISSIONS:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(MainActivity.this, "Location permission is accepted.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Location permission is denied.", Toast.LENGTH_SHORT).show();
                }
            }
            break;
            default:  super.onRequestPermissionsResult(requestCode, permissions, grantResults); break;

        }
    }

    public void onSetLocation(double latitude,double longtitude,double airPressure)
    {
        MapFragment mapFragment = (MapFragment)getSupportFragmentManager().findFragmentById(R.id.frag_map);
        mapFragment.setLocationonMap(latitude,longtitude,airPressure);


    }



}
