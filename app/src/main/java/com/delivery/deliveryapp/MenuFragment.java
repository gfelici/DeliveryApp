package com.delivery.deliveryapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.delivery.deliveryapp.models.Dish;
import com.delivery.deliveryapp.models.Menu;
import com.delivery.deliveryapp.models.ObjectQuantity;
import com.delivery.deliveryapp.models.Order;
import com.delivery.deliveryapp.utils.Utils;

public class MenuFragment extends Fragment {

    private static final String TAG = "INFO2";
    private Menu menu;
    private Order order;

    private Bundle bundle;

    public static MenuFragment newInstance(Menu menu, Order order) {
        Bundle args = new Bundle();
        args.putSerializable("menu", menu);
        args.putSerializable("order", order);
        MenuFragment f = new MenuFragment();
        f.setArguments(args);
        return f;
    }

    public MenuFragment()
    {
        super(R.layout.menufragment);
    }

    public MenuFragment(Menu menu, Order order)
    {
        super(R.layout.menufragment);
        this.menu = menu;
        this.order = order;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        bundle = this.getArguments();

        this.menu = (Menu) bundle.get("menu");
        this.order = (Order) bundle.getSerializable("order");


        final View rootView = inflater.inflate(R.layout.menufragment, container, false);
        LinearLayout menuLayout = (LinearLayout) rootView.findViewById(R.id.menu_layout);
        for (final Dish d : this.menu.getDishses())
        {
            LinearLayout l = new LinearLayout(this.getContext()); //TODO cambiare layout per nomi lunghi
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            params.setMargins(Utils.dpToPx(10, this.getContext()), Utils.dpToPx(10, this.getContext()),
                    Utils.dpToPx(10, this.getContext()), Utils.dpToPx(10, this.getContext()));
            l.setLayoutParams(params);
            l.setBackground(this.getActivity().getDrawable(R.drawable.box));

            TextView dishText = new TextView(this.getContext());
            params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            params.setMarginStart(Utils.dpToPx(10,rootView.getContext()));
            dishText.setLayoutParams(params);
            dishText.setGravity(Gravity.LEFT);
            dishText.setText(d.getName());
            dishText.setTextSize(20);
            dishText.setTextColor(Color.BLACK);

            TextView priceText = new TextView(this.getContext());
            params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    Utils.dpToPx(50, this.getContext()));
            params.setMarginStart(Utils.dpToPx(10,rootView.getContext()));
            priceText.setLayoutParams(params);
            priceText.setGravity(Gravity.BOTTOM | Gravity.RIGHT);
            priceText.setText(d.getPrice() + "€");
            priceText.setTextSize(20);
            priceText.setTextColor(Color.BLACK);

            l.addView(dishText);
            l.addView(priceText);
            l.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.v(TAG, "Item clicked: " + d.getName());
                    Intent myIntent = new Intent(getActivity(), DishActivity.class);
                    myIntent.putExtra("dish", d);
                    Log.v(TAG, "Dish quantity in fragment:" + order.getDishQuantity(d));
                    myIntent.putExtra("count", order.getDishQuantity(d));
                    //startActivity(myIntent);
                    startActivityForResult(myIntent,1);
                }
            });

            menuLayout.addView(l);
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("menu", this.menu);
        outState.putSerializable("order", this.order);
    }
}