
package dao;


import com.raven.model.User_Payment_Method;
import java.util.ArrayList;
import java.util.List;
import utils.jdbcHelper;
import java.sql.ResultSet;

/**
 *
 * @author PC
 */
public class User_Payment_MethodDAO extends EduSysDAO<User_Payment_Method, Integer>{
    final String INSERT_SQL = "INSERT INTO User_Payment_Method (user_id, payment_type_id, card_number, card_holder_name, expiry_date, provider, is_default)\n" +
                              "VALUES (?, ?, ?, ?, ?, ?, ?)";
    final String UPDATE_SQL = "UPDATE User_Payment_Method\n" +
                              "SET user_id = ?, payment_type_id = ?, card_number = ?, card_holder_name = ?, expiry_date = ?, provider = ?, is_default = ?\n" +
                              "WHERE id = ?";
    final String DELETE_SQL = "DELETE FROM User_Payment_Method\n" +
                              "WHERE id = ?";
    final String SELECT_ALL_SQL = "SELECT * FROM User_Payment_Method";
    final String SELECT_BY_ID_SQL = "SELECT * FROM User_Payment_Method where id = ?";
    final String SELECT_BY_USER_ID_SQL = "SELECT * FROM User_Payment_Method\n" +
                                         "WHERE user_id = ?;";
    @Override
    public void insert(User_Payment_Method entity) {
        jdbcHelper.update(INSERT_SQL,
                                entity.getUser_id(),
                                entity.getPayment_type_id(),
                                entity.getCard_number(),
                                entity.getCard_holder_name(),
                                entity.getExpiry_date(),
                                entity.getProvider(),
                                entity.isIs_default());
    }

    @Override
    public void update(User_Payment_Method entity) {
        jdbcHelper.update(UPDATE_SQL,
                                entity.getUser_id(),
                                entity.getPayment_type_id(),
                                entity.getCard_number(),
                                entity.getCard_holder_name(),
                                entity.getExpiry_date(),
                                entity.getProvider(),
                                entity.isIs_default(),
                                entity.getId());        
    }

    @Override
    public void delete(Integer id) {
        jdbcHelper.update(DELETE_SQL, id);
    }

    @Override
    public List<User_Payment_Method> selectAll() {
        return selectBySql(SELECT_ALL_SQL);
    }

    @Override
    public User_Payment_Method selectById(Integer id) {
        List<User_Payment_Method> list = selectBySql(SELECT_BY_ID_SQL, id);
        if(list.isEmpty())
        {
            return null;
        }
        return list.get(0);
    }

    @Override
    public List<User_Payment_Method> selectBySql(String sql, Object... args) {
        List<User_Payment_Method> list = new ArrayList<>();
        try {
            ResultSet rs = jdbcHelper.query(sql, args);
            try {
                while(rs.next())
                {
                    User_Payment_Method entity = new User_Payment_Method();
                    entity.setId(rs.getInt("id"));
                    entity.setUser_id(rs.getInt("user_id"));
                    entity.setPayment_type_id(rs.getInt("payment_type_id"));
                    entity.setCard_number(rs.getString("card_number"));
                    entity.setCard_holder_name(rs.getString("card_holder_name"));
                    entity.setExpiry_date(rs.getDate("expiry_date"));
                    entity.setProvider(rs.getString("provider"));
                    entity.setIs_default(rs.getBoolean("is_default"));
                    list.add(entity);
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
    
    public List<User_Payment_Method> selectByUserId(int id)
    {
        List<User_Payment_Method> list = selectBySql(SELECT_BY_USER_ID_SQL, id);
        if(list.isEmpty())
        {
            return null;
        }
        return list;
    }
}
