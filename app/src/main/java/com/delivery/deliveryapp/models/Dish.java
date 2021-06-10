package com.delivery.deliveryapp.models;

import java.io.Serializable;

public class Dish implements Serializable{

    //private img
    private String name;
    private String desc;
    private float price;

    public Dish()
    {
        this.name = "no name";
        this.desc = "no description";
        this.price = 0.0f;
    }

    public Dish(String name, String desc, float price)
    {
        this.name = name;
        this.desc = desc;
        this.price = price;
    }

    public String getName() {return this.name;}
    public String getDesc() {return this.desc;}
    public float getPrice() {return this.price;}


}
