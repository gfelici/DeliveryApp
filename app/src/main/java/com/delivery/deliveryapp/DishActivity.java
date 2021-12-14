package com.delivery.deliveryapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.delivery.deliveryapp.models.Dish;

public class DishActivity extends Activity {

    private final static String TAG = "INFO";
    private Dish dish;
    private int initialCount;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dish_activity);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            this.dish = (Dish) intent.getExtras().getSerializable("dish");
            this.initialCount = intent.getExtras().getInt("count");
        }
        else
        {
            this.dish = (Dish) savedInstanceState.getSerializable("dish");
            this.initialCount = savedInstanceState.getInt("count");
        }
        TextView dishTextView = findViewById(R.id.dishTextView);
        dishTextView.setText(dish.getName());

        TextView counter = findViewById(R.id.counter);
        Log.v(TAG, "Quantity: " + initialCount);
        counter.setText(""+this.initialCount);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        TextView counter = findViewById(R.id.counter);
        this.initialCount = Integer.parseInt(counter.getText().toString());
        outState.putSerializable("dish", this.dish);
        outState.putInt("count", initialCount);
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
    public void finish()
    {
        Intent intent = getIntent();
        intent.putExtra("dish", this.dish);
        TextView counter = findViewById(R.id.counter);
        int count = Integer.parseInt(counter.getText().toString());
        intent.putExtra("count", count);
        setResult(Activity.RESULT_OK, intent);
        if (count>0 &&  !(initialCount == count))
            Toast.makeText(this, "Aggiunto agli ordini", Toast.LENGTH_LONG).show();//TODO aggiungere a res/strings
        super.finish();
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