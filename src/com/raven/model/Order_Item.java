/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.raven.model;

import dao.ProductDAO;

/**
 *
 * @author PC
 */
public class Order_Item {
    int id;
    int order_id;
    int product_item_id;
    int qty;
    float price;
    ProductDAO prodDAO = new ProductDAO();
    public Order_Item() {
    }

    public Order_Item(int id, int order_id, int product_item_id, int qty, float price) {
        this.id = id;
        this.order_id = order_id;
        this.product_item_id = product_item_id;
        this.qty = qty;
        this.price = price;
    }
    
    public String getName(int product_item_id)
    {
        Product p = prodDAO.selectByProductItemId(product_item_id);
        return p.getName();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public int getProduct_item_id() {
        return product_item_id;
    }

    public void setProduct_item_id(int product_item_id) {
        this.product_item_id = product_item_id;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    @Override
   public String toString() {
        return "id=" + id + ", order_id=" + order_id + ", Product name= " + getName(this.getProduct_item_id());
    }
    
}
