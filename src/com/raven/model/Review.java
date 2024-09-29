
package com.raven.model;

import java.util.Date;

/**
 *
 * @author PC
 */
public class Review {
    private int id;
    private int ordered_product_id;
    private int user_id;
    private int rating_value;
    private String comment;
    private Date create_at;
    private Date update_at;

    public Review() {
    }

    public Review(int id, int ordered_product_id, int user_id, int rating_value, String comment, Date create_at, Date update_at) {
        this.id = id;
        this.ordered_product_id = ordered_product_id;
        this.user_id = user_id;
        this.rating_value = rating_value;
        this.comment = comment;
        this.create_at = create_at;
        this.update_at = update_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrdered_product_id() {
        return ordered_product_id;
    }

    public void setOrdered_product_id(int ordered_product_id) {
        this.ordered_product_id = ordered_product_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getRating_value() {
        return rating_value;
    }

    public void setRating_value(int rating_value) {
        this.rating_value = rating_value;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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
        return "Review{" + "id=" + id + ", ordered_product_id=" + ordered_product_id + ", user_id=" + user_id + ", rating_value=" + rating_value + ", comment=" + comment + ", create_at=" + create_at + ", update_at=" + update_at + '}';
    }
    
    
}
