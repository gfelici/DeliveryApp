package com.delivery.deliveryapp.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Restaurant implements Serializable{

    private String name;
    private ArrayList<Menu> menus;

    public Restaurant(String name)
    {
        this.name = name;
        this.menus = new ArrayList<Menu>();
    }

    public Restaurant(String name, ArrayList<Menu> menus)
    {
        super();
        this.name = name;
        this.menus = menus;
    }

    public String getName() {return this.name;}
    public ArrayList<Menu> getMenus() {return this.menus;}

    public void addMenu(Menu menu) {this.menus.add(menu);}

}
