package com.delivery.deliveryapp.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import static android.Manifest.permission_group.LOCATION;

public class GpsUtils {

    private LocationManager locationManager;
    private LocationListener locationListener;
    private Activity activity;
    private int REQ_CODE = 1;

    //this params is taken followig this link: https://stackoverflow.com/questions/7477003/calculating-new-longitude-latitude-from-old-n-meters
    private final static double KM_UNIT = 0.008983;

    public GpsUtils(Activity activity) {
        this.activity = activity;
        locationManager = (LocationManager) activity.getSystemService(activity.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
    }

    public float[] getCoordinates() {
        if (activity.checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && activity.checkCallingOrSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //avviso l'utente del perchè ho bisogno del permesso

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //lo chiediamo
                final String [] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
                activity.requestPermissions(perms, REQ_CODE);

                return null;
            }
            locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);
        }

        return null;
    }

    public static double [] computeSquareAroundPos(double _lat, double _long, int km) //[lat1, lat2, long1, long2]
    {
        double _lat1 = _lat - km*KM_UNIT;
        double _lat2 = _lat + km*KM_UNIT;
        double _long1 = _long - km*KM_UNIT;
        double _long2 = _long + km*KM_UNIT;
        double [] coords = {_lat1, _lat2, _long1, _long2};

        return coords;
    }
}
