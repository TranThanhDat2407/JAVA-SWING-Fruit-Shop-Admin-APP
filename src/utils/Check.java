package utils;

import com.raven.model.User;

public class Check {
   
//    Đối tượng này chứa thông tin người sử dụng sau khi đăng nhập
    
    public static User user = null;
   
//    Xóa thông tin của người sử dụng khi có yêu cầu đăng xuất
 
    public static void clear() {
        Check.user = null;
    }

//     Kiểm tra xem đăng nhập hay chưa
   
    public static boolean isLogin() {
        return Check.user != null;
    }
     
//      Kiểm tra xem có phải là trưởng phòng hay không
     
    public static boolean isManager() {
        return Check.isLogin() && user.isRole();
    }
}
