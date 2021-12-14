package com.delivery.deliveryapp.models

import java.io.Serializable

class DishQuantity(val dish: Dish) : Serializable {

    var quantity: Int = 0

    init {
        this.quantity = 0
    }

    constructor(dish: Dish, quantity: Int) : this(dish) {
        this.quantity = quantity
    }

    fun incQuantity() {
        this.quantity++
    }
}
