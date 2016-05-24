package com.sungjae.cokaru.whereami;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.CameraPosition;

public class MapFragment extends Fragment implements OnMapReadyCallback
{
    private GoogleMap googleMap;
    private MapView mapView;
    private GLView  glView;

    public MapFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
         super.onCreate(savedInstanceState);
         try
         {
             MapsInitializer.initialize(getActivity().getApplicationContext());
         }
         catch (Exception e)
         {
            Toast.makeText(getActivity(), "MapView Creation is Denied!!", Toast.LENGTH_LONG).show();
             e.printStackTrace();
         }

         if(mapView != null)
             mapView.onCreate(savedInstanceState);

         intializeMap();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    private  void intializeMap()
    {
        if(googleMap == null)
        {
            mapView = (MapView)getActivity().findViewById(R.id.location_map);
            mapView.getMapAsync(this);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
       View view = (RelativeLayout)inflater.inflate(R.layout.fragment_map, container, false);
       mapView = (MapView)view.findViewById(R.id.location_map);
       mapView.getMapAsync(this);

       glView = new GLView(getActivity());

       mapView.addView(glView, new MapView.LayoutParams(
                                MapView.LayoutParams.MATCH_PARENT,
                                MapView.LayoutParams.MATCH_PARENT));

        return view;
    }


    @Override
    public void onResume()
    {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onMapReady(GoogleMap _googleMap)
    {
        googleMap = _googleMap;
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        try{
             MapsInitializer.initialize(getActivity().getApplicationContext());
         }catch (Exception e)
         {
            Toast.makeText(getActivity(), "MapView Creation is Denied!!", Toast.LENGTH_LONG).show();
             e.printStackTrace();
        }

        mapView.onResume();

    }

    public void setLocationonMap(double latitude,double longtitude, double altitude)
    {
        if(googleMap == null) return;
        LatLng startLocation = new LatLng(latitude,longtitude);
        googleMap.addMarker(new MarkerOptions().position(startLocation).title("Start"));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(startLocation)      // Sets the center of the map to Mountain View
                .zoom(17)                   // Sets the zoom
                .bearing(0)                // Sets the orientation of the camera to east
                .tilt(35)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

}
