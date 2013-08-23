package com.luckybrews.beatthetube;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.maps.GeoPoint;

public class MainActivity extends Activity {

    private static final String TAG = "MAIN";
    final int RQS_GooglePlayServices = 1;

    MapView mapView;
    GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // checkGooglePlayServices();

        setContentView(R.layout.activity_main);
        
        Log.e(TAG, "isOnline? " + isOnline());

    }
    
    public boolean isOnline() {
        ConnectivityManager cm =
            (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
    @Override
    protected void onResume() {
        super.onResume();

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

        if (resultCode == ConnectionResult.SUCCESS){
            Toast.makeText(getApplicationContext(), 
                    "isGooglePlayServicesAvailable SUCCESS", 
                    Toast.LENGTH_LONG).show();
        }else{
            GooglePlayServicesUtil.getErrorDialog(resultCode, this, RQS_GooglePlayServices);
        }

    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private Route directions(final GeoPoint start, final GeoPoint dest) {
        Parser parser;
        // https://developers.google.com/maps/documentation/directions/#JSON <-
        // get api
        String jsonURL = "http://maps.googleapis.com/maps/api/directions/json?";
        final StringBuffer sBuf = new StringBuffer(jsonURL);
        sBuf.append("origin=");
        sBuf.append(start.getLatitudeE6() / 1E6);
        sBuf.append(',');
        sBuf.append(start.getLongitudeE6() / 1E6);
        sBuf.append("&destination=");
        sBuf.append(dest.getLatitudeE6() / 1E6);
        sBuf.append(',');
        sBuf.append(dest.getLongitudeE6() / 1E6);
        sBuf.append("&sensor=true&mode=driving");
        parser = new GoogleParser(sBuf.toString());
        Route r = parser.parse();
        return r;
    }

    private void checkGooglePlayServices() {

        int ret = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        Log.d(TAG, "RET: " + ret);
    }

}
