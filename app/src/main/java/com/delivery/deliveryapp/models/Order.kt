package com.delivery.deliveryapp.models

import android.util.Log

import java.io.Serializable
import java.util.ArrayList

class Order(val restaurant: Restaurant) : Serializable {

    var dishes: ArrayList<DishQuantity>
        private set

    val totalPrice: Float
        get() {
            var totalPrice = 0.0f
            for (dq in dishes) {
                val dishesPrice = dq.dish.price * dq.quantity
                totalPrice += dishesPrice
            }

            return totalPrice
        }

    init {
        this.dishes = ArrayList()
    }

    fun addDish(dish: Dish, num: Int) {
        val dishesToAdd = DishQuantity(dish, num)
        this.dishes.add(dishesToAdd)
    }

    fun setDishQuantity(dish: Dish, quantity: Int) {
        for (dq in dishes)
            if (dq.dish.name == dish.name) {
                if (quantity <= 0)
                    dishes.remove(dq)
                else
                    dq.quantity = quantity
                return
            }
        //dish is not in the order, must be added
        if (quantity > 0)
            dishes.add(DishQuantity(dish, quantity))
    }

    fun getDishQuantity(dish: Dish): Int {
        for (dq in dishes) {
            if (dq.dish.name == dish.name)
                return dq.quantity
        }
        return 0
    }

    override fun toString(): String {
        var orderStr = ""
        for (dq in dishes) {
            val dish = dq.dish
            orderStr = orderStr + dish.name + ":" + dq.quantity + " "
        }
        return orderStr
    }
}