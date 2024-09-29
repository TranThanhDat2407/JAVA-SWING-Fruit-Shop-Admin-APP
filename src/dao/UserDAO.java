
package dao;


import com.google.zxing.BinaryBitmap;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.raven.model.User;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import utils.jdbcHelper;


/**
 *
 * @author PC
 */
public class UserDAO extends EduSysDAO<User, String>{

    final String INSERT_SQL = "INSERT INTO [User] (name, password, phone, email,role, QR_IMG)\n" +
                              "VALUES (?, ?, ?, ?, ?,?);";
    
    final String UPDATE_SQL = "UPDATE [User] SET name = ?, password= ?, phone = ?, email = ?,role=?,QR_IMG=?  WHERE id = ?";
    final String UPDATE_OTP_SQL = "UPDATE [User] SET  one_time_password = ?, otp_requested_time = ?  WHERE id = ?";
//    final String DELETE_SQL = "DELETE FROM [User] where id = ?";
    final String DELETE_SQL = "EXEC DeleteUser @UserId = ?";
    final String SELECT_ALL_SQL = "SELECT * FROM [User]";
    final String SELECT_BY_ID_SQL = "SELECT * FROM [User] where id = ?";
    
    @Override
    public void insert(User entity) {
        jdbcHelper.update(INSERT_SQL,
                          entity.getName(),
                          entity.getPassword(),
                          entity.getPhone(),
                          entity.getEmail(),
                          entity.isRole(),
                          entity.getQR_IMG());
    }

    @Override
    public void update(User entity) {
        jdbcHelper.update(UPDATE_SQL,
                          entity.getName(),
                          entity.getPassword(),
                          entity.getPhone(),
                          entity.getEmail(),
                          entity.isRole(),
                          entity.getQR_IMG(),
                          entity.getId());    
    }
    
 
    public void updateOTP(User entity) {
        jdbcHelper.update(UPDATE_OTP_SQL,
                          entity.getOne_time_password(),
                          entity.getOtp_requested_time(),
                          entity.getId());    
    }

    @Override
    public void delete(String id) {
        jdbcHelper.update(DELETE_SQL, id);
    }

    @Override
    public List<User> selectAll() {
        return selectBySql(SELECT_ALL_SQL);
    }

    @Override
    public User selectById(String id) {
        List<User> list = selectBySql(SELECT_BY_ID_SQL, id);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);    
    }
    
    

    @Override
    public List<User> selectBySql(String sql, Object... args) {
        List<User> list = new ArrayList<>();
        try {
            ResultSet rs = jdbcHelper.query(sql, args);
            try {
                while (rs.next()) {
                    User entity = new User();
                    entity.setId(rs.getInt("id"));
                    entity.setPassword(rs.getString("password"));
                    entity.setName(rs.getString("name"));
                    entity.setEmail(rs.getString("email"));
                    entity.setPhone(rs.getString("phone"));
                    entity.setRole(rs.getBoolean("role"));
                    entity.setCreateAt(rs.getDate("create_at"));
                    entity.setUpdateAt(rs.getDate("update_at"));
                    entity.setOne_time_password(rs.getString("one_time_password"));
                    entity.setOtp_requested_time(rs.getTimestamp("otp_requested_time"));
                    entity.setQR_IMG(rs.getString("QR_IMG"));
                    list.add(entity);
                }
            } finally {
                rs.getStatement().getConnection().close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return list;    }
    
    public User selectByEmail(String Email) {
        String sql = "SELECT * FROM [User] WHERE email = ?";
        List<User> list = this.selectBySql(sql, Email);
        return list.size() > 0 ? list.get(0) : null;
    }    
    
    public static void main(String[] args) throws IOException, FileNotFoundException, NotFoundException {
        UserDAO a = new UserDAO();
        List<User> userList = a.selectAll();
        for(User u : userList){
            if(u.getQR_IMG() != null){
        System.out.println(u.getQR_IMG());
        String charset = "UTF-8";
	Map<EncodeHintType, ErrorCorrectionLevel> hashMap = new HashMap<>();
	hashMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

	String qrCodeData = readQR("D:\\FPT Polytechnic\\Ki4\\JAVA4\\Project-Fruit\\Project-Fruit\\src\\main\\webapp\\views\\QRCode\\Users\\"+u.getQR_IMG(), charset, hashMap);
        System.out.println(qrCodeData);
        }
        }
    }
    
     public static String readQR(String path, String charset,
             Map<EncodeHintType, ErrorCorrectionLevel> hashMap)
            		 throws FileNotFoundException, IOException, NotFoundException {
		 BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(
				 new BufferedImageLuminanceSource(
						 ImageIO.read(new FileInputStream(path)))));
		 Result result = new MultiFormatReader().decode(binaryBitmap);
		 return result.getText();
    }
     
     
    
}
