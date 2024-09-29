
package dao;


import com.raven.model.Payment_Type;
import java.util.List;
import utils.jdbcHelper;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 *
 * @author PC
 */
public class Payment_TypeDAO extends EduSysDAO<Payment_Type, Integer>{
    final String INSERT_SQL = "INSERT INTO Payment_Type (name)\n" +
                              "VALUES (?);";
    final String UPDATE_SQL = "UPDATE Payment_Type SET name = ?  WHERE id = ?";
    final String DELETE_SQL = "DELETE FROM Payment_Type where id = ?";
    final String SELECT_ALL_SQL = "SELECT * FROM Payment_Type";
    final String SELECT_BY_ID_SQL = "SELECT * FROM Payment_Type where id = ?";
    @Override
    public void insert(Payment_Type entity) {
        jdbcHelper.update(INSERT_SQL, entity.getName());
    }

    @Override
    public void update(Payment_Type entity) {
        jdbcHelper.update(UPDATE_SQL,
                            entity.getName(),
                            entity.getId());
    }

    @Override
    public void delete(Integer id) {
        jdbcHelper.update(DELETE_SQL, id);
    }

    @Override
    public List<Payment_Type> selectAll() {
        return selectBySql(SELECT_ALL_SQL);
    }

    @Override
    public Payment_Type selectById(Integer id) {
        List<Payment_Type> list = selectBySql(SELECT_BY_ID_SQL, id);
        if(list.isEmpty())
        {
            return null;
        }
        return list.get(0);
    }

    @Override
    public List<Payment_Type> selectBySql(String sql, Object... args) {
        List<Payment_Type> list = new ArrayList<>();
        try {
            ResultSet rs = jdbcHelper.query(sql, args);
            try {
                while(rs.next())
                {
                    Payment_Type pt = new Payment_Type();
                    pt.setId(rs.getInt("id"));
                    pt.setName(rs.getString("name"));
                    list.add(pt);
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
