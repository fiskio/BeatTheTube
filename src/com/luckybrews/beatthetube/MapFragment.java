package com.luckybrews.beatthetube;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;

public class MapFragment extends Fragment {
    
    MapView mapView;
    GoogleMap map;
    
    LatLng fromPosition = new LatLng(13.687140112679154, 100.53525868803263);
    LatLng toPosition = new LatLng(13.683660045847258, 100.53900808095932);

    String fromAddress = "NW32BH";
    String toAddress = "SE10AX";
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
        View v = inflater.inflate(R.layout.some_layout, container, false);
        
        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) v.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        
        // Gets to GoogleMap from the MapView and does initialization stuff
        map = mapView.getMap();
        map.getUiSettings().setMyLocationButtonEnabled(false);
        
        
        map.setMyLocationEnabled(true);

        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        try {
            MapsInitializer.initialize(this.getActivity());
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }

        // new DirectionsDisplayer(fromPosition, toPosition, map).execute();
        new DirectionsDisplayer(fromAddress, toAddress, map).execute();
        
        return v;
    }
    
    

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
    
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
    
    
}