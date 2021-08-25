package com.delivery.deliveryapp.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Order implements Serializable {

    private Restaurant restaurant; //ref to restaurant where the order was taken
    private ArrayList<ObjectQuantity<Dish>> dishes;

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
            if (dq.getObject().equals(dish))
            {
                dq.setQuantity(quantity);
                return;
            }
        //dish is not in the order, must be added
        dishes.add(new ObjectQuantity<Dish>(dish, quantity));
    }

    public int getDishQuantity(Dish dish)
    {
        for (ObjectQuantity<Dish> dq : dishes)
            if (dq.getObject().equals(dish))
                return dq.getQuantity();
        return 0;
    }
}