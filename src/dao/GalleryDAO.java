/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;


import com.raven.model.Gallery;
import java.util.ArrayList;
import java.util.List;
import utils.jdbcHelper;
import java.sql.ResultSet;

/**
 *
 * @author PC
 */
public class GalleryDAO extends EduSysDAO<Gallery, Integer>{
    
    final String INSERT_SQL = "INSERT INTO Gallery (product_id, thumbnail)\n" +
                              "VALUES (?, ?)";
    final String UPDATE_SQL = "UPDATE Gallery\n" +
                              "SET product_id = ?, thumbnail = ?\n" +
                              "WHERE id = ?";
    final String DELETE_SQL = "DELETE FROM Gallery\n" +
                              "WHERE id = ?";
    final String SELECT_ALL_SQL = "SELECT * FROM Gallery";
    final String SELECT_BY_ID_SQL = "SELECT * FROM Gallery where id = ?";
    @Override
    public void insert(Gallery entity) {
        jdbcHelper.update(INSERT_SQL, 
                                entity.getProduct_id(),
                                entity.getThumbnail());
    }

    @Override
    public void update(Gallery entity) {
        jdbcHelper.update(UPDATE_SQL, 
                            entity.getProduct_id(),
                            entity.getThumbnail(),
                            entity.getId());
    }

    @Override
    public void delete(Integer id) {
        jdbcHelper.update(DELETE_SQL, id);
    }

    @Override
    public List<Gallery> selectAll() {
        return selectBySql(SELECT_ALL_SQL);
    }

    @Override
    public Gallery selectById(Integer id) {
        List<Gallery> list = selectBySql(SELECT_BY_ID_SQL, id);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }
 public List<Gallery> selectByIDGallerys(int keyword){
        return this.selectBySql(SELECT_BY_ID_SQL, keyword);
    }
    @Override
    public List<Gallery> selectBySql(String sql, Object... args) {
        List<Gallery> list = new ArrayList<>();
        try {
            ResultSet rs = jdbcHelper.query(sql, args);
            try {
                while(rs.next())
                {
                    Gallery entity = new Gallery();
                    entity.setId(rs.getInt("id"));
                    entity.setProduct_id(rs.getInt("product_id"));
                    entity.setThumbnail(rs.getString("thumbnail"));
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
    
    public List<Gallery> selectByProductId(String product_id)
    {
        String sql = "SELECT thumbnail FROM Gallery\n" +
                     "WHERE product_id = ?";
        List<Gallery> list = this.selectBySql(sql, product_id);
        return list.size() > 0 ? list : null;
    }
    
}
