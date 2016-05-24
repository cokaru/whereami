package com.sungjae.cokaru.whereami;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import uk.me.jstott.jcoord.LatLng;
import uk.me.jstott.jcoord.UTMRef;


public class CommandFragment extends Fragment
{
    LocationManager     locationManager = null;
    MyLocationListener  listener = null;

    int accuracy = 0;
    double startAirPressure;
    double currentAirPressure;

    UTMRef startSpotUtm;
    UTMRef currentSpotUtm;

    boolean   setLocationMode = true;

    Timer     updateTimer = null;
    TimerTask locationUpdateTask = null;

    TextView messageText = null;

    final Handler handler = new Handler();


    OnButtonPressListener buttonPressListener;

    public CommandFragment()
    {

    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        listener        = new MyLocationListener();
    }


    @Override
    public void onAttach (Context context)
    {
        super.onAttach(context);
        Activity activity;

        if (context instanceof Activity){
            activity=(Activity) context;
            buttonPressListener = (OnButtonPressListener)getActivity();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view   = inflater.inflate(R.layout.fragment_command, container, false);
        messageText = (TextView)view.findViewById(R.id.text01);



        Button searchBtn = (Button)view.findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(updateTimer != null)
                    updateTimer.cancel();

                setStartLocation();
            }
        });


        Button updateBtn = (Button)view.findViewById(R.id.updateBtn);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLocationTimer();
                updateTimer = new Timer();
                updateTimer.schedule(locationUpdateTask, 0, 1 * 1000);
                updateLocation();

            }
        });

        return view;
    }

    public void setStartAirPressure(double _value) { startAirPressure = _value; }
    public void setCurrentAirePressure(double _value) { currentAirPressure = _value;}
    public void setAccuracy(int _value) { accuracy = _value; }
    public void setAirPressure(double _value)
    {
        if(setLocationMode)
            startAirPressure = _value;
        else
            currentAirPressure = _value;
    }

    private void startLocationTimer()
    {
        locationUpdateTask = new TimerTask() {
            @Override
            public void run()
            {
                handler.post(new Runnable() {
                    @Override
                    public void run()
                    {
                        updateLocation();
                    }
                });

            }
        };

    }

    @Override
    public void onResume()
    {
        super.onResume();
        if(locationUpdateTask != null)
            locationUpdateTask.run();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if(locationUpdateTask != null)
            locationUpdateTask.cancel();
    }


    public void setStartLocation()
    {
        locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }

        setLocationMode = true;
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);

    }

    public void updateLocation()
    {

        locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(getActivity(), "Permission is Denied!!", Toast.LENGTH_SHORT).show();
            return;
        }

        setLocationMode = false;
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
    }

    private void setText(String msg)
    {
        messageText.setText(msg);
    }

    private void appendText(String msg) { messageText.append("\n"); messageText.append(msg);}

    class MyLocationListener implements LocationListener
    {
        public void onLocationChanged(Location location)
        {
            Toast.makeText(getActivity(), "In onLocationChanged", Toast.LENGTH_SHORT).show();

            if(location.getAccuracy() > 50 || location.getSpeed() > 10)
            {
                if(setLocationMode)
                    setStartLocation();
                else
                    return;
            }

            double latitude     = location.getLatitude();
            double longtitude   = location.getLongitude();
            double altitude     = location.getAltitude();
            accuracy     = (int)location.getAccuracy();


            LatLng latLng = new LatLng(latitude, longtitude);
            if(setLocationMode) {
                startSpotUtm = latLng.toUTMRef();
                buttonPressListener.onSetLocation(latitude, longtitude, altitude);
                updateStartLocationUI();
            }else{
                currentSpotUtm = latLng.toUTMRef();
                updateLocationUI();
            }


            locationManager.removeUpdates(this);
        }


        private  void updateStartLocationUI()
        {
            setText("UTM(East,North):" + startSpotUtm.getEasting() + "," + startSpotUtm.getNorthing());
            appendText("Location Accuracy:" + accuracy);
            appendText("Air Pressure(hPa):" + startAirPressure);

            Log.d("UTM(East,North):", startSpotUtm.getEasting() + "," + startSpotUtm.getNorthing());
            Log.d("ACCURACY" , Double.toString(accuracy));
            Log.d("Air Pressure(hPa)", Double.toString(startAirPressure));
        }

        private void updateLocationUI()
        {
            if(startSpotUtm == null || currentSpotUtm == null)
                return;

            double deltaEastMeter = startSpotUtm.getEasting() - currentSpotUtm.getEasting();
            double deltaNorthMeter = startSpotUtm.getNorthing() - currentSpotUtm.getNorthing();
            double deltaAltitudeMeter = SensorManager.getAltitude((float) startAirPressure, (float) currentAirPressure);

            double distance = Math.sqrt(deltaEastMeter*deltaEastMeter + deltaNorthMeter*deltaNorthMeter + deltaAltitudeMeter*deltaAltitudeMeter);

            setText("Distance:" + distance + "m");
        }

        public void onProviderDisabled(String provider)
        {

        }

        public void onProviderEnabled(String provider)
        {
            Log.d("Provider", provider);
        }

        public void onStatusChanged(String provider,int status, Bundle extras)
        {

        }
    }
}
