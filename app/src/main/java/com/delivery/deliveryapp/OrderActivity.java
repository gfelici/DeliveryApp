package com.delivery.deliveryapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.delivery.deliveryapp.models.Order;
import com.delivery.deliveryapp.models.Restaurant;

public class OrderActivity extends Activity {

    private Order order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_activity);
        Intent intent = getIntent();
        this.order = (Order) intent.getExtras().getSerializable("order");
    }
}
