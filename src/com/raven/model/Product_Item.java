
package com.raven.model;

import java.util.Date;

/**
 *
 * @author PC
 */
public class Product_Item {
    private int id;
    private int product_id;
    private int qty_in_stock;
    private float price;
    private float original_price;
    private Date create_at;
    private Date update_at;

    public Product_Item() {
    }

    public Product_Item(int id, int product_id, int qty_in_stock, float price, float original_price, Date create_at, Date update_at) {
        this.id = id;
        this.product_id = product_id;
        this.qty_in_stock = qty_in_stock;
        this.price = price;
        this.original_price = original_price;
        this.create_at = create_at;
        this.update_at = update_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public int getQty_in_stock() {
        return qty_in_stock;
    }

    public void setQty_in_stock(int qty_in_stock) {
        this.qty_in_stock = qty_in_stock;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public double getOriginal_price() {
        return original_price;
    }

    public void setOriginal_price(float original_price) {
        this.original_price = original_price;
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

    @Override
    public String toString() {
        return "Product_Item{" + "id=" + id + ", product_id=" + product_id + ", qty_in_stock=" + qty_in_stock + ", price=" + price + ", original_price=" + original_price + ", create_at=" + create_at + ", update_at=" + update_at + '}';
    }
    
}
