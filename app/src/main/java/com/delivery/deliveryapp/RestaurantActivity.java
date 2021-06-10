package com.delivery.deliveryapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.delivery.deliveryapp.models.Menu;
import com.delivery.deliveryapp.models.Restaurant;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;

public class RestaurantActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager pager;
    private static final String TAG = "INFO";

    private Restaurant restaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_activity);

        Intent intent = getIntent();
        Restaurant restaurant = (Restaurant) intent.getExtras().getSerializable("restaurant");
        Log.v(TAG, "Restaurant name: " + restaurant.getName());
        TextView textView = findViewById(R.id.restaurantName);
        textView.setText(restaurant.getName());
        this.restaurant = restaurant;

        tabLayout = findViewById(R.id.tab_layout);
        pager = findViewById(R.id.pager);
        pager.setAdapter(new MenuPagerAdapter(this.getSupportFragmentManager(), this.restaurant));
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
    
}
