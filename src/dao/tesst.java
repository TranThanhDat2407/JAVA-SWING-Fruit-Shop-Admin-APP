
package dao;


import com.raven.model.User;
import java.util.List;

/**
 *
 * @author Admin
 */
public class tesst {
   
    public static void main(String[] args) {
       UserDAO dao = new UserDAO();
       List<User> list= dao.selectAll();
        for (User user : list) {
            System.out.println(user.toString());
        }
    }
            
}
