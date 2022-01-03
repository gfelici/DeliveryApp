package com.delivery.deliveryapp.models

import java.io.Serializable
import java.util.ArrayList

class Restaurant(val name: String) : Serializable {

    var menus: ArrayList<Menu>

    init{
        this.menus = ArrayList()
    }

    constructor(name: String, menus: ArrayList<Menu>) : this(name) {
        this.menus = menus
    }

    fun addMenu(menu: Menu) {
        this.menus.add(menu)
    }

}
