
package dao;


import com.raven.model.Product_Item;
import java.util.List;
import utils.jdbcHelper;
import java.sql.ResultSet;
import java.util.ArrayList;
/**
 *
 * @author PC
 */
public class Product_ItemDAO extends EduSysDAO<Product_Item, Integer>{
    
    final String INSERT_SQL = "EXEC sp_UpdateOrInsertProductItem @product_id = ?, @qty_in_stock = ?, @price = ?, @original_price = ?";
    final String UPDATE_SQL = "UPDATE Product_Item\n" +
                              "SET product_id = ?, qty_in_stock = ?, price = ?, original_price = ?, update_at = GETDATE()\n" +
                              "WHERE id = ?;";
    final String DELETE_SQL = "EXEC sp_DeleteProductItem @id = ?";
    final String SELECT_ALL_SQL = "SELECT * FROM Product_Item";
    final String SELECT_BY_ID_SQL = "SELECT * FROM Product_Item where id = ?";
    final String SELECT_BY_NAME_SQL = "SELECT * FROM Product_Item where name like ?";
    public void insert(int product_id, int qty_in_stock, double price, double original_price) {
        jdbcHelper.update(INSERT_SQL, product_id, qty_in_stock, price, original_price);
    }
    @Override
    public void insert(Product_Item entity) {
        jdbcHelper.update(INSERT_SQL, 
                                entity.getProduct_id(),
                                entity.getQty_in_stock(),
                                entity.getPrice(),
                                entity.getOriginal_price());
    }

    @Override
    public void update(Product_Item entity) {
        jdbcHelper.update(UPDATE_SQL, 
                                entity.getProduct_id(),
                                entity.getQty_in_stock(),
                                entity.getPrice(),
                                entity.getOriginal_price(),
                                entity.getId());
    }

    @Override
    public void delete(Integer id) {
        jdbcHelper.update(DELETE_SQL, id);
    }

    @Override
    public List<Product_Item> selectAll() {
        return selectBySql(SELECT_ALL_SQL);
    }

    @Override
    public Product_Item selectById(Integer id) {
        List<Product_Item> list = selectBySql(SELECT_BY_ID_SQL, id);
        if(list.isEmpty())
        {
            return null;
        }
        return list.get(0);
    }
 public List<Product_Item> selectByIDProduct_Items(int id){
        return this.selectBySql(SELECT_BY_ID_SQL, id);
    }
    @Override
    public List<Product_Item> selectBySql(String sql, Object... args) {
        List<Product_Item> list = new ArrayList<>();
        try {
            ResultSet rs = jdbcHelper.query(sql, args);
            try {
                while(rs.next())
                {
                    Product_Item entity = new Product_Item();
                    entity.setId(rs.getInt("id"));
                    entity.setProduct_id(rs.getInt("product_id"));
                    entity.setQty_in_stock(rs.getInt("qty_in_stock"));
                    entity.setPrice(rs.getFloat("price"));
                    entity.setOriginal_price(rs.getFloat("original_price"));
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
    
}
