package com.delivery.deliveryapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.delivery.deliveryapp.Firebase.DbManager;
import com.delivery.deliveryapp.models.Dish;
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
    private MenuPagerAdapter menuPagerAdapter;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_activity);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //è stato usato appCompactActivity perchè ha il metodo getSupportFragmentManager
        getSupportActionBar().hide();

        bundle = savedInstanceState;
        if (bundle == null)
        {
            Intent intent = getIntent();
            this.restaurant = (Restaurant) intent.getExtras().getSerializable("restaurant");
            this.order = new Order(restaurant);
            bundle = new Bundle();
            bundle.putSerializable("restaurant", this.restaurant);
            bundle.putSerializable("order", this.order);
        }
        else
        {
            this.restaurant = (Restaurant) bundle.getSerializable("restaurant");
            this.order = (Order) bundle.getSerializable("order");
        }

        this.menuPagerAdapter = new MenuPagerAdapter(this.getSupportFragmentManager(), this.restaurant, this.order);
        TextView textView = findViewById(R.id.restaurantName);
        textView.setText(restaurant.getName());
        tabLayout = findViewById(R.id.tab_layout);
        pager = findViewById(R.id.pager);
        pager.setAdapter(menuPagerAdapter);
        tabLayout.setupWithViewPager(pager);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("restaurant", bundle.getSerializable("restaurant"));
        outState.putSerializable("order", bundle.getSerializable("order"));
    }

    public void onClick(View v)
    {
        Intent orderIntent = new Intent(RestaurantActivity.this, OrderActivity.class);
        orderIntent.putExtra("order", this.order);
        DbManager dbManager = new DbManager();
        dbManager.checkOrder(this, orderIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK){ //&& requestCode == 1) {
            int count = data.getExtras().getInt("count");
            Dish dish = (Dish) data.getExtras().getSerializable("dish");
            //Log.v(TAG, "Returned value: "+count);
            this.order.setDishQuantity(dish, count);
            bundle.putSerializable("order", this.order);
            //Log.v(TAG, "Dish quantity: "+this.order.getDishQuantity(dish));
        }
    }
}