package com.delivery.deliveryapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.delivery.deliveryapp.models.Dish;
import com.delivery.deliveryapp.models.Menu;
import com.delivery.deliveryapp.models.ObjectQuantity;
import com.delivery.deliveryapp.models.Order;
import com.delivery.deliveryapp.models.Restaurant;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;

public class RestaurantActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager pager;
    private static final String TAG = "INFO";
    private static final String ORDER = "ORDER";

    private Restaurant restaurant;
    private Order order; //ordine per questo ristorante. E' l'equivalente di un carrello

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_activity);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //è stato usato appCompactActivity perchè ha il metodo getSupportFragmentManager
        getSupportActionBar().hide();

        Intent intent = getIntent();
        Restaurant restaurant = (Restaurant) intent.getExtras().getSerializable("restaurant");
        Log.v(TAG, "Restaurant name: " + restaurant.getName());
        TextView textView = findViewById(R.id.restaurantName);
        textView.setText(restaurant.getName());
        this.restaurant = restaurant;

        //creo una nuova ordinazione per questo ristorante
        this.order = new Order(restaurant);

        tabLayout = findViewById(R.id.tab_layout);
        pager = findViewById(R.id.pager);
        pager.setAdapter(new MenuPagerAdapter(this.getSupportFragmentManager(), this.restaurant, this.order));
        tabLayout.setupWithViewPager(pager);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }
    
    private void addTab(String tabName, ArrayList<String> items)
    {
        TabLayout.Tab tab = tabLayout.newTab();
        tab.setText(tabName);
        tabLayout.addTab(tab);
    }

    public void onClick(View v)
    {
        Intent orderIntent = new Intent(RestaurantActivity.this, OrderActivity.class);
        orderIntent.putExtra("order", this.order);
        startActivity(orderIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK){ //&& requestCode == 1) {
            int count = data.getExtras().getInt("count");
            Dish dish = (Dish) data.getExtras().getSerializable("dish");
            Log.v(TAG, "Returned value: "+count);
            this.order.setDishQuantity(dish, count);
            Log.v(TAG, "Dish quantity: "+this.order.getDishQuantity(dish));
            Log.v(ORDER, order.toString());
        }
    }
}