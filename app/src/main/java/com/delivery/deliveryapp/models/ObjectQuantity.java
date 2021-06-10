package com.delivery.deliveryapp.models;

import java.io.Serializable;

public class ObjectQuantity<T> implements Serializable {

    private T obj;
    private int quantity;

    public ObjectQuantity(T obj)
    {
        this.obj = obj;
        this.quantity = 0;
    }

    public ObjectQuantity(T obj, int quantity)
    {
        this.obj = obj;
        this.quantity = quantity;
    }

    public T getObject(){return this.obj;}
    public int getQuantity() {return this.quantity;}
    public void setQuantity(int quantity) {this.quantity = quantity;}
    public void incQuantity() {this.quantity++;}
}
