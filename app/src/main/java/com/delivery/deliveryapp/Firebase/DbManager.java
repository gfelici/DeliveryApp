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

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DbManager extends AsyncTask<Void, Void, Void> {

    public static FirebaseUser user;

    private final String TAG = "DBINFO";
    private FirebaseFirestore db;

    private MainActivity main;
    private double _lat, _long;
    private double [] restaurantSquare; //cords range
    private static int RANGE = 10; //KM
    //private Task<DataSnapshot> task;
    private static boolean end;
    private boolean gpsSetted;

    public DbManager(MainActivity main, float _lat, float _long)
    {
        this.main = main;
        DbManager.end = false;
        gpsSetted = false;

        this._lat = _lat;
        this._long = _long;

        this.db = FirebaseFirestore.getInstance();
    }

    private Menu buildMenu(final String resName, final int idx, final String menuName) {

        final Menu menu = new Menu(menuName);
        DocumentReference priceRef = db.document("restaurants/" + resName + "/menu/"+idx+"/dishes/prices");
        //Task<DocumentSnapshot> task = priceRef.get();
        priceRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    for (String name : snapshot.getData().keySet()) {
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
        final Restaurant restaurant = new Restaurant(resName, menus);
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
                    QuerySnapshot documentSnapshots = task.getResult();
                    //TODO filtrare la latitudine
                    if (documentSnapshots.isEmpty())
                    {
                        Log.d(TAG, "onSuccess: LIST EMPTY");
                        return;
                    }
                    int dim = documentSnapshots.size();
                    int count = 0; //conto quanti ristoranti ho creato
                    for (int r=0; r<dim; r++) {

                        DocumentSnapshot resRef = documentSnapshots.getDocuments().get(r);
                        String name = resRef.getId();

                        Restaurant restaurant = buildRestaurant(name);
                        main.addRestaurant(restaurant);
                        count++;
                    }

                    if (count == dim)//ho aggiunto tutti i ristoranti
                    {
                        DbManager.end = true;
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
            try { Thread.sleep(5); } catch (InterruptedException iex) { }
        }
        this.gpsSetted = true;

        restaurantSquare = GpsUtils.computeSquareAroundPos(_lat, _long, RANGE);

        this.getData();

        Log.d(TAG, "Fine");
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Log.v(TAG, "End");
    }

    public boolean gpsSetted() {return this.gpsSetted;}
    public void setLat(double _lat) {this._lat = _lat;}
    public void setLong(double _long) {this._long = _long;}
}