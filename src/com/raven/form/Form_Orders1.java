/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.raven.form;

import com.raven.model.Address;
import com.raven.model.Order;
import com.raven.model.Order_Item;
import com.raven.model.Product_Item;
import com.raven.model.User;
import com.raven.model.User_Payment_Method;
import dao.AddressDAO;
import dao.OrderDAO;
import dao.Order_ItemDAO;
import dao.Product_ItemDAO;
import dao.UserDAO;
import dao.User_Payment_MethodDAO;
import java.util.Date;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.table.DefaultTableModel;
import utils.ThongBao;

public class Form_Orders1 extends javax.swing.JPanel {

    /**
     * Creates new form Form_1
     */
    OrderDAO orderDao = new OrderDAO();
    UserDAO userDao = new UserDAO();
    User_Payment_MethodDAO upmDao = new User_Payment_MethodDAO();
    AddressDAO addressDao = new AddressDAO();
    Order_ItemDAO oiDao = new Order_ItemDAO();
    Product_ItemDAO piDao = new Product_ItemDAO();
    public Form_Orders1() {
        initComponents();
        fillCboUser();
        fillTableOrder();
        fillCboOrder();
        fillOrderItem();
        fillCboProduct_Item();
    }

    int index = -1;
    int indexOi = -1;
    public void fillCboUser() {
        DefaultComboBoxModel modelUser = (DefaultComboBoxModel) cboUser_id.getModel();
        modelUser.removeAllElements();
        List<User> listUser = userDao.selectAll();
        for (User user : listUser) {
            modelUser.addElement(user);
        }
    }

    public void fillUPM(int user_id) {
        DefaultComboBoxModel modelUPM = (DefaultComboBoxModel) cboUPM.getModel();
        modelUPM.removeAllElements();
        List<User_Payment_Method> listUPM = upmDao.selectByUserId(user_id);
        for (User_Payment_Method upm : listUPM) {
            modelUPM.addElement(upm);
        }
    }

    public void fillAddress(int user_id) {
        DefaultComboBoxModel modelAddress = (DefaultComboBoxModel) cboShipping_Address.getModel();
        modelAddress.removeAllElements();
        List<Address> listAdddress = addressDao.selectByUserId(user_id);
        for (Address listAdddres : listAdddress) {
            modelAddress.addElement(listAdddres);
        }
    }

