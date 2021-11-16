package com.delivery.deliveryapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import com.delivery.deliveryapp.models.Dish;
import com.delivery.deliveryapp.models.ObjectQuantity;
import com.delivery.deliveryapp.models.Order;
import com.delivery.deliveryapp.models.Restaurant;
import com.delivery.deliveryapp.utils.Utils;
import com.google.common.io.LineReader;

public class OrderActivity extends Activity {

    private Order order;
    private LinearLayout orderLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_activity);
        Intent intent = getIntent();
        this.order = (Order) intent.getExtras().getSerializable("order");

        this.orderLayout = findViewById(R.id.orderLayout);

        if (order.getDishes().size() == 0) { //no dishes in this order
            TextView noOrderText = new TextView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            //params.setMarginStart(Utils.dpToPx(10,this));
            noOrderText.setLayoutParams(params);
            noOrderText.setGravity(Gravity.CENTER);
            noOrderText.setText("Ancora nessun ordine");
            noOrderText.setTextSize(30);
            LinearLayout layout = findViewById(R.id.orderLayout);
            layout.addView(noOrderText);
        }
        else {
            for (ObjectQuantity<Dish> dq : order.getDishes())
                addDishCounter(dq);
        }
        TextView total = findViewById(R.id.totalPrice);
        total.setText("Totale: " + order.getTotalPrice() + " €");
    }

    public void addDishCounter(final ObjectQuantity<Dish> dq)
    {
        LinearLayout l = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        l.setLayoutParams(params);
        l.setOrientation(LinearLayout.VERTICAL);

        TextView textName = new TextView(this);
        params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Utils.dpToPx(50, this));
        params.setMarginStart(Utils.dpToPx(10,this));
        textName.setLayoutParams(params);
        textName.setGravity(Gravity.LEFT);
        textName.setText(dq.getObject().getName());
        textName.setTextSize(20);
        l.addView(textName);

        LinearLayout count_and_price_layout = new LinearLayout(this);
        count_and_price_layout.setLayoutParams(params);
        count_and_price_layout.setOrientation(LinearLayout.HORIZONTAL);
        //count_and_price_layout.setGravity(Gravity.END | Gravity.RIGHT);

        TextView textQuant = new TextView(this);
        params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                Utils.dpToPx(50, this));
        params.setMarginStart(Utils.dpToPx(10,this));
        textQuant.setLayoutParams(params);
        //textQuant.setGravity(Gravity.RIGHT);
        textQuant.setText("Quantità: "+dq.getQuantity());
        textQuant.setTextSize(15);
        count_and_price_layout.addView(textQuant);

        TextView textPrice = new TextView(this);
        params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                Utils.dpToPx(50, this));
        params.setMarginStart(Utils.dpToPx(10,this));
        textPrice.setLayoutParams(params);
        //textPrice.setGravity(Gravity.RIGHT);
        Float price = dq.getObject().getPrice()* dq.getQuantity();
        textPrice.setText("Prezzo: " +price+" €");
        textPrice.setTextSize(15);
        count_and_price_layout.addView(textPrice);

        l.addView(count_and_price_layout);
        orderLayout.addView(l);

        //minimo di spazio tra un ordine e l'altro
        Space space = new Space(this);
        //space.getLayoutParams().height = 10;
        orderLayout.addView(space);
    }
}
