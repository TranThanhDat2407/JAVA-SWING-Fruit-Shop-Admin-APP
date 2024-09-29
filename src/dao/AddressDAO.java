/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;


import com.raven.model.Address;
import java.util.List;
import utils.jdbcHelper;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author PC
 */
public class AddressDAO {

    final static String INSERT_SQL_CLIENT = "EXEC sp_CreateAddressClient @user_id = ?, @city = ?, @ward = ?, @street = ?";
    final static String INSERT_SQL_ADMIN = "EXEC sp_CreateAddressAdmin @user_id = ?, @city = ?, @ward = ?, @street = ?, @is_Default = ?";
    final static String UPDATE_SQL_CLIENT = "EXEC sp_UpdateAddressClient @address_id = ?, @city = ?, @ward = ?, @street = ?";
    final static String UPDATE_SQL_ADMIN = "EXEC sp_UpdateAddressAdmin @user_id = ?, @address_id = ?, @city = ?, @ward = ?, @street = ?, @is_default = ?";
    final static String DELETE_SQL = "EXEC sp_DeleteUserAddress @user_id = ?, @address_id = ?";
    
   private static final String SELECT_BY_USER_ID_SQL
        = "SELECT a.id, ua.user_id, u.name , a.city, a.ward, a.street, ua.is_Default"
        + " FROM User_Address ua"
        + " JOIN Address a ON ua.address_id = a.id "
        + " JOIN [User] u ON ua.user_id = u.id "
        + " WHERE ua.user_id = ?";
   
   private static final String SELECT_BY_ID_SQL
        = "SELECT a.id, ua.user_id, u.name, a.city, a.ward, a.street, ua.is_Default"
        + " FROM User_Address ua"
        + " JOIN Address a ON ua.address_id = a.id"
        + " JOIN [User] u ON ua.user_id = u.id"
        + " WHERE a.id = ?";

   private static final String SELECT_BY_USER_NAME_SQL
        = "SELECT a.id, ua.user_id, u.name , a.city, a.ward, a.street, ua.is_Default"
        + " FROM User_Address ua"
        + " JOIN Address a ON a.id = ua.address_id"
        + " JOIN [User] u ON u.id = ua.user_id"
        + " WHERE u.name LIKE ?"; 

private static final String SELECT_ALL
        = "SELECT \n"
        + "    a.id,\n"
        + "    ua.user_id,\n"
        + "    u.name,\n"
        + "    a.city, \n"
        + "    a.ward, \n"
        + "    a.street,\n"
        + "    a.ward, \n"
        + "    ua.is_Default\n"
       
        + "FROM User_Address ua\n"
        + "JOIN Address a ON ua.address_id = a.id\n"
        + "JOIN [User] u ON ua.user_id = u.id";

    public static void insertAdmin(int userId, String city, String ward, String street, boolean is_Default) {
        jdbcHelper.update(INSERT_SQL_ADMIN, userId, city, ward, street, is_Default);
    }
    
    public static void insertClient(int userId, String city, String ward, String street) {
        jdbcHelper.update(INSERT_SQL_CLIENT, userId, city, ward, street);
    }

    public static void updateAdmin(int userId, int address_id, String city, String ward, String street, boolean is_Default) {
        jdbcHelper.update(UPDATE_SQL_ADMIN, userId, address_id, city, ward, street, is_Default);
    }
    
     public static void updateClient( int address_id, String city, String ward, String street) {
        jdbcHelper.update(UPDATE_SQL_CLIENT, address_id, city, ward, street);
    }

    public static void delete(int user_id, int address_id) {
        jdbcHelper.update(DELETE_SQL, user_id, address_id);
    }

    public static List<Address> selectByUserId(int userId) {
        List<Address> addresses = new ArrayList<>();

        try (ResultSet rs = jdbcHelper.query(SELECT_BY_USER_ID_SQL, userId)) {
            while (rs.next()) {
                Address address = new Address();
                address.setId(rs.getInt("id"));
                address.setUser_id(rs.getInt("user_id"));
                address.setCity(rs.getString("city"));
                address.setWard(rs.getString("ward"));
                address.setStreet(rs.getString("street"));
                address.setIsDefault(rs.getBoolean("is_Default"));
                addresses.add(address);
            }
        } catch (SQLException ex) {
            Logger.getLogger(AddressDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return addresses;
    }
    
    public static Address selectById(int id) {
            Address address = new Address();

        try (ResultSet rs = jdbcHelper.query(SELECT_BY_ID_SQL, id)) {
            while (rs.next()) {
             
                address.setId(rs.getInt("id"));
                address.setUser_id(rs.getInt("user_id"));
                address.setCity(rs.getString("city"));
                address.setWard(rs.getString("ward"));
                address.setStreet(rs.getString("street"));
                address.setIsDefault(rs.getBoolean("is_Default"));
        
            }
        } catch (SQLException ex) {
            Logger.getLogger(AddressDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return address;
    }
    

     public static List<Object> getAllAddresses() {
        List<Object> addresses = new ArrayList<>();
        
       try (ResultSet rs = jdbcHelper.query(SELECT_ALL)){

            while (rs.next()) {
                Map<String, Object> address = new HashMap<>();
                address.put("id", rs.getInt("id"));
                address.put("user_id", rs.getInt("user_id"));
                address.put("name", rs.getString("name"));
                address.put("city", rs.getString("city"));
                address.put("ward", rs.getString("ward"));
                address.put("street", rs.getString("street"));
                address.put("is_Default", !rs.getBoolean("is_Default"));
                addresses.add(address);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return addresses;
    }
     
     public static List<Object> getAddressesByName(String name) {
        List<Object> addresses = new ArrayList<>();
        
       try (ResultSet rs = jdbcHelper.query(SELECT_BY_USER_NAME_SQL, "%"+name+"%")){

            while (rs.next()) {
                Map<String, Object> address = new HashMap<>();
                address.put("id", rs.getInt("id"));
                address.put("user_id", rs.getInt("user_id"));
                address.put("name", rs.getString("name"));
                address.put("city", rs.getString("city"));
                address.put("ward", rs.getString("ward"));
                address.put("street", rs.getString("street"));
                address.put("is_Default", !rs.getBoolean("is_Default"));
                addresses.add(address);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return addresses;
    }
     
     
     
   public static void main(String[] args) {
        AddressDAO addressDAO = new AddressDAO();
        List<Object> addresses = addressDAO.getAddressesByName("dat");

        // In ra kết quả
        for (Object obj : addresses) {
            if (obj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> address = (Map<String, Object>) obj;
                System.out.println("ID: " + address.get("id"));
                System.out.println("User ID: " + address.get("user_id"));
                System.out.println("City: " + address.get("city"));
                System.out.println("Ward: " + address.get("ward"));
                System.out.println("Street: " + address.get("street"));
                System.out.println("--------------------");
            }
        }
        
//        int userId = 12345; 
//        AddressDAO addressDAO = new AddressDAO();
//        List<Address> addresses = addressDAO.selectByUserId(userId);
//
//        System.out.println("Danh sách địa chỉ của người dùng có ID = " + userId + ":");
//        for (Address address : addresses) {
//            System.out.println("ID: " + address.getId());
//            System.out.println("Thành phố: " + address.getCity());
//            System.out.println("Quận/Huyện: " + address.getWard());
//            System.out.println("Đường: " + address.getStreet());
//            System.out.println("Mặc định: " + address.isIsDefault());
//            System.out.println("---");
//        }
    
    }

}
