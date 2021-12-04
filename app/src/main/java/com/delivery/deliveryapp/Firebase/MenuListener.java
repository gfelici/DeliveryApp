package com.delivery.deliveryapp.Firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.delivery.deliveryapp.models.Dish;
import com.delivery.deliveryapp.models.Menu;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public class MenuListener<T> implements OnCompleteListener<T> {

    private Menu menu;
    private String menuName;
    private int count;//conta quanti piatti vengono aggiunti
    private boolean canRet;

    public MenuListener(String menuName)
    {
        this.menuName = menuName;
        this.menu = null;
        count = 0;
        canRet = false;
    }

    @Override
    public void onComplete(@NonNull Task<T> task) {
        if (task.isSuccessful()) {
            DocumentSnapshot snapshot = (DocumentSnapshot) task.getResult();
            Menu m = new Menu(menuName);
            for (String name : snapshot.getData().keySet()) {
                //Log.v(TAG, "Dish name: " + name);
                double price = snapshot.getDouble(name);
                Dish dish = new Dish(name, (float) price);
                m.add(dish);
            }

            this.menu = m;
        }
    }

    public Menu getMenu() {return this.menu; }
    public boolean canRet() {return canRet;}
}
