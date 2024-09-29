
package com.raven.model;

/**
 *
 * @author PC
 */
public class Payment_Type {
    private int id;
    private String name;

    public Payment_Type() {
    }

    public Payment_Type(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Payment_Type{" + "id=" + id + ", name=" + name + '}';
    }
    
    
}
