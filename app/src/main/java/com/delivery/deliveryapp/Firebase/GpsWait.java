package com.delivery.deliveryapp.Firebase;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.delivery.deliveryapp.MainActivity;
import com.delivery.deliveryapp.R;
import com.delivery.deliveryapp.utils.GpsUtils;

public class GpsWait extends AsyncTask<Void, Void, Void>{

    private final String TAG = "GPSINFO";
    private double _lat, _long;
    private double [] restaurantSquare; //cords range
    private static int RANGE = 15; //KM
    private boolean gpsSetted;

    private MainActivity main;

    public GpsWait(MainActivity main)
    {
        this.main = main;
    }

    @Override
    public Void doInBackground(Void... v)
    {
        while (_lat == 0 || _long == 0)
        {   //wait until i have both cords
            try { Thread.sleep(5); } catch (InterruptedException iex) { }
        }
        this.gpsSetted = true;

        restaurantSquare = GpsUtils.computeSquareAroundPos(_lat, _long, RANGE);

        DbManager dbManager = new DbManager();
        dbManager.getData(main, restaurantSquare);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        TextView info = main.findViewById(R.id.info);
        info.setText(R.string.res_around_you);
        Log.v(TAG, "Coordinates ok");
    }

    public boolean gpsSetted() {return this.gpsSetted;}
    public void setLat(double _lat) {this._lat = _lat;}
    public void setLong(double _long) {this._long = _long;}
}
