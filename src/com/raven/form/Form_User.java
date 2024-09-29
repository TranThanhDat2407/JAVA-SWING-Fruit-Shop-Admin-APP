/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.raven.form;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.raven.model.Address;
import com.raven.model.StatusType;
import com.raven.model.User;
import dao.AddressDAO;
import dao.UserDAO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.table.DefaultTableModel;
import utils.Encrypt;
import utils.ThongBao;
import utils.XDate;
import utils.XImage;

public class Form_User extends javax.swing.JPanel {

    /**
     * Creates new form Form_1
     */
    public Form_User() {
        initComponents();
        init();
    }
    UserDAO dao = new UserDAO();
    AddressDAO addressDAO = new AddressDAO();
    int index = -1;
    int no = 1;
   
    DefaultTableModel modelUser;

    void init() {
        fillTableAddress();
        fillTable();
        loadUserList();
    }

    void fillTable() {
        DefaultTableModel modelUser = (DefaultTableModel) tblUser.getModel();
        modelUser.setRowCount(0);
        try {
            List<User> list = dao.selectAll();
            for (User u : list) {
                Object[] row = {
                    no,
                    u.getId(),   
                    u.getPassword().replaceAll(".", "*"),
                    u.getName(),
                    u.getPhone(),
                    u.getEmail(),
                    u.isRole() ? "Admin" : "User",
                    XDate.toString(u.getCreateAt(), "dd/MM/yyyy"),
                    XDate.toString(u.getUpdateAt(), "dd/MM/yyyy"),
                    u.getQR_IMG(),
                };
                modelUser.addRow(row);
                no++;
            }
        } catch (Exception e) {
            ThongBao.alert(this, "Lỗi truy vấn dữ liệu!");
        }
    }

    User getFormUser() {
        User u = new User();

        u.setId(Integer.parseInt(txtId.getText()));
        u.setName(txtName.getText());
        u.setPassword(new String(txtPassword.getPassword()));
        u.setPassword(new String(txtRePas.getPassword()));
        u.setEmail(txtEmail.getText());
        u.setPhone(txtPhone.getText());
        u.setRole(rdoUser.isSelected());
//        u.setCreateAt(new Date());
        u.setUpdateAt(new Date());
        u.setQR_IMG(lblQR_IMG.getToolTipText());
        return u;
    }

    void setFormUser(User u) {
        txtId.setText(String.valueOf(u.getId()));
        txtName.setText(u.getName());
        txtPassword.setText(u.getPassword());
        txtRePas.setText(u.getPassword());
        txtEmail.setText(u.getEmail());
        txtPhone.setText(u.getPhone());
         
        String imagePath = u.getQR_IMG();
        if (imagePath != null && !imagePath.equals("")) {
           lblQR_IMG.setToolTipText(imagePath);
           lblQR_IMG.setIcon(XImage.readQR(imagePath));
        } else {
            lblQR_IMG.setToolTipText("");
        }
        
        if (u.isRole()) {
            rdoAdmin.setSelected(true);
        } else {
            rdoUser.setSelected(true);
        }

    }

