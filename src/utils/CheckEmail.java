package utils;

import com.raven.model.User;

public class CheckEmail {
   
//    Đối tượng này chứa thông tin người sử dụng sau khi đăng nhập
    
    public static User user = null;
   
//    Xóa thông tin của người sử dụng khi có yêu cầu đăng xuất
 
    public static void clear() {
        CheckEmail.user = null;
    }

//     Kiểm tra xem đăng nhập hay chưa
   
    public static boolean isLogin() {
        return CheckEmail.user != null;
    }
     
//      Kiểm tra xem có phải là trưởng phòng hay không
     
    public static boolean isManager() {
        return CheckEmail.isLogin() && user.isRole();
    }
}
