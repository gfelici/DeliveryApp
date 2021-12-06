package com.delivery.deliveryapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.delivery.deliveryapp.Firebase.DbManager;
import com.delivery.deliveryapp.Firebase.GpsWait;
import com.delivery.deliveryapp.models.Restaurant;
import com.delivery.deliveryapp.utils.Utils;

import java.util.ArrayList;

public class MainActivity extends Activity
{
    private ArrayList<Restaurant> restaurants;

    private double _lat, _long;//TODO muovere in Manager
    private LocationManager locationManager;
    private LocationListener locationListener;
    private int GPS_REQ_CODE = 10;
    final String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    final String TAG = "INFO";
    final String CORDS = "CORDS";

    final GpsWait gpsWait = new GpsWait(this);
    //final DbManager manager = new DbManager(); //get db instance
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        restaurants = new ArrayList<Restaurant>();
        bundle = savedInstanceState;
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if (bundle == null) //no data saved locally, must query the db
        {
            bundle = new Bundle();
            loadData();
        }

        else
        {
            boolean reload = bundle.getBoolean("reload");
            if (reload)
            {
                loadData();
            }

            else {
                ArrayList<Restaurant> loadedRestaurants = (ArrayList<Restaurant>) bundle.getSerializable("Restaurants");
                Log.v(TAG, "Restaurants loaded: " + loadedRestaurants + "Bundle is: " + bundle);
                LinearLayout l = findViewById(R.id.restaurantLayout);

                if (loadedRestaurants != null && loadedRestaurants.size() != 0) {
                    restaurants = new ArrayList<Restaurant>();
                    l.removeAllViews();
                    for (Restaurant r : loadedRestaurants) {
                        addRestaurant(r);//aggiunge grafica e ogni ristorante
                    }
                }
            }
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        Button settings = this.findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(MainActivity.this, SettingsActivity.class);
                MainActivity.this.startActivity(myIntent);
            }
        });

        Button history = this.findViewById(R.id.orders);
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gpsWait.getStatus() == AsyncTask.Status.RUNNING)
                    gpsWait.cancel(true);
                Intent myIntent = new Intent(MainActivity.this, HistoryActivity.class);
                MainActivity.this.startActivity(myIntent);
            }
        });
    }

    @Override
    protected void onPause() {//TODO posso lasciare onPause vuoto?
        super.onPause();
        if (gpsWait.gpsSetted()) {
            bundle.putBoolean("reload", false);
            bundle.putSerializable("Restaurants", restaurants);
        }
        else {
            bundle.putBoolean("reload", true);
            gpsWait.cancel(true);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (gpsWait.gpsSetted()) {
            outState.putBoolean("reload", false);
            outState.putSerializable("Restaurants", restaurants);
        }
        else {
            outState.putBoolean("reload", true);
            gpsWait.cancel(true);
        }
    }

    private void loadData()
    {
        Log.v(TAG, "Getting data from Firestore");
        _lat = _long = 0; //init location, 0 means not already got cords

        //gps coords
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LOCATION_SERVICE))
        {
            TextView infoTxt = findViewById(R.id.info);
            infoTxt.setText("Ottengo la posizione..");
        }

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.v(CORDS, "lat: " + location.getLatitude() + " long: " + location.getLongitude());
                _lat = location.getLatitude();
                _long = location.getLongitude();

                gpsWait.setLat(_lat);
                gpsWait.setLong(_long);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {
                Log.v("PROVIDER", "Provider enabled");
                TextView infoTxt = findViewById(R.id.info);
                infoTxt.setText("Ottengo la posizione..");
            }

            @Override
            public void onProviderDisabled(String s) {
                Log.v("PROVIDER", "Provider disabled");
                TextView infoTxt = findViewById(R.id.info);
                infoTxt.setText("Attiva la geolocalizzazione.");
            }
        };

        if (checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && checkCallingOrSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //TODO avviso l'utente del perchè ho bisogno del permesso
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //lo chiediamo
                requestPermissions(perms, GPS_REQ_CODE);
            }
        }
        else {
            locationManager.requestLocationUpdates("gps", 1000, 0, locationListener);
            try {
                _lat = locationManager.getLastKnownLocation(LOCATION_SERVICE).getLatitude();
                _long = locationManager.getLastKnownLocation(LOCATION_SERVICE).getLongitude();
            }
            catch (NullPointerException ex)
            {}
        }

        gpsWait.execute();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if (requestCode == GPS_REQ_CODE && grantResults.length>0)
        {
            for (int i=0; i<permissions.length; i++)
                for (int j=0; j<perms.length; j++)
                    if (permissions[i].equals(perms[j]))
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            //flag permesso on, lastknownlocation non deve essere usato
                            Log.d(TAG, "Permission " + permissions[i] + " granted");
                        }
                        else
                            Log.d(TAG,  "Permission " + permissions[i] + " not granted");
        }
    }

    private void newRestaurantActivity(final Restaurant restaurant)
    {
            Intent myIntent = new Intent(MainActivity.this, RestaurantActivity.class);
            myIntent.putExtra("restaurant", restaurant);
            MainActivity.this.startActivity(myIntent);
    }

    public void addRestaurant(final Restaurant restaurant)
    {
        if (restaurants == null)
            restaurants = new ArrayList<Restaurant>();

        this.restaurants.add(restaurant);

        LinearLayout restaurantLayout = findViewById(R.id.restaurantLayout);

        LinearLayout l = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(Utils.dpToPx(10, this), Utils.dpToPx(10, this),
                Utils.dpToPx(10, this), Utils.dpToPx(10, this));
        l.setLayoutParams(params);
        l.setOrientation(LinearLayout.HORIZONTAL);

        l.setBackground(getDrawable(R.drawable.box));

        TextView textView = new TextView(this);
        params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Utils.dpToPx(50, this));
        params.setMarginStart(Utils.dpToPx(10,this));
        textView.setLayoutParams(params);
        textView.setGravity(Gravity.LEFT);
        textView.setText(restaurant.getName());
        textView.setTextSize(30);
        textView.setTextColor(Color.BLACK);

        l.addView(textView);
        l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newRestaurantActivity(restaurant);
            }
        });

        restaurantLayout.addView(l);
    }
}