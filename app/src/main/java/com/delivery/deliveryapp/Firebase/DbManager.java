package com.delivery.deliveryapp.Firebase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.delivery.deliveryapp.HistoryActivity;
import com.delivery.deliveryapp.MainActivity;
import com.delivery.deliveryapp.R;
import com.delivery.deliveryapp.RestaurantActivity;
import com.delivery.deliveryapp.models.Dish;
import com.delivery.deliveryapp.models.Menu;
import com.delivery.deliveryapp.models.DishQuantity;
import com.delivery.deliveryapp.models.Order;
import com.delivery.deliveryapp.models.Restaurant;
import com.delivery.deliveryapp.utils.Utils;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.google.firebase.auth.FirebaseUser;

public class DbManager {

    public static FirebaseUser user;

    private final String TAG = "DBINFO";
    private FirebaseFirestore db;

    public DbManager()
    {
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
                        Dish dish = new Dish(name, (float) price);
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

    public void getData(final MainActivity main, double [] restaurantSquare)
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

    private void updateDishes(final String docId, final Order order)
    {
        for (DishQuantity qd: order.getDishes()) {

            Map<String, Object> dishData = new HashMap<>();
            dishData.put("count", qd.getQuantity());
            dishData.put("price", qd.getDish().getPrice());

            db.document("orders/" + docId + "/dishes/" + qd.getDish().getName()).set(dishData);
        }
    }

    //Time format hh:mm
    public void updateOrder(final Context ctx, String name, String address, String city, final Order order, String time)
    {
        if (order == null || order.getDishes().size() == 0) {
            Toast.makeText(ctx, "Ordine vuoto", Toast.LENGTH_SHORT).show();
            return;
        }
        Date today = Utils.getDateTime(Integer.parseInt(time.split(":")[0]), Integer.parseInt(time.split(":")[1]));
        Timestamp deliveryTimestamp = new Timestamp(today.getTime()/1000, 0);
        Timestamp range = new Timestamp((today.getTime()/1000) - 3600, 0); //1 ora di range per la consegna
        Timestamp now = Timestamp.now();
        if (now.getSeconds() > range.getSeconds()) {
            Toast.makeText(ctx, "Richiedi almeno un'ora prima", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> orderData = new HashMap<>();
        orderData.put("user", DbManager.user.getEmail());
        orderData.put("restaurant", order.getRestaurant().getName());
        orderData.put("address", address);
        orderData.put("name", name);
        orderData.put("city", city);
        orderData.put("deliveryTime", deliveryTimestamp);
        orderData.put("total", order.getTotalPrice());

        db.collection("orders").add(orderData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        updateDishes(documentReference.getId(), order);
                        Toast.makeText(ctx, "Ordine inviato", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.v(TAG, "Error writing document", e);
                        Toast.makeText(ctx, "Errore nell'aggiornamento", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void getOrders(final HistoryActivity historyActivity)
    {
        db.collection("orders").whereEqualTo("user", user.getEmail()).orderBy("deliveryTime")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot snapshot = task.getResult();

                    int numOrders = snapshot.size();
                    for (int i = 0; i < numOrders; i++) {
                        String resName = (String) snapshot.getDocuments().get(i).get("restaurant");
                        double total = snapshot.getDocuments().get(i).getDouble("total");
                        Timestamp deliveryTime = (Timestamp) snapshot.getDocuments().get(i).get("deliveryTime");
                        Timestamp now = Timestamp.now();
                        String time;
                        Date date = deliveryTime.toDate();
                        time = new SimpleDateFormat("dd-MM-yyyy  HH:mm", Locale.ITALY).format(date);

                        if (now.getSeconds() > deliveryTime.getSeconds()) {
                            time = "CONSEGNATO: " + time;
                        }
                        historyActivity.addOrder(resName, total, time);
                    }
                }
            }
        });
    }

    //fa un check che non ci sia già un ordine pendente
    public void checkOrder(final RestaurantActivity restaurantActivity, final Intent orderIntent)
    {
        db.collection("orders").whereEqualTo("user", user.getEmail()).orderBy("deliveryTime")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot snapshot = task.getResult();

                    int numOrders = snapshot.size();
                    if (numOrders > 0) //ho almeno un ordine nella storia
                    {
                        Timestamp now = Timestamp.now();
                        for (int i = 0; i < numOrders; i++) {
                            Timestamp delivery = (Timestamp) snapshot.getDocuments()
                                    .get(i).get("deliveryTime");
                            if (now.getSeconds() < delivery.getSeconds())
                            {
                                Log.v(TAG, "C'è un ordine pendente!");
                                Toast.makeText(restaurantActivity.getBaseContext(), "Stai attendendo un ordine.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    }

                    //qui decido se RestaurantActivity può passare a OrderActivity, devo
                    //aspettare la risposta e procedere in maniera sincrona.
                    restaurantActivity.startActivity(orderIntent);
                }
            }
        });
    }
}