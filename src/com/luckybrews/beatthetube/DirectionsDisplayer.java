package com.luckybrews.beatthetube;

import java.util.ArrayList;

import org.w3c.dom.Document;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class DirectionsDisplayer extends AsyncTask<Void, Void, Void> {
    
        private static final String TAG = DirectionsDisplayer.class.getSimpleName();
    
        private PolylineOptions rectLine;

        private enum Mode { COORDINATES, ADDRESS };
        
        private final Mode mode;
        
        
        private LatLng mFromPosition, mToPosition;
        private String mFromAddress, mToAddress;
        private final GoogleMap mMap;
        
        private Document doc;
        
        public DirectionsDisplayer(String startAdress, String endAddress, GoogleMap map) {
            mFromAddress = startAdress;
            mToAddress = endAddress;
            mMap = map;
            mode = Mode.ADDRESS;
        }
        
        public DirectionsDisplayer(LatLng fromPosition, LatLng toPosition, GoogleMap map) {
            mFromPosition = fromPosition;
            mToPosition = toPosition;
            mMap = map;
            mode = Mode.COORDINATES;
        }
        
        private LatLng getAvgCoordinate(LatLng one, LatLng two) {
            double avgLatitude  = (one.latitude + two.latitude) / 2;
            double avgLongitude = (one.longitude + two.longitude) / 2;
            return new LatLng(avgLatitude, avgLongitude);
        }

        @Override
        protected Void doInBackground(Void... params) {
            fetchDirections();
            return null;
        }

        public void fetchDirections() {
            
            GMapV2Direction md = new GMapV2Direction();

            // Document doc = md.getDocument(mFromPosition, mToPosition, GMapV2Direction.MODE_WALKING);
            switch (mode) {
            
                case ADDRESS: {
                    
                    // TRANSIT
                    doc = md.getDocument(mFromAddress, mToAddress, GMapV2Direction.MODE_TRANSIT);
                    showInfo("Transit Info", md);
                    int trasitTime = md.getDurationValue(doc);
                    
                    // CYCLING
                    doc = md.getDocument(mFromAddress, mToAddress, GMapV2Direction.MODE_CYCLING);
                    showInfo("Cycling Info", md);
                    int cyclingTime = md.getDurationValue(doc);
                    
                    showResult(cyclingTime, trasitTime);
                    
                    break;
                }  
                case COORDINATES: {
                    
                    // TRANSIT
                    doc = md.getDocument(mFromPosition, mToPosition, GMapV2Direction.MODE_TRANSIT);
                    showInfo("Transit Info", md);
                    
                    // CYCLING
                    doc = md.getDocument(mFromPosition, mToPosition, GMapV2Direction.MODE_CYCLING);
                    showInfo("Cycling Info", md);
                    break;
                }
                    
            } 
            
            ArrayList<LatLng> directionPoint = md.getDirection(doc);
            rectLine = new PolylineOptions().width(3).color(Color.BLUE);

            for(int i = 0 ; i < directionPoint.size() ; i++) {          
                rectLine.add(directionPoint.get(i));
            }
        }
        
        private void showResult(int cyclingTime, int trasitTime) {
            
            if (cyclingTime < trasitTime) {
                Log.e(TAG, "BEAT THE TUBE!!!");
            } else {
                Log.e(TAG, "THE TUBE WINS :-(");
            }
            int percentage = (int) (100 * (cyclingTime-trasitTime) / cyclingTime);
            Log.e(TAG, "Difference: " + percentage + "%");
        }
        
        private void showInfo(String tag, GMapV2Direction md) {
            
            int duration = md.getDurationValue(doc);
            String distance = md.getDistanceText(doc);
            String start_address = md.getStartAddress(doc);
            String copy_right = md.getCopyRights(doc);
            
            mFromPosition = md.getStartLocation(doc);
            //Log.w(tag, "StartLocation: " + mFromPosition);
            
            mToPosition = md.getEndLocation(doc);
            //Log.w(tag, "EndLocation: " + mToPosition);
            
            Log.e(tag, "Duration: " + duration);
        }
        
        @Override
        protected void onPostExecute(Void params) {

            mMap.addMarker(new MarkerOptions().position(mFromPosition).title("Start"));
            mMap.addMarker(new MarkerOptions().position(mToPosition).title("End"));
            
            // mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(getAvgCoordinate(start, end), 16));
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(mFromPosition);
            builder.include(mToPosition);
            LatLngBounds bounds = builder.build();
            
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));

            mMap.addPolyline(rectLine);
        }
}