    public void fillTableOrder() {
        DefaultTableModel modelOrder = (DefaultTableModel) tblOrder.getModel();
        modelOrder.setRowCount(0);

        int no = 1;
        try {
            List<Order> orderList = orderDao.selectAll();
            for (Order order : orderList) {
                Object[] row = {
                    no,
                    order.getId(),
                    userDao.selectById(String.valueOf(order.getUser_id())),
                    upmDao.selectById(order.getUser_payment_method_id()).getProvider(),
                    order.getShipping_address(),
                    order.getTotal_amount(),
                    order.isOrder_status() ? "Paid" : "Pending"
                };

                modelOrder.addRow(row);
                no++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            ThongBao.alert(this, "Lỗi truy vấn dữ liệu");
        }
    }

    public Order getFormOrder() {
        User u = (User) cboUser_id.getSelectedItem();
        User_Payment_Method upm = (User_Payment_Method) cboUPM.getSelectedItem();
        Address address = (Address) cboShipping_Address.getSelectedItem();
        Order o = new Order();
        o.setUser_id(u.getId());
        o.setUser_payment_method_id(upm.getId());
        o.setTotal_amount((float) Double.parseDouble(txtTotal_Amount.getText()));
        o.setOrder_status(rdoPaid.isSelected());
        o.setShipping_address(address.toString());
        o.setOrder_status(rdoPaid.isSelected());
        return o;
    }

    public void setForm(Order o) {
        txtOrder_id.setText(String.valueOf(o.getId()));
        cboUser_id.getModel().setSelectedItem(userDao.selectById(String.valueOf(o.getUser_id())));
        cboUPM.getModel().setSelectedItem(upmDao.selectById(o.getUser_payment_method_id()));
        cboShipping_Address.getModel().setSelectedItem(o.getShipping_address());
        txtTotal_Amount.setText(String.valueOf(o.getTotal_amount()));
        rdoPaid.setSelected(!o.isOrder_status());
    }

    public void cleanForm() {
        txtOrder_id.setText("");
        cboUser_id.setSelectedIndex(0);
        txtTotal_Amount.setText("");
        rdoPaid.setSelected(true);
        index = -1;
    }

    public void edit() {
        int id = (int) tblOrder.getValueAt(index, 1);
        Order o = orderDao.selectById(id);
        setForm(o);
        TabOrder.setSelectedIndex(0);
    }

    public void insertOrder() {
        try {
            User u = (User) cboUser_id.getSelectedItem();
            User_Payment_Method upm = (User_Payment_Method) cboUPM.getSelectedItem();
            Address a = (Address) cboShipping_Address.getSelectedItem();
            orderDao.insert(u.getId(), upm.getId(), a.getId());
            this.fillTableOrder();
            this.cleanForm();
            ThongBao.alert(this, "Thêm mới thành công");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Loi: " + e.getMessage());
            ThongBao.alert(this, "Thêm mới thất bại");
        }

    }

    public void updateOrder() {
        try {

            Order o = getFormOrder();
            orderDao.update(Integer.parseInt(txtOrder_id.getText()), o.getUser_payment_method_id(), o.getShipping_address(), rdoPaid.isSelected());
            this.fillTableOrder();
            this.cleanForm();
            ThongBao.alert(this, "Cập nhật thành công");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Loi: " + e.getMessage());
            ThongBao.alert(this, "Cập nhật thất bại" + e.getMessage());
        }

    }

    public void deleteOrder() {
        try {
            int id = Integer.parseInt(txtOrder_id.getText());
            orderDao.delete(id);
            this.fillTableOrder();
            this.cleanForm();
            ThongBao.alert(this, "Xóa thành công");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Loi: " + e.getMessage());
            ThongBao.alert(this, "Xóa thất bại");
        }

    }

    /*-----------------------Order Detail----------------------------*/
    public void fillCboOrder() {
        DefaultComboBoxModel modelOrder = (DefaultComboBoxModel) cboOrder.getModel();
        modelOrder.removeAllElements();
        List<Order> o = orderDao.selectAll();
        for (Order order : o) {
            modelOrder.addElement(order);
        }
    }

    public void fillCboProduct_Item() {
        
        DefaultComboBoxModel modelPi = (DefaultComboBoxModel) cboProduct_Item.getModel();
        modelPi.removeAllElements();
        List<Product_Item> list = piDao.selectAll();
        if (list != null) {
            for (Product_Item order_Item : list) {
                modelPi.addElement(order_Item);
            }
        }
        
    }
    
    
    public void fillOrderItem()
    {
        DefaultTableModel modelOrder = (DefaultTableModel) tblOrderDetail.getModel();
        modelOrder.setRowCount(0);
        int no = 1;
        try {
            List<Order_Item> orderItemList = oiDao.selectAll();
            for (Order_Item oi : orderItemList) {
                Object[] row = {
                    no,
                    oi.getId(),
                    oi.getOrder_id(),
//                    oi.getName(oi.getProduct_item_id()),
                    oi.getQty(),
                    oi.getPrice()
                };

                modelOrder.addRow(row);
                no++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            ThongBao.alert(this, "Lỗi truy vấn dữ liệu");
        }
    }
    
    public Order_Item getFormOrderItem()
    {
        Order o = (Order) cboOrder.getSelectedItem();
        Product_Item cboOi = (Product_Item) cboProduct_Item.getSelectedItem();
        Order_Item oi = new Order_Item();
        oi.setOrder_id(o.getId());
        if(isNumber(txtPrice_Order_detail.getText()) && Float.parseFloat(txtPrice_Order_detail.getText()) > 0)
        {
            oi.setPrice(Float.parseFloat(txtPrice_Order_detail.getText()));
        }else{
            ThongBao.alert(this, "Vui lòng nhập số dương vào ô Price");
            return null;
        }
        if(isNumber(txtQuantity.getText()) && Integer.parseInt(txtQuantity.getText()) > 0)
        {
            oi.setQty(Integer.parseInt(txtQuantity.getText()));
        }else{
            ThongBao.alert(this, "Vui lòng nhập số dương vào ô Quantity");
            return null;
        }
        oi.setProduct_item_id(cboOi.getId());
        return oi;
    }
    
    public void setFormOrderItem(Order_Item oi)
    {
        txtOderDetail_id.setText(String.valueOf(oi.getId()));
        cboOrder.getModel().setSelectedItem(orderDao.selectById(oi.getOrder_id()));
        cboProduct_Item.getModel().setSelectedItem(piDao.selectById(oi.getProduct_item_id()));
        txtPrice_Order_detail.setText(String.valueOf(oi.getPrice()));
        txtQuantity.setText(String.valueOf(oi.getQty()));
    }
    
    public void clearFormOrderItem()
    {
        txtOderDetail_id.setText("");
        cboOrder.setSelectedIndex(0);
        txtPrice_Order_detail.setText("");
        txtQuantity.setText("");
    }
    
    public void editOrderItem()
    {
        int id = (int) tblOrderDetail.getValueAt(indexOi, 1);
        Order_Item oi = oiDao.selectById(id);
        this.setFormOrderItem(oi);
        TabOrder.setSelectedIndex(2);
    }
    
    public boolean isNumber(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        
        try {
            Double.valueOf(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    
    public void insertOrderItem()
    {
        Order_Item oi = getFormOrderItem();
        if(oi != null){
            try {
                oiDao.insert(oi);
                this.fillOrderItem();
                this.clearFormOrderItem();
                ThongBao.alert(this, "Thêm mới thành công");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(""+e.getMessage());
                ThongBao.alert(this, "Thêm mới thất bại");
            }
        }
    }
    
    public void deteleOrderItem()
    {
        if(txtOderDetail_id.getText().equals(""))
        {
            ThongBao.alert(this, "Vui lòng chọn dòng sản phẩm muốn xóa");
        }else
        {
            try {
                oiDao.delete(Integer.parseInt(txtOderDetail_id.getText()));
                this.fillOrderItem();
                this.clearFormOrderItem();
                ThongBao.alert(this, "Xóa thành công");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(""+e.getMessage());
                ThongBao.alert(this, "Xóa thất bại");
            }
        }
    }
    
    public void updateOrderItem()
    {
        if(txtOderDetail_id.getText().equals(""))
        {
            ThongBao.alert(this, "Vui lòng chọn dòng sản phẩm muốn cập nhật");
        }else
        {
            Order_Item oi = getFormOrderItem();
            if(oi != null ){
                oi.setId(Integer.parseInt(txtOderDetail_id.getText()));
            try {
                oiDao.update(oi);
                this.fillOrderItem();
                this.clearFormOrderItem();
                ThongBao.alert(this, "Cập nhật thành công");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(""+e.getMessage());
                ThongBao.alert(this, "Cập nhật thất bại");
            }
            }
        }
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
        jPanel5 = new javax.swing.JPanel();
        TabOrder = new javax.swing.JTabbedPane();
        pnOrderForm = new javax.swing.JPanel();
        jLabel32 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        jLabel63 = new javax.swing.JLabel();
        btnCreate_Order = new javax.swing.JButton();
        btnDelete_Order = new javax.swing.JButton();
        btnCancel_Order = new javax.swing.JButton();
        txtOrder_id = new javax.swing.JTextField();
        txtTotal_Amount = new javax.swing.JTextField();
        rdoPaid = new javax.swing.JRadioButton();
        rdoPending = new javax.swing.JRadioButton();
        btnUpdate_Order1 = new javax.swing.JButton();
        cboUser_id = new javax.swing.JComboBox<>();
        cboUPM = new javax.swing.JComboBox<>();
        cboShipping_Address = new javax.swing.JComboBox<>();
        pnOrderTable = new javax.swing.JPanel();
        jButton28 = new javax.swing.JButton();
        txtFind_Order = new javax.swing.JTextField();
        panelBorder1 = new com.raven.swing.PanelBorder();
        jLabel1 = new javax.swing.JLabel();
        spTable = new javax.swing.JScrollPane();
        tblOrder = new com.raven.swing.Table();
        pnOrderDetailsForm = new javax.swing.JPanel();
        pnAddressForm_list2 = new javax.swing.JPanel();
        jLabel67 = new javax.swing.JLabel();
        jLabel68 = new javax.swing.JLabel();
        jLabel69 = new javax.swing.JLabel();
        jLabel70 = new javax.swing.JLabel();
        jLabel71 = new javax.swing.JLabel();
        jLabel72 = new javax.swing.JLabel();
        jLabel73 = new javax.swing.JLabel();
        btnCreate_Order_detail = new javax.swing.JButton();
        btnDelete_Order_Detail = new javax.swing.JButton();
        btnCancel_Order_detail = new javax.swing.JButton();
        txtOderDetail_id = new javax.swing.JTextField();
        txtQuantity = new javax.swing.JTextField();
        btnUpdate_Order_Detail1 = new javax.swing.JButton();
        cboOrder = new javax.swing.JComboBox<>();
        cboProduct_Item = new javax.swing.JComboBox<>();
        txtPrice_Order_detail = new javax.swing.JTextField();
        pnOrderDetailTable = new javax.swing.JPanel();
        jLabel76 = new javax.swing.JLabel();
        txtFind_Order_detail = new javax.swing.JTextField();
        jButton32 = new javax.swing.JButton();
        panelBorder2 = new com.raven.swing.PanelBorder();
        jLabel2 = new javax.swing.JLabel();
        spTable1 = new javax.swing.JScrollPane();
        tblOrderDetail = new com.raven.swing.Table();
        jLabel92 = new javax.swing.JLabel();
        jLabel101 = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(0, 0));

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setPreferredSize(new java.awt.Dimension(0, 0));
        jPanel5.setVerifyInputWhenFocusTarget(false);

        TabOrder.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TabOrderMouseClicked(evt);
            }
        });

        pnOrderForm.setBackground(new java.awt.Color(255, 255, 255));

        jLabel32.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel32.setText("Order Form");

        jLabel42.setText("Please fill all the information.");

        jLabel44.setText("ID");

        jLabel49.setText("User ID");

        jLabel50.setText("User Payment Method ID");

        jLabel53.setText("Shipping Address");

        jLabel54.setText("Total Amount");

        jLabel63.setText("Order Status");

        btnCreate_Order.setBackground(new java.awt.Color(0, 102, 102));
        btnCreate_Order.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnCreate_Order.setForeground(new java.awt.Color(255, 255, 255));
        btnCreate_Order.setText("Create");
        btnCreate_Order.setBorderPainted(false);
        btnCreate_Order.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreate_OrderActionPerformed(evt);
            }
        });

        btnDelete_Order.setBackground(new java.awt.Color(0, 102, 102));
        btnDelete_Order.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnDelete_Order.setForeground(new java.awt.Color(255, 255, 255));
        btnDelete_Order.setText("Delete");
        btnDelete_Order.setBorderPainted(false);
        btnDelete_Order.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDelete_OrderActionPerformed(evt);
            }
        });

        btnCancel_Order.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnCancel_Order.setText("Cancel");
        btnCancel_Order.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancel_OrderActionPerformed(evt);
            }
        });

        txtOrder_id.setEditable(false);

        txtTotal_Amount.setEditable(false);

        buttonGroup1.add(rdoPaid);
        rdoPaid.setSelected(true);
        rdoPaid.setText("Paid");

        buttonGroup1.add(rdoPending);
        rdoPending.setText("Pending");

        btnUpdate_Order1.setBackground(new java.awt.Color(0, 102, 102));
        btnUpdate_Order1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnUpdate_Order1.setForeground(new java.awt.Color(255, 255, 255));
        btnUpdate_Order1.setText("Update");
        btnUpdate_Order1.setBorderPainted(false);
        btnUpdate_Order1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdate_Order1ActionPerformed(evt);
            }
        });

        cboUser_id.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboUser_idActionPerformed(evt);
            }
        });

        cboUPM.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboUPMItemStateChanged(evt);
            }
        });
        cboUPM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboUPMActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnOrderFormLayout = new javax.swing.GroupLayout(pnOrderForm);
        pnOrderForm.setLayout(pnOrderFormLayout);
        pnOrderFormLayout.setHorizontalGroup(
            pnOrderFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnOrderFormLayout.createSequentialGroup()
                .addGroup(pnOrderFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnOrderFormLayout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addGroup(pnOrderFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel54)
                            .addComponent(jLabel53)
                            .addComponent(jLabel50)
                            .addComponent(jLabel49)
                            .addComponent(jLabel44)
                            .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel32)
                            .addGroup(pnOrderFormLayout.createSequentialGroup()
                                .addGroup(pnOrderFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(btnCreate_Order)
                                    .addComponent(jLabel63))
                                .addGap(54, 54, 54)
                                .addComponent(btnUpdate_Order1)
                                .addGap(54, 54, 54)
                                .addComponent(btnDelete_Order)
                                .addGap(58, 58, 58)
                                .addComponent(btnCancel_Order)))
                        .addGap(0, 443, Short.MAX_VALUE))
                    .addGroup(pnOrderFormLayout.createSequentialGroup()
                        .addGap(220, 220, 220)
                        .addGroup(pnOrderFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtOrder_id, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtTotal_Amount)
                            .addComponent(cboUser_id, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cboUPM, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(pnOrderFormLayout.createSequentialGroup()
                                .addComponent(rdoPaid)
                                .addGap(41, 41, 41)
                                .addComponent(rdoPending)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(cboShipping_Address, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        pnOrderFormLayout.setVerticalGroup(
            pnOrderFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnOrderFormLayout.createSequentialGroup()
                .addComponent(jLabel32)
                .addGap(14, 14, 14)
                .addComponent(jLabel42)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnOrderFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel44)
                    .addComponent(txtOrder_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnOrderFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel49)
                    .addComponent(cboUser_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnOrderFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel50)
                    .addComponent(cboUPM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnOrderFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel53)
                    .addComponent(cboShipping_Address, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnOrderFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel54)
                    .addComponent(txtTotal_Amount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23)
                .addGroup(pnOrderFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel63)
                    .addComponent(rdoPaid)
                    .addComponent(rdoPending))
                .addGap(50, 50, 50)
                .addGroup(pnOrderFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancel_Order)
                    .addComponent(btnDelete_Order)
                    .addComponent(btnCreate_Order)
                    .addComponent(btnUpdate_Order1))
                .addGap(116, 116, 116))
        );

        TabOrder.addTab("Order Form", pnOrderForm);

        pnOrderTable.setBackground(new java.awt.Color(255, 255, 255));

        jButton28.setBackground(new java.awt.Color(0, 102, 102));
        jButton28.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton28.setForeground(new java.awt.Color(255, 255, 255));
        jButton28.setText("Search");
        jButton28.setBorderPainted(false);
        jButton28.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton28ActionPerformed(evt);
            }
        });

        panelBorder1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(127, 127, 127));
        jLabel1.setText("Standard Table Design");

        spTable.setBorder(null);

        tblOrder.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "ID", "User Name", "User Payment Method", "Address", "Total amount", "Order status"
            }
        ));
        tblOrder.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblOrderMouseClicked(evt);
            }
        });
        spTable.setViewportView(tblOrder);
        if (tblOrder.getColumnModel().getColumnCount() > 0) {
            tblOrder.getColumnModel().getColumn(0).setPreferredWidth(10);
            tblOrder.getColumnModel().getColumn(1).setPreferredWidth(10);
            tblOrder.getColumnModel().getColumn(2).setPreferredWidth(30);
            tblOrder.getColumnModel().getColumn(3).setPreferredWidth(10);
            tblOrder.getColumnModel().getColumn(6).setPreferredWidth(30);
        }

        javax.swing.GroupLayout panelBorder1Layout = new javax.swing.GroupLayout(panelBorder1);
        panelBorder1.setLayout(panelBorder1Layout);
        panelBorder1Layout.setHorizontalGroup(
            panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelBorder1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(spTable))
                .addContainerGap())
        );
        panelBorder1Layout.setVerticalGroup(
            panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spTable, javax.swing.GroupLayout.DEFAULT_SIZE, 283, Short.MAX_VALUE)
                .addGap(20, 20, 20))
        );

        javax.swing.GroupLayout pnOrderTableLayout = new javax.swing.GroupLayout(pnOrderTable);
        pnOrderTable.setLayout(pnOrderTableLayout);
        pnOrderTableLayout.setHorizontalGroup(
            pnOrderTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnOrderTableLayout.createSequentialGroup()
                .addGroup(pnOrderTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnOrderTableLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(txtFind_Order, javax.swing.GroupLayout.PREFERRED_SIZE, 798, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                        .addComponent(jButton28, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnOrderTableLayout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(panelBorder1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnOrderTableLayout.setVerticalGroup(
            pnOrderTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnOrderTableLayout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addGroup(pnOrderTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFind_Order, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton28))
                .addGap(18, 18, 18)
                .addComponent(panelBorder1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(37, 37, 37))
        );

        TabOrder.addTab("Order Table", pnOrderTable);

        pnAddressForm_list2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel67.setBackground(new java.awt.Color(0, 102, 102));
        jLabel67.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel67.setText("Order Detail Form");

        jLabel68.setText("Please fill all the information.");

        jLabel69.setText("ID");

        jLabel70.setText("Order ID");

        jLabel71.setText("Product Item ID");

        jLabel72.setText("Quantity");

        jLabel73.setText("Price");

        btnCreate_Order_detail.setBackground(new java.awt.Color(0, 102, 102));
        btnCreate_Order_detail.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnCreate_Order_detail.setForeground(new java.awt.Color(255, 255, 255));
        btnCreate_Order_detail.setText("Create");
        btnCreate_Order_detail.setBorderPainted(false);
        btnCreate_Order_detail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreate_Order_detailActionPerformed(evt);
            }
        });

        btnDelete_Order_Detail.setBackground(new java.awt.Color(0, 102, 102));
        btnDelete_Order_Detail.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnDelete_Order_Detail.setForeground(new java.awt.Color(255, 255, 255));
        btnDelete_Order_Detail.setText("Delete");
        btnDelete_Order_Detail.setBorderPainted(false);
        btnDelete_Order_Detail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDelete_Order_DetailActionPerformed(evt);
            }
        });

        btnCancel_Order_detail.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnCancel_Order_detail.setText("Cancel");
        btnCancel_Order_detail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancel_Order_detailActionPerformed(evt);
            }
        });

        txtOderDetail_id.setEditable(false);

        txtQuantity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtQuantityActionPerformed(evt);
            }
        });

        btnUpdate_Order_Detail1.setBackground(new java.awt.Color(0, 102, 102));
        btnUpdate_Order_Detail1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnUpdate_Order_Detail1.setForeground(new java.awt.Color(255, 255, 255));
        btnUpdate_Order_Detail1.setText("Update");
        btnUpdate_Order_Detail1.setBorderPainted(false);
        btnUpdate_Order_Detail1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdate_Order_Detail1ActionPerformed(evt);
            }
        });

        cboOrder.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboOrderItemStateChanged(evt);
            }
        });

        cboProduct_Item.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboProduct_ItemItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout pnAddressForm_list2Layout = new javax.swing.GroupLayout(pnAddressForm_list2);
        pnAddressForm_list2.setLayout(pnAddressForm_list2Layout);
        pnAddressForm_list2Layout.setHorizontalGroup(
            pnAddressForm_list2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnAddressForm_list2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnAddressForm_list2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtQuantity, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 651, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnAddressForm_list2Layout.createSequentialGroup()
                        .addComponent(btnCreate_Order_detail)
                        .addGap(29, 29, 29)
                        .addComponent(btnUpdate_Order_Detail1)
                        .addGap(18, 18, 18)
                        .addComponent(btnDelete_Order_Detail)
                        .addGap(24, 24, 24)
                        .addComponent(btnCancel_Order_detail)
                        .addGap(478, 478, 478))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnAddressForm_list2Layout.createSequentialGroup()
                        .addGroup(pnAddressForm_list2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel73)
                            .addComponent(jLabel72)
                            .addComponent(jLabel71)
                            .addComponent(jLabel70)
                            .addComponent(jLabel69)
                            .addComponent(jLabel68, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel67))
                        .addGap(46, 46, 46)
                        .addGroup(pnAddressForm_list2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtOderDetail_id, javax.swing.GroupLayout.DEFAULT_SIZE, 652, Short.MAX_VALUE)
                            .addComponent(cboOrder, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cboProduct_Item, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtPrice_Order_detail))))
                .addGap(58, 58, 58))
        );
        pnAddressForm_list2Layout.setVerticalGroup(
            pnAddressForm_list2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnAddressForm_list2Layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addComponent(jLabel67)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel68)
                .addGap(18, 18, 18)
                .addGroup(pnAddressForm_list2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel69)
                    .addComponent(txtOderDetail_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnAddressForm_list2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel70)
                    .addComponent(cboOrder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnAddressForm_list2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel71)
                    .addComponent(cboProduct_Item, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnAddressForm_list2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel72))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnAddressForm_list2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnAddressForm_list2Layout.createSequentialGroup()
                        .addComponent(jLabel73)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 46, Short.MAX_VALUE)
                        .addGroup(pnAddressForm_list2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnCreate_Order_detail)
                            .addComponent(btnDelete_Order_Detail)
                            .addComponent(btnCancel_Order_detail)
                            .addComponent(btnUpdate_Order_Detail1))
                        .addGap(143, 143, 143))
                    .addGroup(pnAddressForm_list2Layout.createSequentialGroup()
                        .addComponent(txtPrice_Order_detail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        javax.swing.GroupLayout pnOrderDetailsFormLayout = new javax.swing.GroupLayout(pnOrderDetailsForm);
        pnOrderDetailsForm.setLayout(pnOrderDetailsFormLayout);
        pnOrderDetailsFormLayout.setHorizontalGroup(
            pnOrderDetailsFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnOrderDetailsFormLayout.createSequentialGroup()
                .addComponent(pnAddressForm_list2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        pnOrderDetailsFormLayout.setVerticalGroup(
            pnOrderDetailsFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnOrderDetailsFormLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnAddressForm_list2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        TabOrder.addTab("Order Detail Form", pnOrderDetailsForm);

        jLabel76.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel76.setText("Order Detail Table");

        txtFind_Order_detail.setText("Order ID");
        txtFind_Order_detail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFind_Order_detailActionPerformed(evt);
            }
        });

        jButton32.setBackground(new java.awt.Color(0, 102, 102));
        jButton32.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton32.setForeground(new java.awt.Color(255, 255, 255));
        jButton32.setText("Search");
        jButton32.setBorderPainted(false);
        jButton32.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton32ActionPerformed(evt);
            }
        });

        panelBorder2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel2.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(127, 127, 127));
        jLabel2.setText("Standard Table Design");

        spTable1.setBorder(null);

        tblOrderDetail.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "Id", "Order Id", "Product name", "Quantity", "Price"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblOrderDetail.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblOrderDetailMouseClicked(evt);
            }
        });
        spTable1.setViewportView(tblOrderDetail);

        javax.swing.GroupLayout panelBorder2Layout = new javax.swing.GroupLayout(panelBorder2);
        panelBorder2.setLayout(panelBorder2Layout);
        panelBorder2Layout.setHorizontalGroup(
            panelBorder2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder2Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(panelBorder2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelBorder2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(spTable1))
                .addContainerGap())
        );
        panelBorder2Layout.setVerticalGroup(
            panelBorder2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder2Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spTable1, javax.swing.GroupLayout.DEFAULT_SIZE, 252, Short.MAX_VALUE)
                .addGap(20, 20, 20))
        );

        javax.swing.GroupLayout pnOrderDetailTableLayout = new javax.swing.GroupLayout(pnOrderDetailTable);
        pnOrderDetailTable.setLayout(pnOrderDetailTableLayout);
        pnOrderDetailTableLayout.setHorizontalGroup(
            pnOrderDetailTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnOrderDetailTableLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(pnOrderDetailTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel76)
                    .addGroup(pnOrderDetailTableLayout.createSequentialGroup()
                        .addComponent(txtFind_Order_detail, javax.swing.GroupLayout.PREFERRED_SIZE, 843, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton32, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(pnOrderDetailTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnOrderDetailTableLayout.createSequentialGroup()
                    .addGap(14, 14, 14)
                    .addComponent(panelBorder2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGap(15, 15, 15)))
        );
        pnOrderDetailTableLayout.setVerticalGroup(
            pnOrderDetailTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnOrderDetailTableLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel76)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnOrderDetailTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFind_Order_detail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton32))
                .addContainerGap(410, Short.MAX_VALUE))
            .addGroup(pnOrderDetailTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnOrderDetailTableLayout.createSequentialGroup()
                    .addGap(73, 73, 73)
                    .addComponent(panelBorder2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGap(74, 74, 74)))
        );

        TabOrder.addTab("Order Detail Table", pnOrderDetailTable);

        jLabel92.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel92.setText("Orders");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel101)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel92)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(TabOrder)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel101)
                    .addComponent(jLabel92))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(TabOrder, javax.swing.GroupLayout.PREFERRED_SIZE, 496, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, 951, Short.MAX_VALUE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, 539, Short.MAX_VALUE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton28ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton28ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton28ActionPerformed

    private void btnCancel_Order_detailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancel_Order_detailActionPerformed
        clearFormOrderItem();
    }//GEN-LAST:event_btnCancel_Order_detailActionPerformed

    private void txtQuantityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtQuantityActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtQuantityActionPerformed

    private void txtFind_Order_detailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFind_Order_detailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFind_Order_detailActionPerformed

    private void jButton32ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton32ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton32ActionPerformed

    private void btnCancel_OrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancel_OrderActionPerformed
        cleanForm();// TODO add your handling code here:
    }//GEN-LAST:event_btnCancel_OrderActionPerformed

    private void tblOrderMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblOrderMouseClicked
        index = tblOrder.getSelectedRow();
        this.edit();// TODO add your handling code here:
    }//GEN-LAST:event_tblOrderMouseClicked

    private void cboUPMItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboUPMItemStateChanged

    }//GEN-LAST:event_cboUPMItemStateChanged

    private void cboUPMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboUPMActionPerformed

    }//GEN-LAST:event_cboUPMActionPerformed

    private void cboUser_idActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboUser_idActionPerformed
        User u = (User) cboUser_id.getSelectedItem();
        fillUPM(u.getId());
        fillAddress(u.getId());
    }//GEN-LAST:event_cboUser_idActionPerformed

    private void btnCreate_OrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreate_OrderActionPerformed
        this.insertOrder();// TODO add your handling code here:
    }//GEN-LAST:event_btnCreate_OrderActionPerformed

    private void btnUpdate_Order1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdate_Order1ActionPerformed
        this.updateOrder();// TODO add your handling code here:
    }//GEN-LAST:event_btnUpdate_Order1ActionPerformed

    private void btnDelete_OrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDelete_OrderActionPerformed
        this.deleteOrder();// TODO add your handling code here:
    }//GEN-LAST:event_btnDelete_OrderActionPerformed

    private void TabOrderMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TabOrderMouseClicked
        this.fillTableOrder();
    }//GEN-LAST:event_TabOrderMouseClicked

    private void cboOrderItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboOrderItemStateChanged
        
    }//GEN-LAST:event_cboOrderItemStateChanged

    private void cboProduct_ItemItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboProduct_ItemItemStateChanged
        
    }//GEN-LAST:event_cboProduct_ItemItemStateChanged

    private void tblOrderDetailMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblOrderDetailMouseClicked
        indexOi = tblOrderDetail.getSelectedRow();
        this.editOrderItem();
    }//GEN-LAST:event_tblOrderDetailMouseClicked

    private void btnCreate_Order_detailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreate_Order_detailActionPerformed
        this.insertOrderItem();
    }//GEN-LAST:event_btnCreate_Order_detailActionPerformed

    private void btnDelete_Order_DetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDelete_Order_DetailActionPerformed
        this.deteleOrderItem();// TODO add your handling code here:
    }//GEN-LAST:event_btnDelete_Order_DetailActionPerformed

    private void btnUpdate_Order_Detail1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdate_Order_Detail1ActionPerformed
        this.updateOrderItem();// TODO add your handling code here:
    }//GEN-LAST:event_btnUpdate_Order_Detail1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane TabOrder;
    private javax.swing.JButton btnCancel_Order;
    private javax.swing.JButton btnCancel_Order_detail;
    private javax.swing.JButton btnCreate_Order;
    private javax.swing.JButton btnCreate_Order_detail;
    private javax.swing.JButton btnDelete_Order;
    private javax.swing.JButton btnDelete_Order_Detail;
    private javax.swing.JButton btnUpdate_Order1;
    private javax.swing.JButton btnUpdate_Order_Detail1;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<String> cboOrder;
    private javax.swing.JComboBox<String> cboProduct_Item;
    private javax.swing.JComboBox<String> cboShipping_Address;
    private javax.swing.JComboBox<String> cboUPM;
    private javax.swing.JComboBox<String> cboUser_id;
    private javax.swing.JButton jButton28;
    private javax.swing.JButton jButton32;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel101;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel92;
    private javax.swing.JPanel jPanel5;
    private com.raven.swing.PanelBorder panelBorder1;
    private com.raven.swing.PanelBorder panelBorder2;
    private javax.swing.JPanel pnAddressForm_list2;
    private javax.swing.JPanel pnOrderDetailTable;
    private javax.swing.JPanel pnOrderDetailsForm;
    private javax.swing.JPanel pnOrderForm;
    private javax.swing.JPanel pnOrderTable;
    private javax.swing.JRadioButton rdoPaid;
    private javax.swing.JRadioButton rdoPending;
    private javax.swing.JScrollPane spTable;
    private javax.swing.JScrollPane spTable1;
    private com.raven.swing.Table tblOrder;
    private com.raven.swing.Table tblOrderDetail;
    private javax.swing.JTextField txtFind_Order;
    private javax.swing.JTextField txtFind_Order_detail;
    private javax.swing.JTextField txtOderDetail_id;
    private javax.swing.JTextField txtOrder_id;
    private javax.swing.JTextField txtPrice_Order_detail;
    private javax.swing.JTextField txtQuantity;
    private javax.swing.JTextField txtTotal_Amount;
    // End of variables declaration//GEN-END:variables
}
