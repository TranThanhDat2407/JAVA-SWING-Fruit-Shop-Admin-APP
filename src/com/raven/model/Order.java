/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.raven.model;

import java.util.Date;

/**
 *
 * @author PC
 */
public class Order {
    int id;
    int user_id;
    int user_payment_method_id;
    String shipping_address;
    float total_amount;
    boolean order_status;
    Date create_at;
    Date update_at;

    public Order() {
    }

    public Order(int id, int user_id, int user_payment_method_id, String shipping_address, float total_amount, boolean order_status, Date create_at, Date update_at) {
        this.id = id;
        this.user_id = user_id;
        this.user_payment_method_id = user_payment_method_id;
        this.shipping_address = shipping_address;
        this.total_amount = total_amount;
        this.order_status = order_status;
        this.create_at = create_at;
        this.update_at = update_at;
    }

    
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getUser_payment_method_id() {
        return user_payment_method_id;
    }

    public void setUser_payment_method_id(int user_payment_method_id) {
        this.user_payment_method_id = user_payment_method_id;
    }

    

    public float getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(float total_amount) {
        this.total_amount = total_amount;
    }

    public boolean isOrder_status() {
        return order_status;
    }

    public void setOrder_status(boolean order_status) {
        this.order_status = order_status;
    }

    public Date getCreate_at() {
        return create_at;
    }

    public void setCreate_at(Date create_at) {
        this.create_at = create_at;
    }

    public Date getUpdate_at() {
        return update_at;
    }

    public void setUpdate_at(Date update_at) {
        this.update_at = update_at;
    }

    public String getShipping_address() {
        return shipping_address;
    }

    public void setShipping_address(String shipping_address) {
        this.shipping_address = shipping_address;
    }

    @Override
    public String toString() {
        return "Order{" + "id=" + id + ", user_id=" + user_id + ", user_payment_method_id=" + user_payment_method_id + ", shipping_address=" + shipping_address + ", total_amount=" + total_amount + ", order_status=" + order_status + ", create_at=" + create_at + ", update_at=" + update_at + '}';
    }
    
    

    
    
    
}
