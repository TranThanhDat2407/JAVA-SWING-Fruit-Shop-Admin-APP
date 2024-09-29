
package com.raven.model;

/**
 *
 * @author PC
 */
public class Gallery {
    private int id;
    private int product_id;
    private String thumbnail;

    public Gallery(int id, int product_id, String thumbnail) {
        this.id = id;
        this.product_id = product_id;
        this.thumbnail = thumbnail;
    }

    public Gallery() {
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

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    @Override
    public String toString() {
        return "Gallery{" + "id=" + id + ", product_id=" + product_id + ", thumbnail=" + thumbnail + '}';
    }
    
    
}
