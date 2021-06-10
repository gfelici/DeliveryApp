package com.delivery.deliveryapp.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Order extends ArrayList<ObjectQuantity<Dish>> implements Serializable {

    private Restaurant restaurant; //ref to restaurant where the order was taken

    public Order(Restaurant restaurant)
    {
        this.restaurant = restaurant;
    }
}
