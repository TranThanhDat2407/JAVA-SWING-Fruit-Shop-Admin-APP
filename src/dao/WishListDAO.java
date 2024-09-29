
package dao;


import com.raven.model.WishList;
import java.util.List;
import java.sql.ResultSet;
import java.util.ArrayList;
import utils.jdbcHelper;

/**
 *
 * @author PC
 */
public class WishListDAO extends EduSysDAO<WishList, Integer>{
    final String INSERT_SQL = "INSERT INTO Wishlist (user_id, product_item_id)\n" +
                              "VALUES (?,?);";
    final String UPDATE_SQL = "UPDATE Wishlist SET user_id = ?, product_item_id = ?  WHERE id = ?";
    final String DELETE_SQL = "DELETE FROM Wishlist where id = ?";
    final String SELECT_ALL_SQL = "SELECT * FROM Wishlist";
    final String SELECT_BY_ID_SQL = "SELECT * FROM Wishlist where id = ?";
    @Override
    public void insert(WishList entity) {
        jdbcHelper.update(INSERT_SQL, entity.getUser_id(), entity.getProduct_item_id());
    }

    @Override
    public void update(WishList entity) {
        jdbcHelper.update(UPDATE_SQL, entity.getUser_id(), entity.getProduct_item_id(), entity.getId());
    }

    @Override
    public void delete(Integer id) {
        jdbcHelper.update(DELETE_SQL, id);
    }

    @Override
    public List<WishList> selectAll() {
        return selectBySql(SELECT_ALL_SQL);
    }

    @Override
    public WishList selectById(Integer id) {
        List<WishList> list = selectBySql(SELECT_BY_ID_SQL, id);
        if(list.isEmpty())
        {
            return null;
        }
        return list.get(0);
    }

    @Override
    public List<WishList> selectBySql(String sql, Object... args) {
        List<WishList> list = new ArrayList<>();
        try {
            ResultSet rs = jdbcHelper.query(sql, args);
            try {
                while(rs.next())
                {
                    WishList wl = new WishList();
                    wl.setId(rs.getInt("id"));
                    wl.setUser_id(rs.getInt("user_id"));
                    wl.setProduct_item_id(rs.getInt("product_item_id"));
                    list.add(wl);
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
