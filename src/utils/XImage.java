package utils;

import java.awt.Image;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javax.swing.Icon;
import javax.swing.ImageIcon;

public class XImage {
    /**
     * Ảnh biểu tượng của ứng dụng, xuất hiện trên mọi cửa sổ
     */
//   public static Image getAppIcon() {
//    String file = "/Hinh/fpt.png";
//    return new ImageIcon(XImage.class.getResource(file)).getImage();
//}
    
    /**
     * Sao chép file logo chuyên đề vào thư mục logo
     * @param src là đối tượng file ảnh
     */   
    public static void save(File src){
        File dst = new File("logos", src.getName());
        if(!dst.getParentFile().exists()){
            dst.getParentFile().mkdirs();
        }
        try {
            Path from = Paths.get(src.getAbsolutePath());
            Path to = Paths.get(dst.getAbsolutePath());
            Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);
        } 
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    /**
     * Đọc hình ảnh logo chuyên đề
     * @param fileName  là tên file logo
     * @return ảnh đọc được
     */   
    public static ImageIcon readProductImg(String fileName){
        File path = new File("D:\\FPT Polytechnic\\Ki4\\DuAn1\\admin\\java-ui-dashboard-001\\part 3\\ui-dashboard-001\\src\\com\\raven\\imgs\\products", fileName);
        return new ImageIcon(new ImageIcon(path.getAbsolutePath()).getImage().getScaledInstance(364, 289, Image.SCALE_DEFAULT));
    }
    
      public static ImageIcon readQR(String fileName){
        File path = new File("D:\\FPT Polytechnic\\Ki4\\DuAn1\\admin\\java-ui-dashboard-001\\part 3\\ui-dashboard-001\\src\\com\\raven\\QRCode\\Users", fileName);
        return new ImageIcon(new ImageIcon(path.getAbsolutePath()).getImage().getScaledInstance(180, 180, Image.SCALE_DEFAULT));
    }

   
}