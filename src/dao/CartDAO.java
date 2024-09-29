/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;


import com.raven.model.Cart;
import java.util.ArrayList;
import java.util.List;
import utils.jdbcHelper;
import java.sql.ResultSet;
/**
 *
 * @author PC
 */
public class CartDAO extends EduSysDAO<Cart, Integer>{
    final String INSERT_SQL = "INSERT INTO Cart (user_id, product_item_id, qty)\n" +
                              "VALUES (?, ?, ?)";
    final String UPDATE_SQL = "UPDATE Cart SET user_id = ?, product_item_id= ?, qty = ? WHERE id = ?";
    final String DELETE_SQL = "DELETE FROM Cart where id = ?";
    final String SELECT_ALL_SQL = "SELECT * FROM Cart";
    final String SELECT_BY_ID_SQL = "SELECT * FROM Cart where id = ?";
    @Override
    public void insert(Cart entity) {
        jdbcHelper.update(INSERT_SQL, 
                            entity.getUser_id(),
                            entity.getProduct_item_id(),
                            entity.getQty());
    }

    @Override
    public void update(Cart entity) {
        jdbcHelper.update(UPDATE_SQL,
                            entity.getUser_id(),
                            entity.getProduct_item_id(),
                            entity.getQty(),
                            entity.getId());
    }

    @Override
    public void delete(Integer id) {
        jdbcHelper.update(DELETE_SQL, id);
    }

    @Override
    public List<Cart> selectAll() {
        return selectBySql(SELECT_ALL_SQL);
    }

    @Override
    public Cart selectById(Integer id) {
        List<Cart> list = selectBySql(SELECT_BY_ID_SQL, id);
        if(list.isEmpty())
        {
            return null;
        }
        return list.get(0);
    }

    @Override
    public List<Cart> selectBySql(String sql, Object... args) {
        List<Cart> list = new ArrayList<>();
        try {
            ResultSet rs = jdbcHelper.query(sql, args);
            try {
                while(rs.next())
                {
                    Cart c = new Cart();
                    c.setId(rs.getInt("id"));
                    c.setUser_id(rs.getInt("user_id"));
                    c.setProduct_item_id(rs.getInt("product_item_id"));
                    c.setQty(rs.getInt("qty"));
                    list.add(c);
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
    
}
