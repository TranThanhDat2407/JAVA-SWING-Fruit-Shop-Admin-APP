/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import utils.jdbcHelper;

/**
 *
 * @author THANHDAT
 */
public class ThongKeDAO {

    final static String GetBestSellProducts = "sp_GetBestSellProducts";
    final static String GetBestSellProductsByTime = "sp_GetBestSellProductsByTime @Year = ?, @Month = ?";
    
    final static String GetUsersCountByMonth = "sp_GetUsersCountByMonth";
    final static String GetUsersCount = "sp_GetUsersCount @Year = ?, @Month = ?";
    final static String GetUsers = "sp_GetUsersByTime @Year = ?, @Month = ?";
    
    final static String GetReneuveByMonth = "sp_GetRevenueByMonth";
    final static String GetReneuve = "sp_GetRevenue @Year = ?, @Month = ?";
    
    final static String GetOrderCountByMonth = "sp_GetOrdersCountByMonth";
    final static String GetOrderCount = "sp_GetOrdersCount @Year = ?, @Month = ?";
    final static String GetOrder = "sp_GetOrders @Year = ?, @Month = ?" ;
    
    public static List<Object> getBestSellProducts() {
        List<Object> bestProcs = new ArrayList<>();

        try (ResultSet rs = jdbcHelper.query(GetBestSellProducts)) {

            while (rs.next()) {
                Map<String, Object> bestProc = new HashMap<>();
                bestProc.put("id", rs.getInt("id"));
                bestProc.put("img", rs.getString("img"));
                bestProc.put("name", rs.getString("name"));
                bestProc.put("price", rs.getFloat("price"));
                bestProc.put("total_sold_quantity", rs.getFloat("total_sold_quantity"));
                bestProc.put("total_revenue", rs.getFloat("total_revenue"));
                bestProcs.add(bestProc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bestProcs;
    }
    
      public static List<Object> getBestSellProducts(int year, int month) {
        List<Object> bestProcs = new ArrayList<>();

        try (ResultSet rs = jdbcHelper.query(GetBestSellProductsByTime,year,month)) {

            while (rs.next()) {
                Map<String, Object> bestProc = new HashMap<>();
                bestProc.put("id", rs.getString("id"));
                bestProc.put("img", rs.getString("img"));
                bestProc.put("name", rs.getString("name"));
                bestProc.put("price", rs.getString("price"));
                bestProc.put("total_sold_quantity", rs.getString("total_sold_quantity"));
                bestProc.put("total_revenue", rs.getString("total_revenue"));
                bestProcs.add(bestProc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bestProcs;
    }

    public static int GetUsersCountByMonth() throws SQLException {
        int userCount = 0;
        ResultSet rs = jdbcHelper.query(GetUsersCountByMonth);
        if (rs.next()) {
            userCount = rs.getInt("total_users");
        }
        rs.getStatement().getConnection().close();
        return userCount;
    }
    
     public static int GetUserCount(int year, int month) throws SQLException {
        int userCount = 0;
        ResultSet rs = jdbcHelper.query(GetUsersCount,year,month);
        if (rs.next()) {
            userCount = rs.getInt("total_users");
        }
        rs.getStatement().getConnection().close();
        return userCount;
    }
     
       public static List<Object> getUsers(int year, int month) throws SQLException {
       List<Object> users = new ArrayList<>();

        try (ResultSet rs = jdbcHelper.query(GetUsers,year,month)) {

            while (rs.next()) {
                Map<String, Object> bestProc = new HashMap<>();
                bestProc.put("id", rs.getString("id"));
                bestProc.put("name", rs.getString("name"));
                bestProc.put("email", rs.getString("email"));
                bestProc.put("[Month_Year]", rs.getString("Month_Year"));
                users.add(bestProc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
    
    public static int GetOrdersCountByMonth() throws SQLException {
        int userCount = 0;
        ResultSet rs = jdbcHelper.query(GetOrderCountByMonth);
        if (rs.next()) {
            userCount = rs.getInt("total_orders");
        }
        rs.getStatement().getConnection().close();
        return userCount;
    }
    
     public static int GetOrderCount(int year, int month) throws SQLException {
        int userCount = 0;
        ResultSet rs = jdbcHelper.query(GetOrderCount,year,month);
        if (rs.next()) {
            userCount = rs.getInt("total_orders");
        }
        rs.getStatement().getConnection().close();
        return userCount;
    }
    
       public static List<Object> GetOrders(int year, int month) throws SQLException {
       List<Object> doanhthu = new ArrayList<>();

        try (ResultSet rs = jdbcHelper.query(GetOrder,year,month)) {

            while (rs.next()) {
                Map<String, Object> bestProc = new HashMap<>();
                bestProc.put("OrderID", rs.getString("id"));
                bestProc.put("UserID", rs.getString("id"));
                bestProc.put("UserName", rs.getString("name"));
                bestProc.put("Email", rs.getString("email"));
                bestProc.put("Total", rs.getString("total_amount"));
                bestProc.put("Date", rs.getString("Month_Year"));
                doanhthu.add(bestProc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return doanhthu;
    }
    
    public static int GetReneuveByMonth() throws SQLException {
        int userCount = 0;
        ResultSet rs = jdbcHelper.query(GetReneuveByMonth);
        if (rs.next()) {
            userCount = rs.getInt("total_Revenue");
        }
        rs.getStatement().getConnection().close();
        return userCount;
    }
    
      public static List<Object> GetReneuve(int year, int month) throws SQLException {
       List<Object> doanhthu = new ArrayList<>();

        try (ResultSet rs = jdbcHelper.query(GetReneuve,year,month)) {

            while (rs.next()) {
                Map<String, Object> bestProc = new HashMap<>();
                bestProc.put("[Month_Year]", rs.getString("Month_Year"));
                bestProc.put("total_Revenue", rs.getString("total_Revenue"));
                doanhthu.add(bestProc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return doanhthu;
    }

      public static void main(String[] args) throws SQLException {
//        List<Object> test = ThongKeDAO.getUsers(2024, 7);
//        for(Object a: test){
//             System.out.println(a);
//        }
        
        int a = ThongKeDAO.GetUserCount(2024, 7);
        System.out.println(a);
    }
}
