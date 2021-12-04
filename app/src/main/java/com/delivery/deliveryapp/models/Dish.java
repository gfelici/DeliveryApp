package com.delivery.deliveryapp.models;

import java.io.Serializable;

public class Dish implements Serializable{

    private String name;
    private float price;

    public Dish()
    {
        this.name = "no name";
        this.price = 0.0f;
    }

    public Dish(String name, float price)
    {
        this.name = name;
        this.price = price;
    }

    public String getName() {return this.name;}
    public float getPrice() {return this.price;}
}
