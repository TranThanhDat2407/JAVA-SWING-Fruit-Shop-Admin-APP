
package dao;


import com.raven.model.Order;
import java.util.ArrayList;
import java.util.List;
import utils.jdbcHelper;
import java.sql.ResultSet;
/**
 *
 * @author PC
 */
public class OrderDAO extends EduSysDAO<Order, Integer>{
    final String INSERT_SQL = "EXEC sp_CreateOrder @user_id = ?, @user_payment_method_id = ?, @address_id = ?;";
    final String UPDATE_SQL = "EXEC sp_UpdateOrder @order_id = ?, @user_payment_method_id = ?, @shipping_address = ?, @order_status = ?";
    
    final String DELETE_SQL = "EXEC sp_DeleteOrder @order_id = ?;";
    final String SELECT_ALL_SQL = "SELECT * FROM [Order]";
    final String SELECT_BY_ID_SQL = "SELECT * FROM [Order] where id = ?";
    final String SELECT_BY_USER_ID = "SELECT *\n" +
                                     "FROM [Order]\n" +
                                     "WHERE user_id = ?;";
    public void insert(int userId, int upm, int address){
        jdbcHelper.update(INSERT_SQL, userId, upm, address);
   }

    
    public void update(int order_id, int upm, String shipping_address,boolean order_status) {
        jdbcHelper.update(UPDATE_SQL,order_id,upm,shipping_address,order_status);
    }


    @Override
    public void delete(Integer id) {
        jdbcHelper.update(DELETE_SQL, id);
    }

    @Override
    public List<Order> selectAll() {
         return selectBySql(SELECT_ALL_SQL);
    }

    @Override
    public Order selectById(Integer id) {
        List<Order> list = selectBySql(SELECT_BY_ID_SQL, id);
        if(list.isEmpty())
        {
            return null;
        }
        return list.get(0);
    }

    @Override
    public List<Order> selectBySql(String sql, Object... args) {
       List<Order> list = new ArrayList<>();
        try {
            ResultSet rs = jdbcHelper.query(sql, args);
            try {
                while(rs.next())
                {
                    Order o = new Order();
                    o.setId(rs.getInt("id"));
                    o.setUser_id(rs.getInt("user_id"));
                    o.setUser_payment_method_id(rs.getInt("user_payment_method_id"));
                    o.setShipping_address(rs.getString("shipping_address"));
                    o.setTotal_amount(rs.getFloat("total_amount"));
                    o.setOrder_status(rs.getBoolean("order_status"));
                    o.setCreate_at(rs.getDate("create_at"));
                    o.setUpdate_at(rs.getDate("update_at"));
                    list.add(o);
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
    
    public List<Order> selectByUserId(int user_id)
    {
        List<Order> list = selectBySql(SELECT_BY_USER_ID, user_id);
        if(list.isEmpty())
        {
            return null;
        }
        return list;
    }

    @Override
    public void insert(Order entity) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void update(Order entity) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
