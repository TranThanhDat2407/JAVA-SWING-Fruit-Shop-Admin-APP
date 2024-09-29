
package com.raven.model;


public class Category {
    private int id;
    private int parent_id;
    private String name;

    public Category() {
    }

    public Category(int id, int parent_id, String name) {
        this.id = id;
        this.parent_id = parent_id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParent_id() {
        return parent_id;
    }

    public void setParent_id(int parent_id) {
        this.parent_id = parent_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return " parent_id= " + parent_id + ", name= " + name ;
    }
    
}
