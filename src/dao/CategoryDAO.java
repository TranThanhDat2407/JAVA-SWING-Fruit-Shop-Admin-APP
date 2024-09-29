
package dao;


import com.raven.model.Category;
import java.util.ArrayList;
import java.util.List;
import utils.jdbcHelper;
import java.sql.ResultSet;

/**
 *
 * @author PC
 */
public class CategoryDAO extends EduSysDAO<Category, Integer>{
    final String INSERT_SQL = "INSERT INTO Category (parent_id,name)\n" +
                              "VALUES (?,?)";
    final String UPDATE_SQL = "UPDATE Category\n" +
                                "SET parent_id = ?, name = ?\n" +
                                "WHERE id = ?";
    final String DELETE_SQL = "DELETE FROM Category\n" +
                              "WHERE id = ?";
    final String SELECT_ALL_SQL = "SELECT * FROM Category";
    final String SELECT_BY_ID_SQL = "SELECT * FROM Category where id = ?";
    final String SELECT_BY_NAME_SQL = "SELECT * FROM Category where name like ?";
    @Override
    public void insert(Category entity) {
        jdbcHelper.update(INSERT_SQL, 
                            entity.getParent_id(),
                            entity.getName());
    }

    @Override
    public void update(Category entity) {
        jdbcHelper.update(UPDATE_SQL, 
                            entity.getParent_id(),
                            entity.getName(),
                            entity.getId());
    }

    @Override
    public void delete(Integer id) {
        jdbcHelper.update(DELETE_SQL, id);
    }

    @Override
   public List<Category> selectAll() {
        return selectBySql(SELECT_ALL_SQL);
    }

    @Override
    public Category selectById(Integer id) {
        List<Category> list = selectBySql(SELECT_BY_ID_SQL, id);
        if(list.isEmpty())
        {
            return null;
        }
        return list.get(0);
    }
  public List<Category> selectByName(String keyword){
        return this.selectBySql(SELECT_BY_NAME_SQL, "%"+keyword+"%");
    }
    @Override
    public List<Category> selectBySql(String sql, Object... args) {
        List<Category> list = new ArrayList<>();
        try {
            ResultSet rs = jdbcHelper.query(sql, args);
            try {
               while(rs.next())
            {
                Category entity = new Category();
                entity.setId(rs.getInt("id"));
                entity.setParent_id(rs.getInt("parent_id"));
                entity.setName(rs.getString("name"));
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
    
}
