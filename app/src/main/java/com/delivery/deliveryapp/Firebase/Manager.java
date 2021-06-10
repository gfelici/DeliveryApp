package com.delivery.deliveryapp.Firebase;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.delivery.deliveryapp.MainActivity;
import com.delivery.deliveryapp.models.Dish;
import com.delivery.deliveryapp.models.Menu;
import com.delivery.deliveryapp.models.Restaurant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


public class Manager extends AsyncTask<Void, Void, Void> {

    private final String TAG = "INFO2";
    private FirebaseDatabase db;

    private MainActivity main;
    private Task<DataSnapshot> task;
    private static boolean end;

    public Manager(MainActivity main)
    {
        this.main = main;
        Manager.end = false;

        this.db = FirebaseDatabase.getInstance();
    }

    public void getData()
    {
        DatabaseReference ref = db.getReference("restaurants");
        Query query = ref.orderByKey();
        this.task = query.get();
        task.addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                Log.v(TAG, String.valueOf(task.getResult().child("0").child("name").getValue()));
                DataSnapshot data = task.getResult();
                for (long i=0; i<data.getChildrenCount(); i++)
                {
                    Restaurant restaurant = new Restaurant(String.valueOf(data.child(i+"").child("name").getValue()));
                    DataSnapshot menuData = data.child(i+"").child("menus");
                    for (long j=0; j<menuData.getChildrenCount(); j++)
                    {
                        String menuName = String.valueOf(menuData.child(j+"").child("name").getValue());
                        Log.d(TAG, "Menu name: " + menuName);
                        Menu menu = new Menu(String.valueOf(menuData.child(j+"").child("name").getValue()));
                        DataSnapshot dishes = menuData.child(j+"").child("dishes");
                        for (long k = 0; k < dishes.getChildrenCount(); k++)
                        {
                            Dish dish = new Dish(String.valueOf(dishes.child(k + "").child("name").getValue()),
                                    null,
                                    Float.parseFloat(String.valueOf(dishes.child(k + "").child("price").getValue())));
                            menu.add(dish);
                        }
                        restaurant.addMenu(menu);
                    }
                    Log.d(TAG, "Restaurant added!");
                    main.addRestaurant(restaurant);
                }
                Manager.end = true;
            }
        });

    }

    @Override
    public Void doInBackground(Void... v)
    {
        this.getData();
        //this.end = true;
        while (!Manager.end);
        Log.d(TAG, "Fine");
        return null;
    }

    public boolean isEnded() {return Manager.end;}
}
