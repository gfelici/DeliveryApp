package com.delivery.deliveryapp.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Menu implements Serializable{

    private String name;
    private ArrayList<Dish> dishes;

    public Menu(String name)
    {
        this.name = name;
        dishes = new ArrayList<Dish>();
    }

    public Menu(String name, ArrayList<Dish> dishes)
    {
        this.name = name;
        this.dishes = dishes;
    }

    public String getName() {return this.name;}
    public ArrayList<Dish> getDishes() {return this.dishes;}

    public void add(Dish dish)
    {
        this.dishes.add(dish);
    }
}
