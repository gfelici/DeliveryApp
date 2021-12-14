package com.delivery.deliveryapp.models;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

public class Order implements Serializable {

    private Restaurant restaurant; //ref to restaurant where the order was taken
    private ArrayList<DishQuantity> dishes;
    private static final String TAG = "INFO";

    public Order(Restaurant restaurant)
    {
        this.restaurant = restaurant;
        this.dishes = new ArrayList<DishQuantity>();
    }

    public ArrayList<DishQuantity> getDishes()
    {
        return this.dishes;
    }
    public void setDishes(ArrayList<DishQuantity> dishes)
    {
        this.dishes = dishes;
    }

    public void addDishes(Dish dish, int num)
    {
        DishQuantity dishesToAdd = new DishQuantity(dish, num);
        this.dishes.add(dishesToAdd);
    }

    public void setDishQuantity(Dish dish, int quantity)
    {
        for (DishQuantity dq : dishes)
            if (dq.getDish().getName().equals(dish.getName()))
            {
                if (quantity <= 0)
                    dishes.remove(dq);
                else
                    dq.setQuantity(quantity);
                return;
            }
        //dish is not in the order, must be added
        if (quantity > 0)
            dishes.add(new DishQuantity(dish, quantity));
    }

    public int getDishQuantity(Dish dish)
    {
        for (DishQuantity dq : dishes) {
            Log.v(TAG, "Dish: " + dq.getDish().getName());
            Log.v(TAG, "Dish to found: " + dish.getName());
            if (dq.getDish().getName().equals(dish.getName()))
                return dq.getQuantity();
        }
        return 0;
    }

    public float getTotalPrice()
    {
        float totalPrice = 0.0f;
        for (DishQuantity dq : dishes) {
            float dishesPrice = dq.getDish().getPrice() * dq.getQuantity();
            totalPrice += dishesPrice;
        }

        return totalPrice;
    }

    public String toString()
    {
        String orderStr = "";
        for (DishQuantity dq : dishes) {
            Dish dish = dq.getDish();
            orderStr = orderStr + dish.getName() + ":"+ dq.getQuantity()+" ";
        }
        return orderStr;
    }

    public Restaurant getRestaurant()
    {
        return this.restaurant;
    }
}