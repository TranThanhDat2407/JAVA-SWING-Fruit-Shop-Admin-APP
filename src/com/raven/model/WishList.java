
package com.raven.model;

/**
 *
 * @author PC
 */
public class WishList {
    private int id;
    private int user_id;
    private int product_item_id;

    public WishList() {
    }

    public WishList(int id, int user_id, int product_item_id) {
        this.id = id;
        this.user_id = user_id;
        this.product_item_id = product_item_id;
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

    public int getProduct_item_id() {
        return product_item_id;
    }

    public void setProduct_item_id(int product_item_id) {
        this.product_item_id = product_item_id;
    }

    @Override
    public String toString() {
        return "WishList{" + "id=" + id + ", user_id=" + user_id + ", product_item_id=" + product_item_id + '}';
    }
    
    
}
