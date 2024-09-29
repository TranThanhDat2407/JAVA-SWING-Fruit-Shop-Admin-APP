
package com.raven.model;

import java.util.Date;

/**
 *
 * @author PC
 */
public class User_Payment_Method {
    private int id;
    private int user_id;
    private int payment_type_id;
    private String card_number;
    private String card_holder_name;
    private Date expiry_date;
    private String provider;
    private boolean is_default;

    public User_Payment_Method() {
    }

    public User_Payment_Method(int id, int user_id, int payment_type_id, String card_number, String card_holder_name, Date expiry_date, String provider, boolean is_default) {
        this.id = id;
        this.user_id = user_id;
        this.payment_type_id = payment_type_id;
        this.card_number = card_number;
        this.card_holder_name = card_holder_name;
        this.expiry_date = expiry_date;
        this.provider = provider;
        this.is_default = is_default;
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

    public int getPayment_type_id() {
        return payment_type_id;
    }

    public void setPayment_type_id(int payment_type_id) {
        this.payment_type_id = payment_type_id;
    }

    public String getCard_number() {
        return card_number;
    }

    public void setCard_number(String card_number) {
        this.card_number = card_number;
    }

    public String getCard_holder_name() {
        return card_holder_name;
    }

    public void setCard_holder_name(String card_holder_name) {
        this.card_holder_name = card_holder_name;
    }

    public Date getExpiry_date() {
        return expiry_date;
    }

    public void setExpiry_date(Date expiry_date) {
        this.expiry_date = expiry_date;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public boolean isIs_default() {
        return is_default;
    }

    public void setIs_default(boolean is_default) {
        this.is_default = is_default;
    }

    @Override
    public String toString() {
        return "User_Payment_Method{" + "id=" + id + ", user_id=" + user_id + ", payment_type_id=" + payment_type_id + ", card_number=" + card_number + ", card_holder_name=" + card_holder_name + ", expiry_date=" + expiry_date + ", provider=" + provider + ", is_default=" + is_default + '}';
    }
    
    
}
