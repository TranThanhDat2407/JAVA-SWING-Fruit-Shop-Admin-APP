package com.raven.model;




/**
 *
 * @author PC
 */
public class Address {
    private int id;
    private int user_id;
    private String city;
    private String ward;
    private String street;
    private boolean isDefault;
    
    public Address() {
        
    }

    public Address(int id, int user_id, String city, String ward, String street, boolean isDefault) {
        this.id = id;
        this.user_id = user_id;
        this.city = city;
        this.ward = ward;
        this.street = street;
        this.isDefault = isDefault;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public boolean isIsDefault() {
        return isDefault;
    }

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    
    @Override
    public String toString() {
        return this.city + ", " + this.ward + ", " + this.street;
    }
      
    

   
    
    
}
