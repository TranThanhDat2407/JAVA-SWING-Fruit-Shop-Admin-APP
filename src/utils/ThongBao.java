package utils;

import java.awt.Component;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class ThongBao {
   
    public static void alert(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, 
                "Hệ thống quản lý đào tạo", JOptionPane.INFORMATION_MESSAGE);
    }
       
    public static boolean confirm(Component parent, String message) {
        int result = JOptionPane.showConfirmDialog(parent, message, 
                "Hệ thống quản lý đào tạo", 
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return result == JOptionPane.YES_OPTION;
    }
       
    public static String prompt(Component parent, String message) {
        return JOptionPane.showInputDialog(parent, message, 
                "Hệ thống quản lý đào tạo", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void error(SQLException e) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public static void error(String invalid_year_or_month_selected) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public static void showMessage(String export_to_Excel_successful) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public static void showErrorMessage(String string) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
