package com.delivery.deliveryapp.models

import java.io.Serializable
import java.util.ArrayList

class Menu(val name: String) : Serializable {

    var dishes: ArrayList<Dish>
        private set


    init {
        this.dishes = ArrayList()
    }

    constructor(name: String, dishes: ArrayList<Dish>) : this(name) {
        this.dishes = dishes
    }

    fun add(dish: Dish) {
        this.dishes.add(dish)
    }
}
