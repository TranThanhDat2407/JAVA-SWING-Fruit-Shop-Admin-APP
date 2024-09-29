
package dao;


import com.raven.model.Product;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import utils.jdbcHelper;

/**
 *
 * @author PC
 */
public class ProductDAO extends EduSysDAO<Product, Integer>{
    final String INSERT_SQL = "INSERT INTO Product (category_id, name, description)\n"
                            + "VALUES (?, ?, ?)";
    final String UPDATE_SQL = "UPDATE Product\n" +
                              "SET name = ?, description = ?,update_at = GETDATE()\n" +
                              "WHERE id = ?;";
    final String DELETE_SQL = "DELETE FROM Product\n" +
                              "WHERE id = ?;";
    final String SELECT_ALL_SQL = "SELECT * FROM Product";
    final String SELECT_BY_ID_SQL = "SELECT * FROM Product where id = ?";
    final String SELECT_BY_NAME_SQL = "SELECT * FROM Product where name like ?";
    final String SELECT_BY_PRODUCT_ITEM_ID_SQL = "SELECT * FROM Product where id = ?";
    @Override
    public void insert(Product entity) {
        jdbcHelper.update(INSERT_SQL, 
                            entity.getCategory_id(),
                            entity.getName(),
                            entity.getDescription());
    }
public Product selectByProductItemId(Integer Product_item_id)
    {
        List<Product> list = selectBySql(SELECT_BY_PRODUCT_ITEM_ID_SQL, Product_item_id);
        if(list.isEmpty())
        {
            return null;
        }
        return list.get(0);
    }
    
    @Override
    public void update(Product entity) {
        jdbcHelper.update(UPDATE_SQL,
                            entity.getName(),
                            entity.getDescription(),
                            entity.getId());
    }

    @Override
    public void delete(Integer id) {
        jdbcHelper.update(DELETE_SQL, id);
    }

    @Override
    public List<Product> selectAll() {
        return selectBySql(SELECT_ALL_SQL);
    }

    @Override
    public Product selectById(Integer id) {
        List<Product> list = selectBySql(SELECT_BY_ID_SQL, id);
        if(list.isEmpty())
        {
            return null;
        }
        return list.get(0);
    }

     public List<Product> selectByName(String keyword){
        
        return this.selectBySql(SELECT_BY_NAME_SQL, "%"+keyword+"%");
    }
    
  
 

    
    @Override
    public List<Product> selectBySql(String sql, Object... args) {
        List<Product> list = new ArrayList<>();
        try {
            ResultSet rs = jdbcHelper.query(sql, args);
            try {
                while(rs.next())
                {
                    Product entity = new Product();
                    entity.setId(rs.getInt("id"));
                    entity.setCategory_id(rs.getInt("category_id"));
                    entity.setName(rs.getString("name"));
                    entity.setDescription(rs.getString("description"));
                    entity.setCreate_at(rs.getDate("create_at"));
                    entity.setUpdate_at(rs.getDate("update_at"));
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
 
    public static void main(String[] args) {
        ProductDAO dao = new ProductDAO();
        Product list = dao.selectByProductItemId(1);
        System.out.println(list);      
    }
}
