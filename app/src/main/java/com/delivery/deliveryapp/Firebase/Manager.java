package com.delivery.deliveryapp.Firebase;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.delivery.deliveryapp.MainActivity;
import com.delivery.deliveryapp.models.Dish;
import com.delivery.deliveryapp.models.Menu;
import com.delivery.deliveryapp.models.Restaurant;
import com.delivery.deliveryapp.utils.GpsUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class Manager extends AsyncTask<Void, Void, Void> {

    private final String TAG = "DBINFO";
    private FirebaseFirestore db;

    private MainActivity main;
    private double _lat, _long;
    private double [] restaurantSquare; //cords range
    private static int RANGE = 10; //KM
    private Task<DataSnapshot> task;
    private static boolean end;

    public Manager(MainActivity main, float _lat, float _long)
    {
        this.main = main;
        Manager.end = false;

        this._lat = _lat;
        this._long = _long;

        this.db = FirebaseFirestore.getInstance();
    }

    private Menu buildMenu(final String resName, final int idx, final String menuName) {

        final Menu menu = new Menu(menuName);
        DocumentReference priceRef = db.document("restaurants/" + resName + "/menu/"+idx+"/dishes/prices");
        priceRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    for (String name : snapshot.getData().keySet())
                    {
                        Log.v(TAG, "Dish name: " + name);
                        double price = snapshot.getDouble(name);
                        Dish dish = new Dish(name, "description", (float) price);
                        menu.add(dish);
                    }
                }
            }
        });

        return menu;
    }

    private Restaurant buildRestaurant(final String resName) {

        final ArrayList<Menu> menus = new ArrayList<>();
        db.collection("restaurants/" + resName + "/menu")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            QuerySnapshot snapshot = task.getResult();
                            int numMenus = snapshot.size();
                            for (int i=0; i<numMenus; i++)
                            {
                                String menuName = (String) snapshot.getDocuments().get(i).get("nome");
                                Log.v(TAG, "Name: " + menuName);
                                Menu menu = buildMenu(resName, i, menuName);
                                menus.add(menu);
                            }
                        }
                    }
                });

        return new Restaurant(resName, menus);
    }

    public void getData()
    {
        GeoPoint north = new GeoPoint(restaurantSquare[1], 0.0);
        GeoPoint south = new GeoPoint(restaurantSquare[0], 0.0);
        //GeoPoint east = new GeoPoint( 0.0, restaurantSquare[3]);
        //GeoPoint weast = new GeoPoint(0.0, restaurantSquare[2]);
        //filtro solo la latitudine dal db remoto, una volta scaricati i dati filtrerò sulla longitudine
        //l'alternativa è usare i geohash: https://firebase.google.com/docs/firestore/solutions/geoqueries
        db.collection("restaurants")
                .whereLessThan("position", north)
                .whereGreaterThan("position", south)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot documentSnapshots  = task.getResult();
                    //TODO filtrare la latitudine
                    if (documentSnapshots.isEmpty())
                    {
                        Log.d(TAG, "onSuccess: LIST EMPTY");
                        return;
                    }
                    int dim = documentSnapshots.size();
                    for (int r=0; r<dim; r++) {

                        DocumentSnapshot resRef = documentSnapshots.getDocuments().get(r);
                        String name = resRef.getId();
                        //------LOG------
                        //Log.v(TAG, "Restaurant name: " + name);
                        //GeoPoint p = (GeoPoint) resRef.get("position");
                        //Log.v(TAG, "Lat " + p.getLatitude());
                        //Log.v(TAG, "Long " + p.getLongitude());
                        //----END LOG----

                        Restaurant restaurant = buildRestaurant(name);
                        //try {Thread.sleep(1000);} catch (Exception ex) {}//TODO aggiungere wait dei task ??
                        main.addRestaurant(restaurant);
                    }
                }
            }
        });
    }

    @Override
    public Void doInBackground(Void... v)
    {
        while (_lat == 0 || _long == 0)
        {   //wait until i have both cords
            try { Thread.sleep(20); } catch (InterruptedException iex) { }
        }

        //Log.v(TAG, "In manager task, lat = " + _lat);
        //Log.v(TAG, "In manager task, long = " + _long);

        restaurantSquare = GpsUtils.computeSquareAroundPos(_lat, _long, RANGE);
        //for (double pos : restaurantSquare)
        //    Log.v(TAG, ""+pos);
        this.getData();
        //this.end = true;
        while (!Manager.end);
        Log.d(TAG, "Fine");
        return null;
    }

    public boolean isEnded() {return Manager.end;}
    public void setLat(double _lat) {this._lat = _lat;}
    public void setLong(double _long) {this._long = _long;}
}