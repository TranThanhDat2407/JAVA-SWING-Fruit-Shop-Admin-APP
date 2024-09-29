
package dao;


import com.raven.model.Review;
import java.util.ArrayList;
import java.util.List;
import utils.jdbcHelper;
import java.sql.ResultSet;
/**
 *
 * @author PC
 */
public class ReviewDAO extends EduSysDAO<Review, Integer>{

    final String INSERT_SQL = "INSERT INTO Review (ordered_product_id, user_id, rating_value, comment)\n" +
                              "VALUES (?, ?, ?, ?)";
    final String UPDATE_SQL = "UPDATE Review\n" +
                              "SET rating_value = ?, comment = ?, update_at = GETDATE()\n" +
                              "WHERE id = ?";
    final String DELETE_SQL = "DELETE FROM Review WHERE id = ?";
    final String SELECT_ALL_SQL = "SELECT * FROM Review";
    final String SELECT_BY_ID_SQL = "SELECT * FROM Review where id = ?";
    final String SELECT_BY_PRODUCT_ID_SQL = "SELECT r.id, r.ordered_product_id, r.user_id, r.rating_value, r.comment, r.update_at, r.create_at\n" +
                                            "FROM Review r\n" +
                                            "INNER JOIN Order_Item oi ON r.ordered_product_id = oi.id\n" +
                                            "WHERE oi.product_item_id = ?\n" +
                                            "ORDER BY r.create_at DESC";
    @Override
    public void insert(Review entity) {
        jdbcHelper.update(INSERT_SQL, 
                          entity.getOrdered_product_id(),
                          entity.getUser_id(),
                          entity.getRating_value(),
                          entity.getComment());
    }

    @Override
    public void update(Review entity) {
        jdbcHelper.update(UPDATE_SQL,
                          entity.getRating_value(),
                          entity.getComment(),
                          entity.getId());
    }

    @Override
    public void delete(Integer id) {
        jdbcHelper.update(DELETE_SQL, id);
    }

    @Override
    public List<Review> selectAll() {
        return selectBySql(SELECT_ALL_SQL);
    }

    @Override
    public Review selectById(Integer id) {
        List<Review> list = selectBySql(SELECT_BY_ID_SQL, id);
        if(list.isEmpty())
        {
            return null;
        }
        return list.get(0);
    }

    @Override
    public List<Review> selectBySql(String sql, Object... args) {
        List<Review> list = new ArrayList<>();
        try {
            ResultSet rs = jdbcHelper.query(sql, args);
            try {
                while(rs.next())
                {
                    Review rv = new Review();
                    rv.setId(rs.getInt("id"));
                    rv.setOrdered_product_id(rs.getInt("ordered_product_id"));
                    rv.setUser_id(rs.getInt("user_id"));
                    rv.setRating_value(rs.getInt("rating_value"));
                    rv.setComment(rs.getString("comment"));
                    rv.setCreate_at(rs.getDate("create_at"));
                    rv.setUpdate_at(rs.getDate("update_at"));
                    list.add(rv);
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
    
    public List<Review> selectByProductId(int product_item_id)
    {
        List<Review> list = selectBySql(SELECT_BY_PRODUCT_ID_SQL, product_item_id);
        if(list.isEmpty())
        {
            return null;
        }
        return list;
    }
}
