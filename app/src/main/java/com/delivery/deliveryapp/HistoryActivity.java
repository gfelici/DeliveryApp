package com.delivery.deliveryapp;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintSet;

import com.delivery.deliveryapp.Firebase.DbManager;
import com.delivery.deliveryapp.utils.Utils;

public class HistoryActivity extends Activity {

    LinearLayout history;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
    }

    @Override
    protected void onStart() {
        super.onStart();
        history = (LinearLayout) findViewById(R.id.historylayout);
        DbManager dbManager = new DbManager();
        dbManager.getOrder(this);
    }

    /*
    time è una stringa che rappresenta l'orario di consegna
     */
    public void addOrder(String restaurant, double total, String deliveryTime)
    {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(Utils.dpToPx(10, this), Utils.dpToPx(10, this),
                Utils.dpToPx(10, this), Utils.dpToPx(10, this));
        layout.setLayoutParams(params);

        TextView resText = new TextView(this);
        resText.setText(restaurant);
        resText.setTextColor(Color.BLACK);
        resText.setTextSize(25);

        TextView totalText = new TextView(this);
        totalText.setText("Totale: " + total);
        totalText.setTextColor(Color.BLACK);
        totalText.setTextSize(20);

        //consegna alle
        TextView deliveryText = new TextView(this);
        deliveryText.setText("time here");
        deliveryText.setTextColor(Color.BLACK);
        deliveryText.setTextSize(16);
        deliveryText.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);

        layout.setBackground(getDrawable(R.drawable.box));

        layout.addView(resText);
        layout.addView(totalText);
        layout.addView(deliveryText);

        history.addView(layout);
    }
}