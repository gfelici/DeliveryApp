package com.delivery.deliveryapp.models;

import android.os.Parcelable;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class Menu implements Serializable{

    private String name;
    private ArrayList<Dish> dishses;

    public Menu(String name)
    {
        this.name = name;
        dishses = new ArrayList<Dish>();
    }

    public Menu(String name, ArrayList<Dish> dishes)
    {
        this.name = name;
        this.dishses = dishes;
    }

    public String getName() {return this.name;}
    public ArrayList<Dish> getDishses() {return this.dishses;}

    public void add(Dish dish)
    {
        this.dishses.add(dish);
    }
}
