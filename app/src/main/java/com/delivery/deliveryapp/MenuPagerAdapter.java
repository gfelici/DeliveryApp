package com.delivery.deliveryapp;

import android.app.Activity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.delivery.deliveryapp.models.Restaurant;

public class MenuPagerAdapter extends FragmentPagerAdapter {

    private Restaurant restaurant;

    public MenuPagerAdapter(FragmentManager fm, Restaurant restaurant)
    {
        super(fm);
        this.restaurant = restaurant;
    }

    @Override
    public int getCount()
    {
        return restaurant.getMenus().size();
    }

    @Override
    public Fragment getItem(int position)
    {
        return new MenuFragment(this.restaurant.getMenus().get(position));
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return this.restaurant.getMenus().get(position).getName();
        }
    }
