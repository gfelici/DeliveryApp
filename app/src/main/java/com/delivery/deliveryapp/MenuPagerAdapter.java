package com.delivery.deliveryapp;

import android.app.Activity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.delivery.deliveryapp.models.Order;
import com.delivery.deliveryapp.models.Restaurant;

public class MenuPagerAdapter extends FragmentPagerAdapter {

    private Restaurant restaurant;
    private Order order;

    public MenuPagerAdapter(FragmentManager fm, Restaurant restaurant, Order order)
    {
        super(fm);
        this.restaurant = restaurant;
        this.order = order;
    }

    @Override
    public int getCount()
    {
        return restaurant.getMenus().size();
    }

    @Override
    public Fragment getItem(int position)
    {
        return new MenuFragment(this.restaurant.getMenus().get(position), order);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return this.restaurant.getMenus().get(position).getName();
        }
    }
