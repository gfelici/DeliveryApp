package com.delivery.deliveryapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import com.delivery.deliveryapp.Firebase.Manager;
import com.delivery.deliveryapp.models.Dish;
import com.delivery.deliveryapp.models.Menu;
import com.delivery.deliveryapp.models.Restaurant;
import com.delivery.deliveryapp.utils.Utils;

import java.util.ArrayList;

public class MainActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        for (int i=0; i<15; i++)
        {
            ArrayList<Menu> menus = new ArrayList<Menu>();

            Menu primi = new Menu("Primi");
            primi.add(new Dish("Tortelli", "Desc", 10.0f ));
            primi.add(new Dish("Pasta al pesto", "Desc", 5.0f ));
            primi.add(new Dish("Spaghetti alla carbonara", "Desc", 9.5f ));
            primi.add(new Dish());

            Menu secondi = new Menu("Secondi");
            secondi.add(new Dish("Fiorentina", "Desc", 30.0f));
            secondi.add(new Dish("Sformato di spinaci", "Desc", 7.5f));
            secondi.add(new Dish("Scaloppine al limone", "Desc", 9.0f));

            menus.add(primi);
            menus.add(secondi);

            final Restaurant restaurant = new Restaurant("I 7 mari", menus);

            addRestaurant(restaurant);
        }
        */

        Manager manager = new Manager(this);
        manager.execute();
        Log.d("INFO2", "Inizio");
        //while (!manager.isEnded());
        //try {Thread.sleep(5*1000);} catch (InterruptedException iex) {}
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    private void newActivity(final Restaurant restaurant)
    {
            Intent myIntent = new Intent(MainActivity.this, RestaurantActivity.class);
            myIntent.putExtra("restaurant", restaurant);
            MainActivity.this.startActivity(myIntent);
    }

    public void addRestaurant(final Restaurant restaurant)
    {
        LinearLayout restaurantLayout = findViewById(R.id.restaurantLayout);

        LinearLayout l = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        l.setLayoutParams(params);
        l.setOrientation(LinearLayout.HORIZONTAL);

        ImageView image = new ImageView(this);
        params = new LinearLayout.LayoutParams(Utils.dpToPx(100,this), Utils.dpToPx(100,this));
        image.setLayoutParams(params);
        image.setForegroundGravity(Gravity.LEFT);
        image.setImageResource(R.drawable.sushi);

        TextView textView = new TextView(this);
        params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMarginStart(Utils.dpToPx(10,this));
        textView.setLayoutParams(params);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setText(restaurant.getName());
        textView.setTextSize(20);

        l.addView(image);
        l.addView(textView);
        l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newActivity(restaurant);
            }
        });

        restaurantLayout.addView(l);

        //minimo di spazio tra un ristorante e l'altro
        Space space = new Space(this);
        //space.getLayoutParams().height = 10;
        restaurantLayout.addView(space);
    }
}