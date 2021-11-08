package com.delivery.deliveryapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import com.delivery.deliveryapp.Firebase.Manager;
import com.delivery.deliveryapp.models.Restaurant;
import com.delivery.deliveryapp.utils.Utils;

public class MainActivity extends Activity
{
    private double _lat, _long;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private int GPS_REQ_CODE = 10;
    final String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    final String TAG = "PERMS";
    final String INFO = "CORDS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //gps coords
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.v(INFO, "lat: " + location.getLatitude() + " long: " + location.getLongitude());
                _lat = location.getLatitude();
                _long = location.getLongitude();
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

        if (checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && checkCallingOrSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //TODO avviso l'utente del perchè ho bisogno del permesso
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //lo chiediamo
                requestPermissions(perms, GPS_REQ_CODE);
            }
        }
        else
            locationManager.requestLocationUpdates("gps", 1000, 0, locationListener);
        //Log.d("INFO2", "Inizio");
        //while (!manager.isEnded());
        //try {Thread.sleep(5*1000);} catch (InterruptedException iex) {}

        Manager manager = new Manager(this);
        manager.execute();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if (requestCode == GPS_REQ_CODE && grantResults.length>0)
        {
            for (int i=0; i<permissions.length; i++)
                for (int j=0; j<perms.length; j++)
                    if (permissions[i].equals(perms[j]))
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
                            Log.d(TAG, "Permission " + permissions[i] + " granted");
                        else
                            Log.d(TAG,  "Permission " + permissions[i] + " not granted");
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    private void newRestaurantActivity(final Restaurant restaurant)
    {
            Intent myIntent = new Intent(MainActivity.this, RestaurantActivity.class);
            myIntent.putExtra("restaurant", restaurant);
            MainActivity.this.startActivity(myIntent);
    }

    public void addRestaurant(final Restaurant restaurant)
    {
        LinearLayout restaurantLayout = findViewById(R.id.restaurantLayout);

        LinearLayout l = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        l.setLayoutParams(params);
        l.setOrientation(LinearLayout.HORIZONTAL);

        //ImageView image = new ImageView(this);
        //params = new LinearLayout.LayoutParams(Utils.dpToPx(100,this), Utils.dpToPx(100,this));
        //image.setLayoutParams(params);
        //image.setForegroundGravity(Gravity.LEFT);
        //image.setImageResource(R.drawable.sushi);

        TextView textView = new TextView(this);
        params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Utils.dpToPx(50, this));
        params.setMarginStart(Utils.dpToPx(10,this));
        textView.setLayoutParams(params);
        textView.setGravity(Gravity.LEFT);
        textView.setText(restaurant.getName());
        textView.setTextSize(30);

        //l.addView(image);
        l.addView(textView);
        l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newRestaurantActivity(restaurant);
            }
        });

        restaurantLayout.addView(l);

        //minimo di spazio tra un ristorante e l'altro
        Space space = new Space(this);
        //space.getLayoutParams().height = 10;
        restaurantLayout.addView(space);
    }
}