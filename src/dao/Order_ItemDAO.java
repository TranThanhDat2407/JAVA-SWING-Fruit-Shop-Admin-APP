/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import com.raven.model.Order_Item;
import java.util.ArrayList;
import java.util.List;
import utils.jdbcHelper;
import java.sql.ResultSet;

/**
 *
 * @author PC
 */
public class Order_ItemDAO extends EduSysDAO<Order_Item, Integer>{
    final String INSERT_SQL = "INSERT INTO Order_Item (order_id, product_item_id, qty, price)\n" +
                              "VALUES\n" +
                              "(?, ?, ?, ?)";
    final String UPDATE_SQL = "UPDATE Order_Item SET order_id= ?, product_item_id = ?, qty = ?, price = ?"
                            + "WHERE id = ?";
    final String DELETE_SQL = "DELETE FROM Order_Item\n" +
                              "WHERE id = ?;";
    final String SELECT_ALL_SQL = "SELECT * FROM Order_Item";
    final String SELECT_BY_ID_SQL = "SELECT * FROM Order_Item where id = ?";
    final String SELECT_BY_ORDER_ID = "SELECT oi.*\n" +
                                      "FROM Order_Item oi\n" +
                                      "JOIN [Order] o ON oi.order_id = o.id\n" +
                                      "WHERE o.id = ?;";
    @Override
    public void insert(Order_Item entity) {
        jdbcHelper.update(INSERT_SQL, 
                            entity.getOrder_id(),
                            entity.getProduct_item_id(),
                            entity.getQty(),
                            entity.getPrice());
    }

    @Override
    public void update(Order_Item entity) {
        jdbcHelper.update(UPDATE_SQL, 
                            entity.getOrder_id(),
                            entity.getProduct_item_id(),
                            entity.getQty(),
                            entity.getPrice(),
                            entity.getId());
    }

    @Override
    public void delete(Integer id) {
        jdbcHelper.update(DELETE_SQL, id);
    }

    @Override
    public List<Order_Item> selectAll() {
        return selectBySql(SELECT_ALL_SQL);
    }

    @Override
    public Order_Item selectById(Integer id) {
        List<Order_Item> list = selectBySql(SELECT_BY_ID_SQL, id);
        if(list.isEmpty())
        {
            return null;
        }
        return list.get(0);
    }

    @Override
    public List<Order_Item> selectBySql(String sql, Object... args) {
        List<Order_Item> list = new ArrayList<>();
        try {
            ResultSet rs = jdbcHelper.query(sql, args);
            try {
                while(rs.next())
                {
                    Order_Item oi = new Order_Item();
                    oi.setId(rs.getInt("id"));
                    oi.setOrder_id(rs.getInt("order_id"));
                    oi.setProduct_item_id(rs.getInt("product_item_id"));
                    oi.setQty(rs.getInt("qty"));
                    oi.setPrice(rs.getFloat("price"));
                    list.add(oi);
                }
            } finally {
                rs.getStatement().getConnection().close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return list;
    }
    
    public List<Order_Item> selecByOrderId(int order_id)
    {
        List<Order_Item> list = selectBySql(SELECT_BY_ORDER_ID, order_id);
        if(list.isEmpty())
        {
            return null;
        }
        return list;
    }
}
