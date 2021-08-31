package com.delivery.deliveryapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.delivery.deliveryapp.models.Dish;
import com.delivery.deliveryapp.models.ObjectQuantity;
import com.delivery.deliveryapp.models.Order;

public class DishActivity extends Activity {

    private final static String TAG = "INFO";
    private Dish dish;
    private int initialCount;
    //private Order order; //ref to general order. Dishes are added here.

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dish_activity);

        Intent intent = getIntent();
        this.dish = (Dish) intent.getExtras().getSerializable("dish");
        this.initialCount = intent.getExtras().getInt("count");
        TextView dishTextView = findViewById(R.id.dishTextView);
        dishTextView.setText(dish.getName());

        TextView counter = findViewById(R.id.counter);
        Log.v(TAG, "Quantity: " + initialCount);
        counter.setText(""+intent.getExtras().getInt("count"));

        //this.order = (Order) intent.getExtras().getSerializable("order");
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        TextView counter = findViewById(R.id.counter);
        Log.v(TAG, "Quantity: " + initialCount);
        counter.setText(""+initialCount);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        //finish();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        //finish();
    }

    @Override
    public void finish()
    {
        Intent intent = getIntent();
        intent.putExtra("dish", this.dish);
        TextView counter = findViewById(R.id.counter);
        int count = Integer.parseInt(counter.getText().toString());
        intent.putExtra("count", count);
        setResult(Activity.RESULT_OK, intent);
        super.finish();
    }

    private void updateOrder()
    {
        TextView counter = findViewById(R.id.counter);
        String numText = counter.getText().toString();
        int num = Integer.parseInt(numText);
        if (num > 0) {
            //order.setDishQuantity(this.dish, num);
            //order.addDishes(this.dish, num);
            //Toast.makeText(this, "Aggiunto agli ordini", Toast.LENGTH_LONG).show();
        }
    }

    public void onClick(View v)
    {
        Button button = (Button) v;
        String text = button.getText().toString();
        TextView counter = findViewById(R.id.counter);
        if (text.equals("+"))
        {
            String numText = counter.getText().toString();
            int val = Integer.parseInt(numText);
            val++;
            counter.setText(val+"");
        }
        else if (text.equals("-"))
        {
            String numText = counter.getText().toString();
            int val = Integer.parseInt(numText);
            if (val > 0)
            {
                val--;
                counter.setText(val + "");
            }
        }
        //updateOrder();
    }
}