package com.delivery.deliveryapp.Firebase;

import android.app.Activity;
import android.content.Context;
import android.media.audiofx.Equalizer;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.delivery.deliveryapp.MainActivity;
import com.delivery.deliveryapp.R;
import com.delivery.deliveryapp.SettingsActivity;
import com.delivery.deliveryapp.models.Dish;
import com.delivery.deliveryapp.models.Menu;
import com.delivery.deliveryapp.models.Order;
import com.delivery.deliveryapp.models.Restaurant;
import com.delivery.deliveryapp.utils.GpsUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.firebase.auth.FirebaseUser;

public class DbManager extends AsyncTask<Void, Void, Void> {

    public static FirebaseUser user;

    private final String TAG = "DBINFO";
    private FirebaseFirestore db;

    private MainActivity main;
    private double _lat, _long;
    private double [] restaurantSquare; //cords range
    private static int RANGE = 15; //KM
    private boolean gpsSetted;

    //costruttore usato quando si devono invocare funzioni che non richiedono l'esecuzione del task
    //TODO refactoring (spostare il task fuori che chiama le funzionalità) + dichiarare le funzionalità statiche
    public DbManager()
    {
        this.db = FirebaseFirestore.getInstance();
    }

    public DbManager(MainActivity main, float _lat, float _long)
    {
        this.main = main;
        gpsSetted = false;

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
                    for (int r=0; r<dim; r++) {

                        DocumentSnapshot resRef = documentSnapshots.getDocuments().get(r);
                        String name = resRef.getId();

                        Restaurant restaurant = buildRestaurant(name);
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
            try { Thread.sleep(5); } catch (InterruptedException iex) { }
        }
        this.gpsSetted = true;

        restaurantSquare = GpsUtils.computeSquareAroundPos(_lat, _long, RANGE);

        this.getData();

        Log.d(TAG, "Fine");
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {//TODO da rimuovere
        super.onPostExecute(aVoid);
        Log.v(TAG, "End");
    }

    public void updateUserData(final Context ctx, String name, String address, String city)
    {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("name", name);
        userInfo.put("address", address);
        userInfo.put("city", city);

        db.collection("userInfo").document(user.getEmail())
                .set(userInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Document successfully written!");
                        Toast.makeText(ctx, "Aggiornato", Toast.LENGTH_SHORT);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.v(TAG, "Error writing document", e);
                        Toast.makeText(ctx, "Errore nell'aggiornamento", Toast.LENGTH_SHORT);
                    }
                });
    }


     //Activity può essere SettingsActivity o UserActivity che entrambe richiedono gli stessi campi
    public void getUserData(final Activity activity)
    {
        db.collection("userInfo").document(user.getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();

                    String name = snapshot.getString("name");
                    String address = snapshot.getString("address");
                    String city = snapshot.getString("city");

                    EditText txtName, txtAddress, txtCity;
                    txtName = activity.findViewById(R.id.editTextName);
                    txtAddress = activity.findViewById(R.id.editTextAddress);
                    txtCity = activity.findViewById(R.id.editTextCity);
                    txtName.setText(name, TextView.BufferType.EDITABLE);
                    txtAddress.setText(address, TextView.BufferType.EDITABLE);
                    txtCity.setText(city, TextView.BufferType.EDITABLE);
                    }
                }
        });
    }

    private void updateDishes(final Context ctx, final String docId, final Order order)
    {
        Map<String, Object> dishData = new HashMap<>();
        dishData.put("count", 2);
        dishData.put("price", 5.5);

        db.document("orders/" + docId + "/dishes/piatto3").set(dishData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Document successfully written!");
                        Toast.makeText(ctx, "Aggiornato", Toast.LENGTH_SHORT);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.v(TAG, "Error writing document", e);
                        Toast.makeText(ctx, "Errore nell'aggiornamento", Toast.LENGTH_SHORT);
                    }
                });
    }

    public void updateOrder(final Context ctx, String name, String address, String city, final Order order)
    {
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("user", DbManager.user.getEmail());
        orderData.put("restaurant", order.getRestaurant().getName());
        orderData.put("address", address);
        orderData.put("name", name);
        orderData.put("city", city);
        orderData.put("deliveryTime", Timestamp.now()); //TODO mettere ora di consegna preferita
        //orderData.put("order", order);

        db.collection("orders").add(orderData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        //Log.d(TAG, "Document successfully written!");
                        //Toast.makeText(ctx, "Aggiornato", Toast.LENGTH_SHORT);
                        updateDishes(ctx, documentReference.getId(), order);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.v(TAG, "Error writing document", e);
                        Toast.makeText(ctx, "Errore nell'aggiornamento", Toast.LENGTH_SHORT);
                    }
                });
    }

    public boolean gpsSetted() {return this.gpsSetted;}
    public void setLat(double _lat) {this._lat = _lat;}
    public void setLong(double _long) {this._long = _long;}
}