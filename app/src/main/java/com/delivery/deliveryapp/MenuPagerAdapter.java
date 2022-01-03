package com.delivery.deliveryapp;

import android.app.Activity;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.delivery.deliveryapp.models.Order;
import com.delivery.deliveryapp.models.Restaurant;

import java.io.Serializable;

public class MenuPagerAdapter extends FragmentPagerAdapter {

    private Restaurant restaurant;
    private Order order;

    MenuFragment fragments[];

    public MenuPagerAdapter(FragmentManager fm, Restaurant restaurant, Order order)
    {
        super(fm);
        this.restaurant = restaurant;
        this.order = order;
        fragments = new MenuFragment[restaurant.getMenus().size()];
    }

    @Override
    public int getCount()
    {
        return restaurant.getMenus().size();
    }

    @Override
    public Fragment getItem(int position)
    {
        return MenuFragment.newInstance(this.restaurant.getMenus().get(position), order);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return this.restaurant.getMenus().get(position).getName();
        }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        MenuFragment fragment = (MenuFragment) super.instantiateItem(container, position);
        fragments[position] = fragment;
        return fragment;
    }

}
