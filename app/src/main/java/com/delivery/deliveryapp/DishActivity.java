package com.delivery.deliveryapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.delivery.deliveryapp.models.Dish;

public class DishActivity extends Activity {

    private final static String TAG = "INFO2";
    private Dish dish;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dish_activity);

        Intent intent = getIntent();
        this.dish = (Dish) intent.getExtras().getSerializable("dish");
        TextView dishTextView = findViewById(R.id.dishTextView);
        dishTextView.setText(dish.getName());
    }

    @Override
    protected void onStart()
    {
        super.onStart();
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
    }
}
