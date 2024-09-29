
package com.raven.model;

import dao.UserDAO;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;



/**
 *
 * @author PC
 */
public class User {
    private int id;
    private String password;
    private String name;
    private String phone;
    private String email;
    private boolean role;
    private Date createAt;
    private Date updateAt;
    private String one_time_password;
    private Date otp_requested_time;
    private String QR_IMG;

private static final long OTP_VALID_DURATION = 1 * 60 * 1000; 

public boolean isOTPRequired() {
    if (this.getOne_time_password() == null) {
        return false;
    }

    long currentTimeInMillis = System.currentTimeMillis();
    long otpRequestedTimeInMillis = this.otp_requested_time.getTime();
    
    System.out.println(currentTimeInMillis);
    System.out.println(otpRequestedTimeInMillis + OTP_VALID_DURATION);
    if (otpRequestedTimeInMillis + OTP_VALID_DURATION < currentTimeInMillis) {
        return false;
    }

    return true;
}

    public static void main(String[] args) {
        UserDAO dao = new UserDAO();
        User u = dao.selectByEmail("datttps37451@fpt.edu.vn");

        Boolean a = u.isOTPRequired();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        System.out.println(sdf.format(u.getOtp_requested_time()));

        System.out.println(a);
    }
    
	
    
    public String getOne_time_password() {
        return one_time_password;
    }

    public void setOne_time_password(String one_time_password) {
        this.one_time_password = one_time_password;
    }

    public Date getOtp_requested_time() {
        return otp_requested_time;
    }

    public void setOtp_requested_time(Date otp_requested_time) {
        this.otp_requested_time = otp_requested_time;
    }

    public String getQR_IMG() {
        return QR_IMG;
    }

    public void setQR_IMG(String QR_IMG) {
        this.QR_IMG = QR_IMG;
    }

    public User(int id, String password, String name, String phone, String email, boolean role, Date createAt, Date updateAt, String one_time_password, Date otp_requested_time, String QR_IMG) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.role = role;
        this.createAt = createAt;
        this.updateAt = updateAt;
        this.one_time_password = one_time_password;
        this.otp_requested_time = otp_requested_time;
        this.QR_IMG = QR_IMG;
    }
    
    
    
    public User(int id, String password, String name, String phone, String email, boolean role, Date createAt, Date updateAt) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.role = role;
        this.createAt = createAt;
        this.updateAt = updateAt;
    }

    public User() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isRole() {
        return role;
    }

    public void setRole(boolean role) {
        this.role = role;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public Date getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }

//    @Override
//    public String toString() {
//        return "User{" + "id=" + id + ", password=" + password + ", name=" + name + ", phone=" + phone + ", email=" + email + ", role=" + role + ", createAt=" + createAt + ", updateAt=" + updateAt + '}';
//    }
    @Override
public String toString() {
    return "ID: " + id + ", Name: " + name;
}

}
