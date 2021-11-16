package com.delivery.deliveryapp.models;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

public class Order implements Serializable {

    private Restaurant restaurant; //ref to restaurant where the order was taken
    private ArrayList<ObjectQuantity<Dish>> dishes;
    private static final String TAG = "INFO";

    public Order(Restaurant restaurant)
    {
        this.restaurant = restaurant;
        this.dishes = new ArrayList<ObjectQuantity<Dish>>();
    }

    public ArrayList<ObjectQuantity<Dish>> getDishes()
    {
        return this.dishes;
    }
    public void setDishes(ArrayList<ObjectQuantity<Dish>> dishes)
    {
        this.dishes = dishes;
    }

    public void addDishes(Dish dish, int num)
    {
        ObjectQuantity<Dish> dishesToAdd = new ObjectQuantity<Dish>(dish, num);
        this.dishes.add(dishesToAdd);
    }

    public void setDishQuantity(Dish dish, int quantity)
    {
        for (ObjectQuantity<Dish> dq : dishes)
            if (dq.getObject().getName().equals(dish.getName()))
            {
                if (quantity <= 0)
                    dishes.remove(dq);
                else
                    dq.setQuantity(quantity);
                return;
            }
        //dish is not in the order, must be added
        if (quantity > 0)
            dishes.add(new ObjectQuantity<Dish>(dish, quantity));
    }

    public int getDishQuantity(Dish dish)
    {
        for (ObjectQuantity<Dish> dq : dishes) {
            Log.v(TAG, "Dish: " + dq.getObject().getName());
            Log.v(TAG, "Dish to found: " + dish.getName());
            if (dq.getObject().getName().equals(dish.getName()))//TODO BUG QUI (non torna l'equals)
                return dq.getQuantity();
        }
        return 0;
    }

    public float getTotalPrice()
    {
        float totalPrice = 0.0f;
        for (ObjectQuantity<Dish> dq : dishes) {
            float dishesPrice = dq.getObject().getPrice() * dq.getQuantity();
            totalPrice += dishesPrice;
        }

        return totalPrice;
    }

    public String toString()
    {
        String orderStr = "";
        for (ObjectQuantity<Dish> dq : dishes) {
            Dish dish = (Dish) dq.getObject();
            orderStr = orderStr + dish.getName() + ":"+ dq.getQuantity()+" ";
        }
        return orderStr;
    }
}