    void insertUser() {
        User u = this.getFormUser();
        if (isValidate()) {
            String mk2 = new String(txtPassword.getPassword());
            if (!mk2.equals(u.getPassword())) {
                ThongBao.alert(this, "Xác nhận mật khẩu không đúng!");
            } else {
                try {
                    String qrCodeFileName = createUserQRImage(u,new String(txtPassword.getPassword()));
	            u.setQR_IMG(qrCodeFileName);
                    System.out.println(u.getQR_IMG());
                    u.setPassword(Encrypt.hashPassword(mk2));
                    dao.insert(u);
                    this.fillTable();
                    this.clearForm();
                    ThongBao.alert(this, "Thêm mới thành công!");
                } catch (Exception e) {
                    ThongBao.alert(this, "Thêm mới thất bại!");
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    void updateUser() {
        User u = this.getFormUser();
        if (isValidate()) {
            String mk2 = new String(txtPassword.getPassword());
            if (!mk2.equals(u.getPassword())) {
                ThongBao.alert(this, "Xác nhận mật khẩu không đúng!");
            } else {
                try {
                    String qrCodeFileName = createUserQRImage(u,new String(txtPassword.getPassword()));
                    u.setPassword(Encrypt.hashPassword(mk2));
	            u.setQR_IMG(qrCodeFileName);
                    System.out.println(mk2);
                    System.out.println(u);
                    dao.update(u);
                    this.fillTable();
                    this.clearForm();
                    ThongBao.alert(this, "Cập nhật thành công!");
                } catch (Exception e) {
                    ThongBao.alert(this, "Cập nhật thất bại!");
                }
            }
        }
    }

    void deleteUser() {
        String manv = txtId.getText();
        ThongBao.confirm(this, "Bạn thực sự muốn xóa nhân viên này?");
        try {
            dao.delete(manv);
            this.fillTable();
            ThongBao.alert(this, "Xóa thành công!");
            this.clearForm();
        } catch (Exception e) {
            ThongBao.alert(this, "Xóa thất bại!");
        }

    }

    void clearForm() {
        txtId.setText("");
        txtName.setText("");
        txtPassword.setText("");
        txtEmail.setText("");
        txtPhone.setText("");
        lblQR_IMG.setIcon(null); 
        lblQR_IMG.setToolTipText(""); 
        User u = new User();
        this.setFormUser(u);
        this.index = -1;

    }

    void edit() {
        String id = tblUser.getValueAt(index, 1).toString();
        User u = dao.selectById(id);
        this.setFormUser(u);
        TabUser.setSelectedIndex(0);

    }

    boolean isValidate() {
        User u = getFormUser();

        // Validate Name
        if (u.getName().length() == 0) {
            ThongBao.alert(this, "Tên không được để trống!");
            return false;
        }

        // Validate Email
        if (u.getEmail().length() == 0) {
            ThongBao.alert(this, "Email không được để trống!");
            return false;
        } else if (!isValidEmail(u.getEmail().trim())) {
            ThongBao.alert(this, "Email không hợp lệ!");
            return false;
        }

        // Validate Phone
        if (u.getPhone().length() == 0) {
            ThongBao.alert(this, "Số điện thoại không được để trống!");
            return false;

//        } else if (!isValidPhone(u.getPhone().trim())) {
//            ThongBao.alert(this, "Số điện thoại không hợp lệ!");
//            return false;
//    }
        // Validate Password
//        if (u.getPassword().length() == 0) {
//            ThongBao.alert(this, "Mật khẩu không được để trống!");
//            return false;
//        }
//        if (!txtRePas.getPassword().equals(txtPassword.getPassword())) {
//            ThongBao.alert(this, "Mật khẩu không trùng!");
//            return false;
     }
        return true;
    }

    private boolean isValidEmail(String email) {

        return email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }

//    private boolean isValidPhone(String phone) {
//
//        return phone.matches("^\\+?\\d{1,3}[- ]?\\d{10,10}$");
//    }

    /*
    -------------------------Address From---------------------------------------------
     */
    
    int indexAddress = -1;
    void fillTableAddress() {
        DefaultTableModel modelAddress = (DefaultTableModel) tblAddress_table.getModel();
        modelAddress.setRowCount(0);
        try {
            List<Object> list = AddressDAO.getAllAddresses();
            int no = 1;
            for (Object obj : list) {
                if (obj instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> u = (Map<String, Object>) obj;
                    Object[] row = {
                        no,
                        u.get("id"),
                        u.get("user_id"),
                        u.get("name"),
                        u.get("city"),
                        u.get("ward"),
                        u.get("street"),
                        u.get("is_Default"),
                    };
                    modelAddress.addRow(row);
                    no++;
                }
            }
        } catch (Exception e) {
            ThongBao.alert(this, "Lỗi truy vấn dữ liệu!");
            e.printStackTrace(); 
        }
    }
    
     void fillTableAddress(String name) {
        DefaultTableModel modelAddress = (DefaultTableModel) tblAddress_table.getModel();
        modelAddress.setRowCount(0);
        try {
            List<Object> list = AddressDAO.getAddressesByName(name);
            int no = 1;
            for (Object obj : list) {
                if (obj instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> u = (Map<String, Object>) obj;
                    Object[] row = {
                        no,
                        u.get("id"),
                        u.get("user_id"),
                        u.get("name"),
                        u.get("city"),
                        u.get("ward"),
                        u.get("street"),
                        u.get("is_Default"),
                    };
                    modelAddress.addRow(row);
                    no++;
                }
            }
        } catch (Exception e) {
            ThongBao.alert(this, "Lỗi truy vấn dữ liệu!");
            e.printStackTrace(); 
        }
    }
    
    Address getFormAddress() {
    Address ad = new Address();
    ad.setId(Integer.parseInt(txtID.getText()));

    User selectedUser = (User) cboUserList.getModel().getSelectedItem();
    System.out.println(selectedUser.getId());
  
        ad.setUser_id(selectedUser.getId());
        ad.setCity(txtCity.getText());
        ad.setWard(txtWard.getText());
        ad.setStreet(txtStreet.getText());
        ad.setIsDefault(rdoIs_Default.isSelected());

    System.out.println(ad);
    return ad;
}

    void setFormAddress(Address ad) {
    txtID.setText(String.valueOf(ad.getId()));
    User user = dao.selectById(String.valueOf(ad.getUser_id()));
    System.out.println(user);
    if (user != null) {
       cboUserList.getModel().setSelectedItem(user);
    } else {
        cboUserList.setSelectedIndex(-1);
    }
    txtCity.setText(ad.getCity());
    txtWard.setText(ad.getWard());
    txtStreet.setText(ad.getStreet());
    rdoIs_Default.setSelected(!ad.isIsDefault());
    rdoNotDefault.setSelected(ad.isIsDefault());
}

    void loadUserList() {
        DefaultComboBoxModel model = (DefaultComboBoxModel) cboUserList.getModel();
        model.removeAllElements();
        List<User> users = dao.selectAll();
    for (User user : users) {
        if (user != null) {
            model.addElement(user);
            }
        }
    }
    
    void editAddress() {
        String addressId = tblAddress_table.getValueAt(indexAddress, 1).toString();
        Address address = AddressDAO.selectById(Integer.parseInt(addressId));
        this.setFormAddress(address);
        TabUser.setSelectedIndex(2);
    }
    
    void insertAddress() {
        Address ad = this.getFormAddress();
        if (isValidateAddress()) {
                try {
                    AddressDAO.insertAdmin(ad.getUser_id(),
                            ad.getCity(), ad.getWard(), ad.getStreet(),ad.isIsDefault());
                    this.fillTable();
                    this.clearFormAddress();
                    ThongBao.alert(this, "Thêm mới thành công!");
                } catch (Exception e) {
                    ThongBao.alert(this, "Thêm mới thất bại!");
                    System.out.println(e.getMessage());
                }
            }
        }
    

    void updateAddressAdmin() {
        Address ad = this.getFormAddress();
        if (isValidateAddress()) {
                try {
                    AddressDAO.updateAdmin(ad.getUser_id(),ad.getId(),
                            ad.getCity(), ad.getWard(), ad.getStreet(),ad.isIsDefault());
                    this.fillTable();
                    this.clearFormAddress();
                    ThongBao.alert(this, "Cập nhật thành công!");
                } catch (Exception e) {
                    ThongBao.alert(this, "Cập nhật thất bại!");
                }
            }
        }
    

    void deleteAddress() {
        Address ad = this.getFormAddress();
        
        try {
            addressDAO.delete(ad.getId(),ad.getUser_id());
            ThongBao.confirm(this, "Bạn thực sự muốn xóa địa chỉ này?");
            this.fillTable();
            ThongBao.alert(this, "Xóa thành công!");
           this.clearFormAddress();
        } catch (Exception e) {
            ThongBao.alert(this, "Xóa thất bại!");
        }

    }

    void clearFormAddress() {
       txtID.setText("");
       cboUserList.setSelectedIndex(0);
       txtCity.setText("");
       txtWard.setText("");
       txtStreet.setText("");

        Address ad = new Address();
        this.setFormAddress(ad);
        this.indexAddress = -1;

    }
    boolean isValidateAddress() {
    Address ad = getFormAddress();

    if (ad.getCity().length() ==0) {
        ThongBao.alert(this, "Thành phố không được để trống!");
        return false;
    }
    if (ad.getWard().length() ==0) {
        ThongBao.alert(this, "Phường/Xã không được để trống!");
        return false;
    }
    if (ad.getStreet().length() ==0) {
        ThongBao.alert(this, "Đường không được để trống!");
        return false;
    }

    return true;
}

    public void findAddressbyKeyword() {
        String keyword = txtFindAddress.getText();
        fillTableAddress(keyword);
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jPanel2 = new javax.swing.JPanel();
        TabUser = new javax.swing.JTabbedPane();
        pnUserForm = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        btnCreate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        txtId = new javax.swing.JTextField();
        txtName = new javax.swing.JTextField();
        txtEmail = new javax.swing.JTextField();
        txtPhone = new javax.swing.JTextField();
        txtPassword = new javax.swing.JPasswordField();
        txtRePas = new javax.swing.JPasswordField();
        rdoUser = new javax.swing.JRadioButton();
        rdoAdmin = new javax.swing.JRadioButton();
        btnUpdate1 = new javax.swing.JButton();
        lblQR_IMG = new javax.swing.JLabel();
        pnUserTable = new javax.swing.JPanel();
        panelBorder2 = new com.raven.swing.PanelBorder();
        jLabel2 = new javax.swing.JLabel();
        spTable1 = new javax.swing.JScrollPane();
        tblUser = new com.raven.swing.Table();
        txtFindUser = new javax.swing.JTextField();
        jButton5 = new javax.swing.JButton();
        pnAdressForm = new javax.swing.JPanel();
        pnAddressForm_list = new javax.swing.JPanel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        btnUpdateAddress = new javax.swing.JButton();
        txtID = new javax.swing.JTextField();
        txtCity = new javax.swing.JTextField();
        txtWard = new javax.swing.JTextField();
        btnDeleteAddress = new javax.swing.JButton();
        txtStreet = new javax.swing.JTextField();
        jLabel40 = new javax.swing.JLabel();
        rdoIs_Default = new javax.swing.JRadioButton();
        rdoNotDefault = new javax.swing.JRadioButton();
        btnCreateAddress = new javax.swing.JButton();
        btnCancelAddress = new javax.swing.JButton();
        cboUserList = new javax.swing.JComboBox<>();
        tblAddress = new javax.swing.JPanel();
        panelBorder1 = new com.raven.swing.PanelBorder();
        jLabel1 = new javax.swing.JLabel();
        spTable = new javax.swing.JScrollPane();
        tblAddress_table = new com.raven.swing.Table();
        txtFindAddress = new javax.swing.JTextField();
        jButton4 = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jLabel98 = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(0, 0));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        pnUserForm.setBackground(new java.awt.Color(255, 255, 255));

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel15.setText("Users Form");

        jLabel16.setText("Please fill all the information.");

        jLabel17.setText("ID");

        jLabel18.setText("Name");

        jLabel19.setText("Email");

        jLabel20.setText("Phone");

        jLabel21.setText("Password");

        jLabel22.setText("Re Password");

        jLabel23.setText("Role");

        btnCreate.setBackground(new java.awt.Color(0, 102, 102));
        btnCreate.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnCreate.setForeground(new java.awt.Color(255, 255, 255));
        btnCreate.setText("Create");
        btnCreate.setBorderPainted(false);
        btnCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateActionPerformed(evt);
            }
        });

        btnDelete.setBackground(new java.awt.Color(0, 102, 102));
        btnDelete.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnDelete.setForeground(new java.awt.Color(255, 255, 255));
        btnDelete.setText("Delete");
        btnDelete.setBorderPainted(false);
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnCancel.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnCancel.setText("Cancel");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        txtId.setEditable(false);
        txtId.setEnabled(false);

        buttonGroup2.add(rdoUser);
        rdoUser.setSelected(true);
        rdoUser.setText("User");

        buttonGroup2.add(rdoAdmin);
        rdoAdmin.setText("Admin");

        btnUpdate1.setBackground(new java.awt.Color(0, 102, 102));
        btnUpdate1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnUpdate1.setForeground(new java.awt.Color(255, 255, 255));
        btnUpdate1.setText("Update");
        btnUpdate1.setBorderPainted(false);
        btnUpdate1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdate1ActionPerformed(evt);
            }
        });

        lblQR_IMG.setMaximumSize(new java.awt.Dimension(180, 180));
        lblQR_IMG.setMinimumSize(new java.awt.Dimension(180, 180));
        lblQR_IMG.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblQR_IMGMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout pnUserFormLayout = new javax.swing.GroupLayout(pnUserForm);
        pnUserForm.setLayout(pnUserFormLayout);
        pnUserFormLayout.setHorizontalGroup(
            pnUserFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnUserFormLayout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addComponent(btnCreate)
                .addGap(33, 33, 33)
                .addComponent(btnUpdate1)
                .addGap(31, 31, 31)
                .addComponent(btnDelete)
                .addGap(36, 36, 36)
                .addComponent(btnCancel)
                .addGap(0, 500, Short.MAX_VALUE))
            .addGroup(pnUserFormLayout.createSequentialGroup()
                .addGroup(pnUserFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnUserFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pnUserFormLayout.createSequentialGroup()
                            .addGap(34, 34, 34)
                            .addGroup(pnUserFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel22)
                                .addComponent(jLabel21)
                                .addComponent(jLabel20)
                                .addComponent(jLabel19)
                                .addComponent(jLabel18)
                                .addComponent(jLabel17)
                                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel15)
                                .addComponent(jLabel23)))
                        .addGroup(pnUserFormLayout.createSequentialGroup()
                            .addGap(220, 220, 220)
                            .addGroup(pnUserFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(txtPassword, javax.swing.GroupLayout.DEFAULT_SIZE, 358, Short.MAX_VALUE)
                                .addComponent(txtPhone)
                                .addComponent(txtEmail)
                                .addComponent(txtName)
                                .addComponent(txtId))))
                    .addGroup(pnUserFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pnUserFormLayout.createSequentialGroup()
                            .addComponent(rdoAdmin)
                            .addGap(46, 46, 46)
                            .addComponent(rdoUser))
                        .addComponent(txtRePas, javax.swing.GroupLayout.PREFERRED_SIZE, 358, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblQR_IMG, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(82, 82, 82))
        );
        pnUserFormLayout.setVerticalGroup(
            pnUserFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnUserFormLayout.createSequentialGroup()
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel16)
                .addGap(21, 21, 21)
                .addGroup(pnUserFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(pnUserFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnUserFormLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnUserFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel18)
                            .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(pnUserFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel19))
                        .addGap(28, 28, 28)
                        .addGroup(pnUserFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel20))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pnUserFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel21)
                            .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(pnUserFormLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(lblQR_IMG, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(9, 9, 9)
                .addGroup(pnUserFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(txtRePas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnUserFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(rdoUser)
                    .addComponent(rdoAdmin))
                .addGap(49, 49, 49)
                .addGroup(pnUserFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCreate)
                    .addComponent(btnDelete)
                    .addComponent(btnCancel)
                    .addComponent(btnUpdate1))
                .addGap(31, 31, 31))
        );

        TabUser.addTab("User Form", pnUserForm);

        panelBorder2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel2.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(127, 127, 127));
        jLabel2.setText("User");

        spTable1.setBorder(null);

        tblUser.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No.", "User ID", "Password", "User Name", "Phone", "Email", "Role", "Create At", "Update_At", "QR_IMG"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, true, true, true, true, true, true, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblUser.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblUserMouseClicked(evt);
            }
        });
        spTable1.setViewportView(tblUser);

        txtFindUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFindUserActionPerformed(evt);
            }
        });
        txtFindUser.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtFindUserKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtFindUserKeyTyped(evt);
            }
        });

        jButton5.setBackground(new java.awt.Color(0, 102, 102));
        jButton5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton5.setForeground(new java.awt.Color(255, 255, 255));
        jButton5.setText("Search");
        jButton5.setBorderPainted(false);
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelBorder2Layout = new javax.swing.GroupLayout(panelBorder2);
        panelBorder2.setLayout(panelBorder2Layout);
        panelBorder2Layout.setHorizontalGroup(
            panelBorder2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder2Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(panelBorder2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(spTable1, javax.swing.GroupLayout.DEFAULT_SIZE, 916, Short.MAX_VALUE)
                    .addGroup(panelBorder2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFindUser)
                        .addGap(18, 18, 18)
                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        panelBorder2Layout.setVerticalGroup(
            panelBorder2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder2Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(panelBorder2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtFindUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spTable1, javax.swing.GroupLayout.PREFERRED_SIZE, 386, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(16, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnUserTableLayout = new javax.swing.GroupLayout(pnUserTable);
        pnUserTable.setLayout(pnUserTableLayout);
        pnUserTableLayout.setHorizontalGroup(
            pnUserTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelBorder2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pnUserTableLayout.setVerticalGroup(
            pnUserTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelBorder2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        TabUser.addTab("User Table", pnUserTable);

        pnAddressForm_list.setBackground(new java.awt.Color(255, 255, 255));

        jLabel33.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel33.setText("Address Form");

        jLabel34.setText("Please fill all the information.");

        jLabel35.setText("ID");

        jLabel36.setText("User ID");

        jLabel37.setText("City");

        jLabel38.setText("Ward");

        jLabel39.setText("Street");

        btnUpdateAddress.setBackground(new java.awt.Color(0, 102, 102));
        btnUpdateAddress.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnUpdateAddress.setForeground(new java.awt.Color(255, 255, 255));
        btnUpdateAddress.setText("Update");
        btnUpdateAddress.setBorderPainted(false);
        btnUpdateAddress.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateAddressActionPerformed(evt);
            }
        });

        txtID.setEnabled(false);

        txtWard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtWardActionPerformed(evt);
            }
        });

        btnDeleteAddress.setBackground(new java.awt.Color(0, 102, 102));
        btnDeleteAddress.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnDeleteAddress.setForeground(new java.awt.Color(255, 255, 255));
        btnDeleteAddress.setText("Delete");
        btnDeleteAddress.setBorderPainted(false);
        btnDeleteAddress.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteAddressActionPerformed(evt);
            }
        });

        jLabel40.setText("Is_Default");

        buttonGroup1.add(rdoIs_Default);
        rdoIs_Default.setSelected(true);
        rdoIs_Default.setText("Yes");

        buttonGroup1.add(rdoNotDefault);
        rdoNotDefault.setText("No");

        btnCreateAddress.setBackground(new java.awt.Color(0, 102, 102));
        btnCreateAddress.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnCreateAddress.setForeground(new java.awt.Color(255, 255, 255));
        btnCreateAddress.setText("CREATE");
        btnCreateAddress.setBorderPainted(false);
        btnCreateAddress.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateAddressActionPerformed(evt);
            }
        });

        btnCancelAddress.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnCancelAddress.setForeground(new java.awt.Color(0, 102, 102));
        btnCancelAddress.setText("CANCEL");
        btnCancelAddress.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        btnCancelAddress.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelAddressActionPerformed(evt);
            }
        });

        cboUserList.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout pnAddressForm_listLayout = new javax.swing.GroupLayout(pnAddressForm_list);
        pnAddressForm_list.setLayout(pnAddressForm_listLayout);
        pnAddressForm_listLayout.setHorizontalGroup(
            pnAddressForm_listLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnAddressForm_listLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnAddressForm_listLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnAddressForm_listLayout.createSequentialGroup()
                        .addGroup(pnAddressForm_listLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel33))
                        .addGap(565, 565, 565))
                    .addComponent(txtID, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 651, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCity, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 651, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnAddressForm_listLayout.createSequentialGroup()
                        .addGroup(pnAddressForm_listLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel39)
                            .addComponent(jLabel40))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(pnAddressForm_listLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnAddressForm_listLayout.createSequentialGroup()
                                .addComponent(rdoIs_Default)
                                .addGap(29, 29, 29)
                                .addComponent(rdoNotDefault))
                            .addGroup(pnAddressForm_listLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(txtWard, javax.swing.GroupLayout.DEFAULT_SIZE, 651, Short.MAX_VALUE)
                                .addComponent(txtStreet))))
                    .addGroup(pnAddressForm_listLayout.createSequentialGroup()
                        .addGroup(pnAddressForm_listLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel38)
                            .addComponent(jLabel37)
                            .addComponent(jLabel35)
                            .addGroup(pnAddressForm_listLayout.createSequentialGroup()
                                .addGap(34, 34, 34)
                                .addComponent(btnCreateAddress)
                                .addGap(26, 26, 26)
                                .addComponent(btnUpdateAddress)))
                        .addGap(32, 32, 32)
                        .addComponent(btnDeleteAddress)
                        .addGap(26, 26, 26)
                        .addComponent(btnCancelAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnAddressForm_listLayout.createSequentialGroup()
                        .addComponent(jLabel36)
                        .addGap(66, 66, 66)
                        .addComponent(cboUserList, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(0, 170, Short.MAX_VALUE))
        );
        pnAddressForm_listLayout.setVerticalGroup(
            pnAddressForm_listLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnAddressForm_listLayout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addComponent(jLabel33)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel34)
                .addGap(18, 18, 18)
                .addGroup(pnAddressForm_listLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel35)
                    .addComponent(txtID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnAddressForm_listLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel36)
                    .addComponent(cboUserList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnAddressForm_listLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel37))
                .addGap(18, 18, 18)
                .addGroup(pnAddressForm_listLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtWard, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel38))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnAddressForm_listLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel39)
                    .addComponent(txtStreet, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnAddressForm_listLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel40)
                    .addComponent(rdoIs_Default)
                    .addComponent(rdoNotDefault))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 88, Short.MAX_VALUE)
                .addGroup(pnAddressForm_listLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnUpdateAddress)
                    .addComponent(btnDeleteAddress)
                    .addComponent(btnCreateAddress)
                    .addComponent(btnCancelAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(62, 62, 62))
        );

        javax.swing.GroupLayout pnAdressFormLayout = new javax.swing.GroupLayout(pnAdressForm);
        pnAdressForm.setLayout(pnAdressFormLayout);
        pnAdressFormLayout.setHorizontalGroup(
            pnAdressFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnAdressFormLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnAddressForm_list, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnAdressFormLayout.setVerticalGroup(
            pnAdressFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnAdressFormLayout.createSequentialGroup()
                .addComponent(pnAddressForm_list, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        TabUser.addTab("Address Form", pnAdressForm);

        panelBorder1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(127, 127, 127));
        jLabel1.setText("ADDRESS TABLE");

        spTable.setBorder(null);

        tblAddress_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No.", "Address ID", "User ID", "User Name", "City", "Ward", "Street", "Is_Default"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, true, true, true, true, true, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblAddress_table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblAddress_tableMouseClicked(evt);
            }
        });
        spTable.setViewportView(tblAddress_table);

        txtFindAddress.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFindAddressActionPerformed(evt);
            }
        });
        txtFindAddress.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtFindAddressKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtFindAddressKeyTyped(evt);
            }
        });

        jButton4.setBackground(new java.awt.Color(0, 102, 102));
        jButton4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton4.setForeground(new java.awt.Color(255, 255, 255));
        jButton4.setText("Search");
        jButton4.setBorderPainted(false);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelBorder1Layout = new javax.swing.GroupLayout(panelBorder1);
        panelBorder1.setLayout(panelBorder1Layout);
        panelBorder1Layout.setHorizontalGroup(
            panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(spTable)
                    .addGroup(panelBorder1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFindAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 650, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelBorder1Layout.setVerticalGroup(
            panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtFindAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spTable, javax.swing.GroupLayout.PREFERRED_SIZE, 386, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(16, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout tblAddressLayout = new javax.swing.GroupLayout(tblAddress);
        tblAddress.setLayout(tblAddressLayout);
        tblAddressLayout.setHorizontalGroup(
            tblAddressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelBorder1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        tblAddressLayout.setVerticalGroup(
            tblAddressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelBorder1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        TabUser.addTab("Address Table", tblAddress);

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel8.setText("Users");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(TabUser)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel98)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel98)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(TabUser)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1285, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 722, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateActionPerformed
        this.insertUser();
    }//GEN-LAST:event_btnCreateActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        this.deleteUser();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        this.clearForm();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnUpdate1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdate1ActionPerformed
        this.updateUser();
    }//GEN-LAST:event_btnUpdate1ActionPerformed

    private void txtWardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtWardActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtWardActionPerformed

    private void txtFindAddressActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFindAddressActionPerformed
  
    }//GEN-LAST:event_txtFindAddressActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton4ActionPerformed

    private void btnUpdateAddressActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateAddressActionPerformed
        this.updateAddressAdmin();
        fillTableAddress();
    }//GEN-LAST:event_btnUpdateAddressActionPerformed

    private void btnDeleteAddressActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteAddressActionPerformed
       this.deleteAddress();
       fillTableAddress();
    }//GEN-LAST:event_btnDeleteAddressActionPerformed

    private void btnCreateAddressActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateAddressActionPerformed
        this.insertAddress();
        fillTableAddress();
    }//GEN-LAST:event_btnCreateAddressActionPerformed

    private void btnCancelAddressActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelAddressActionPerformed
        clearFormAddress();
    }//GEN-LAST:event_btnCancelAddressActionPerformed

    private void tblAddress_tableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblAddress_tableMouseClicked
        this.indexAddress = tblAddress_table.getSelectedRow();
        editAddress();
    }//GEN-LAST:event_tblAddress_tableMouseClicked

    private void txtFindAddressKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFindAddressKeyPressed
       
    }//GEN-LAST:event_txtFindAddressKeyPressed

    private void txtFindAddressKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFindAddressKeyTyped
        findAddressbyKeyword();
    }//GEN-LAST:event_txtFindAddressKeyTyped

    private void tblUserMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblUserMouseClicked
        this.index = tblUser.getSelectedRow();
        edit();
    }//GEN-LAST:event_tblUserMouseClicked

    private void txtFindUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFindUserActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFindUserActionPerformed

    private void txtFindUserKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFindUserKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFindUserKeyPressed

    private void txtFindUserKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFindUserKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFindUserKeyTyped

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton5ActionPerformed

    private void lblQR_IMGMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblQR_IMGMouseClicked

    }//GEN-LAST:event_lblQR_IMGMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane TabUser;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnCancelAddress;
    private javax.swing.JButton btnCreate;
    private javax.swing.JButton btnCreateAddress;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnDeleteAddress;
    private javax.swing.JButton btnUpdate1;
    private javax.swing.JButton btnUpdateAddress;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JComboBox<String> cboUserList;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel98;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lblQR_IMG;
    private com.raven.swing.PanelBorder panelBorder1;
    private com.raven.swing.PanelBorder panelBorder2;
    private javax.swing.JPanel pnAddressForm_list;
    private javax.swing.JPanel pnAdressForm;
    private javax.swing.JPanel pnUserForm;
    private javax.swing.JPanel pnUserTable;
    private javax.swing.JRadioButton rdoAdmin;
    private javax.swing.JRadioButton rdoIs_Default;
    private javax.swing.JRadioButton rdoNotDefault;
    private javax.swing.JRadioButton rdoUser;
    private javax.swing.JScrollPane spTable;
    private javax.swing.JScrollPane spTable1;
    private javax.swing.JPanel tblAddress;
    private com.raven.swing.Table tblAddress_table;
    private com.raven.swing.Table tblUser;
    private javax.swing.JTextField txtCity;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtFindAddress;
    private javax.swing.JTextField txtFindUser;
    private javax.swing.JTextField txtID;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtName;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtPhone;
    private javax.swing.JPasswordField txtRePas;
    private javax.swing.JTextField txtStreet;
    private javax.swing.JTextField txtWard;
    // End of variables declaration//GEN-END:variables

    private BufferedImage generateQRCodeImage(String text, int width, int height) throws WriterException {
	    QRCodeWriter qrCodeWriter = new QRCodeWriter();
	    BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, generateQRCodeHints());
	    BufferedImage qrCodeImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	    Graphics2D graphics = qrCodeImage.createGraphics();
	    graphics.setColor(Color.WHITE);
	    graphics.fillRect(0, 0, width, height);
	    graphics.setColor(Color.BLACK);
	    for (int x = 0; x < width; x++) {
	        for (int y = 0; y < height; y++) {
	            if (bitMatrix.get(x, y)) {
	                graphics.fillRect(x, y, 1, 1);
	            }
	        }
	    }
	    graphics.dispose();
	    return qrCodeImage;
	}
	
	private String createUserQRImage(User user ,String password) {
	    try {

	        BufferedImage qrCodeImage = generateQRCodeImage(user.getEmail()+ " " + password , 200, 200);

	        String qrCodeFileName = "User" + user.getEmail() + ".png";
	        String qrCodeFilePath = "D:\\FPT Polytechnic\\Ki4\\DuAn1\\admin\\java-ui-dashboard-001\\part 3\\ui-dashboard-001\\src\\com\\raven\\QRCode\\Users\\"  + qrCodeFileName;
	        File qrCodeFile = new File(qrCodeFilePath);
	      
	        ImageIO.write(qrCodeImage, "png", qrCodeFile);

	        return qrCodeFileName;
	    } catch (WriterException | IOException e) {
	        e.printStackTrace();
	        return null;
	    }
	}

    	private Hashtable<EncodeHintType, Object> generateQRCodeHints() {
	    Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
	    hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
	    hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
	    return hints;
	}

}
