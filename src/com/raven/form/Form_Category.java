package com.raven.form;

import com.raven.model.Category;
import dao.CategoryDAO;

import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import utils.ThongBao;


public class Form_Category extends javax.swing.JPanel {
 private Timer timer;
    private static final int DELAY = 2000;
    /**
     * Creates new form Form_1
     */
    public Form_Category() {
        initComponents();
        loadParent_Id_list();
        init();
    }
    CategoryDAO catergoryDAO = new CategoryDAO();

    int index = -1;
    int no = 1;
    DefaultTableModel modelCategory;

    void init() {
        fillTableCategory();
    }

    void fillTableCategory() {
        DefaultTableModel modelCategory = (DefaultTableModel) tblCatergory.getModel();
        modelCategory.setRowCount(0);
        int no = 1;
        try {
            List<Category> list = catergoryDAO.selectAll();
            for (Category c : list) {
                Object[] row = {
                    no,
                    c.getId(),
                    c.getParent_id(),
                    c.getName()};

                modelCategory.addRow(row);
                no++;
            }
        } catch (Exception e) {
            ThongBao.alert(this, "Lỗi truy vấn dữ liệu!");
        }
    }
void fillTableCategory(String keyword) {
        DefaultTableModel modelCategory = (DefaultTableModel) tblCatergory.getModel();
        modelCategory.setRowCount(0);
        int no = 1;
        try {
            List<Category> list = catergoryDAO.selectByName(keyword);
            for (Category c : list) {
                Object[] row = {
                    no,
                    c.getId(),
                    c.getParent_id(),
                    c.getName()};

                modelCategory.addRow(row);
                no++;
            }
        } catch (Exception e) {
            ThongBao.alert(this, "Lỗi truy vấn dữ liệu!");
        }
    }
    
    Category getFormCategory() {
        Category category = new Category();
        if (txtID_Catergory.getText().equals("")) {
            category.setId(0);

        } else {
            category.setId(Integer.parseInt(txtID_Catergory.getText()));
        }


        Object selectedItem = cboParent_ID.getSelectedItem();
    if (selectedItem != null && selectedItem instanceof Category) {
        Category selectedCategory = (Category) selectedItem;
        category.setParent_id(selectedCategory.getId());
    } else {
        category.setParent_id(0); 
    }

        category.setName(txtNameCatergory.getText().trim());
        System.out.println("get " + category);
        return category;
    }

    void setFormCategory(Category c) {

        txtID_Catergory.setText(String.valueOf(c.getId()));
//    cboParent_ID.setSelectedItem(String.valueOf(c.getParent_id()));
    
        if (c != null) {
            Category cate = (Category)cboParent_ID.getModel().getSelectedItem();
            cboParent_ID.getModel().setSelectedItem(c);
            
            System.out.println("set Parent ID: "+cboParent_ID);
        } else {
            cboParent_ID.setSelectedItem(null);
        }

        txtNameCatergory.setText(c.getName());
        System.out.println("set " + c);
    }

    void loadParent_Id_list() {
        DefaultComboBoxModel model = (DefaultComboBoxModel) cboParent_ID.getModel();
        model.removeAllElements();
        List<Category> cate = catergoryDAO.selectAll();
//        model.addElement(null);
        for (Category ca : cate) {
            if(ca != null){
               model.addElement(ca);  
            }
           
        }
        
//       cboParent_ID.setSelectedIndex(0);
    }


    void clearFormCategory() {
        txtID_Catergory.setText("");
        cboParent_ID.setSelectedIndex(0);
        txtNameCatergory.setText("");

        Category u = new Category();

        this.setFormCategory(u);
        this.index = -1;

    }

    void insertCategory() {
        if (isValidate()) {
            Category u = this.getFormCategory();
          if(u != null){
                try{
                    catergoryDAO.insert(u);
                    this.fillTableCategory();
                    this.clearFormCategory();
                    ThongBao.alert(this, "Thêm mới thành công!");
                } catch (Exception e) {
                    ThongBao.alert(this, "Thêm mới thất bại!");
                    System.out.println(u);
                    e.printStackTrace();
                }
          }
            
        }
    }

    void updateCategory() {
        Category u = this.getFormCategory();
        if (isValidate()) {
            try {
                catergoryDAO.update(u);
                this.fillTableCategory();
                this.clearFormCategory();
                ThongBao.alert(this, "Cập nhật thành công!");
            } catch (Exception e) {
                ThongBao.alert(this, "Cập nhật thất bại!");
                System.out.println(e.getMessage());
            }
        }
    }

    void deleteCategory() {
        int id = Integer.parseInt(txtID_Catergory.getText());
        ThongBao.confirm(this, "Bạn thực sự muốn xóa danh mục này?");
        try {
            catergoryDAO.delete(id);
            this.fillTableCategory();
            this.clearFormCategory();
            ThongBao.alert(this, "Xóa thành công!");
        } catch (Exception e) {
            ThongBao.alert(this, "Xóa thất bại!");
            System.out.println(e.getMessage());
        }

    }
     void findCategoryByName(){
        String keyword = txtFind_Category.getText();
        List<Category> cate = catergoryDAO.selectByName(keyword);
        if (cate != null && !cate.isEmpty()) {
            fillTableCategory(keyword);
        } else {
            ThongBao.alert(this, "Không tìm thấy sản phẩm với tên " + keyword);
            System.out.println(keyword);
        }
    }

    void edit() {
        int id = Integer.valueOf(tblCatergory.getValueAt(index, 1).toString());

        Category u = catergoryDAO.selectById(id);
        this.setFormCategory(u);
        TabCatergory.setSelectedIndex(0);

    }

    boolean isValidate() {
        Category u = getFormCategory();

        // Kiểm tra name
        if (u.getName() == null || u.getName().trim().isEmpty()) {
            ThongBao.alert(this, "Tên không được để trống!");
            return false;
        }

        return true;

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        TabCatergory = new javax.swing.JTabbedPane();
        pnCatergoryForm = new javax.swing.JPanel();
        jLabel56 = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        jLabel58 = new javax.swing.JLabel();
        jLabel59 = new javax.swing.JLabel();
        jLabel61 = new javax.swing.JLabel();
        btnCreate_Catergory = new javax.swing.JButton();
        btnUpdate_Catergory = new javax.swing.JButton();
        btnCancel_Catergory = new javax.swing.JButton();
        txtID_Catergory = new javax.swing.JTextField();
        txtNameCatergory = new javax.swing.JTextField();
        btnDelete_Catergory = new javax.swing.JButton();
        cboParent_ID = new javax.swing.JComboBox<>();
        pnCatergoryTable = new javax.swing.JPanel();
        panelBorder1 = new com.raven.swing.PanelBorder();
        jLabel1 = new javax.swing.JLabel();
        spTable = new javax.swing.JScrollPane();
        tblCatergory = new com.raven.swing.Table();
        txtFind_Category = new javax.swing.JTextField();
        jButton13 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel93 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(242, 242, 242));
        setPreferredSize(new java.awt.Dimension(0, 0));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setPreferredSize(new java.awt.Dimension(0, 0));

        pnCatergoryForm.setBackground(new java.awt.Color(255, 255, 255));
        pnCatergoryForm.setPreferredSize(new java.awt.Dimension(0, 0));

        jLabel56.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel56.setText("Category Form");

        jLabel57.setText("Please fill all the information.");

        jLabel58.setText("ID");

        jLabel59.setText("Parent ID");

        jLabel61.setText("Name");

        btnCreate_Catergory.setBackground(new java.awt.Color(0, 102, 102));
        btnCreate_Catergory.setForeground(new java.awt.Color(255, 255, 255));
        btnCreate_Catergory.setText("Create");
        btnCreate_Catergory.setBorderPainted(false);
        btnCreate_Catergory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreate_CatergoryActionPerformed(evt);
            }
        });

        btnUpdate_Catergory.setBackground(new java.awt.Color(0, 102, 102));
        btnUpdate_Catergory.setForeground(new java.awt.Color(255, 255, 255));
        btnUpdate_Catergory.setText("Update");
        btnUpdate_Catergory.setBorderPainted(false);
        btnUpdate_Catergory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdate_CatergoryActionPerformed(evt);
            }
        });

        btnCancel_Catergory.setText("Cancel");
        btnCancel_Catergory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancel_CatergoryActionPerformed(evt);
            }
        });

        txtID_Catergory.setEditable(false);

        btnDelete_Catergory.setBackground(new java.awt.Color(0, 102, 102));
        btnDelete_Catergory.setForeground(new java.awt.Color(255, 255, 255));
        btnDelete_Catergory.setText("Delete");
        btnDelete_Catergory.setBorderPainted(false);
        btnDelete_Catergory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDelete_CatergoryActionPerformed(evt);
            }
        });

        cboParent_ID.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboParent_ID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboParent_IDActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnCatergoryFormLayout = new javax.swing.GroupLayout(pnCatergoryForm);
        pnCatergoryForm.setLayout(pnCatergoryFormLayout);
        pnCatergoryFormLayout.setHorizontalGroup(
            pnCatergoryFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnCatergoryFormLayout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(pnCatergoryFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel58, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel57, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel56, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnCatergoryFormLayout.createSequentialGroup()
                        .addGroup(pnCatergoryFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel59)
                            .addComponent(jLabel61))
                        .addGap(131, 131, 131)
                        .addGroup(pnCatergoryFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnCatergoryFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(cboParent_ID, javax.swing.GroupLayout.Alignment.LEADING, 0, 614, Short.MAX_VALUE)
                                .addComponent(txtID_Catergory, javax.swing.GroupLayout.Alignment.LEADING))
                            .addComponent(txtNameCatergory, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 614, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(pnCatergoryFormLayout.createSequentialGroup()
                .addGap(55, 55, 55)
                .addComponent(btnCreate_Catergory)
                .addGap(30, 30, 30)
                .addComponent(btnUpdate_Catergory)
                .addGap(26, 26, 26)
                .addComponent(btnDelete_Catergory)
                .addGap(42, 42, 42)
                .addComponent(btnCancel_Catergory)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        pnCatergoryFormLayout.setVerticalGroup(
            pnCatergoryFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnCatergoryFormLayout.createSequentialGroup()
                .addComponent(jLabel56)
                .addGap(14, 14, 14)
                .addComponent(jLabel57)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnCatergoryFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel58)
                    .addComponent(txtID_Catergory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(pnCatergoryFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel59)
                    .addComponent(cboParent_ID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnCatergoryFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNameCatergory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel61))
                .addGap(46, 46, 46)
                .addGroup(pnCatergoryFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCreate_Catergory)
                    .addComponent(btnUpdate_Catergory)
                    .addComponent(btnCancel_Catergory)
                    .addComponent(btnDelete_Catergory))
                .addGap(260, 260, 260))
        );

        TabCatergory.addTab("Catergory Form", pnCatergoryForm);

        panelBorder1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("sansserif", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(127, 127, 127));
        jLabel1.setText("Standard Table Design");

        spTable.setBorder(null);

        tblCatergory.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No.", "Category ID", "Parent_ID", "Name"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblCatergory.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblCatergoryMouseClicked(evt);
            }
        });
        spTable.setViewportView(tblCatergory);

        txtFind_Category.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFind_CategoryKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtFind_CategoryKeyTyped(evt);
            }
        });

        jButton13.setBackground(new java.awt.Color(0, 102, 102));
        jButton13.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton13.setForeground(new java.awt.Color(255, 255, 255));
        jButton13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/raven/icon/search.png"))); // NOI18N
        jButton13.setBorderPainted(false);
        jButton13.setIconTextGap(5);
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelBorder1Layout = new javax.swing.GroupLayout(panelBorder1);
        panelBorder1.setLayout(panelBorder1Layout);
        panelBorder1Layout.setHorizontalGroup(
            panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(spTable, javax.swing.GroupLayout.PREFERRED_SIZE, 859, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelBorder1Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txtFind_Category, javax.swing.GroupLayout.PREFERRED_SIZE, 513, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton13, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(85, Short.MAX_VALUE))
        );
        panelBorder1Layout.setVerticalGroup(
            panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelBorder1Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton13, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtFind_Category, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(21, 21, 21)
                .addComponent(spTable, javax.swing.GroupLayout.DEFAULT_SIZE, 457, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnCatergoryTableLayout = new javax.swing.GroupLayout(pnCatergoryTable);
        pnCatergoryTable.setLayout(pnCatergoryTableLayout);
        pnCatergoryTableLayout.setHorizontalGroup(
            pnCatergoryTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelBorder1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pnCatergoryTableLayout.setVerticalGroup(
            pnCatergoryTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelBorder1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        TabCatergory.addTab("Catergory Table", pnCatergoryTable);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setText("Category");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(TabCatergory, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addGap(23, 23, 23))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jLabel93)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addGap(0, 783, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel93)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(TabCatergory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 911, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 911, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 619, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 619, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnUpdate_CatergoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdate_CatergoryActionPerformed
        this.updateCategory();
    }//GEN-LAST:event_btnUpdate_CatergoryActionPerformed

    private void btnCreate_CatergoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreate_CatergoryActionPerformed
        this.insertCategory();
    }//GEN-LAST:event_btnCreate_CatergoryActionPerformed

    private void btnDelete_CatergoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDelete_CatergoryActionPerformed
        this.deleteCategory();
    }//GEN-LAST:event_btnDelete_CatergoryActionPerformed

    private void btnCancel_CatergoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancel_CatergoryActionPerformed
        this.clearFormCategory();
    }//GEN-LAST:event_btnCancel_CatergoryActionPerformed

    private void cboParent_IDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboParent_IDActionPerformed

    }//GEN-LAST:event_cboParent_IDActionPerformed

    private void tblCatergoryMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblCatergoryMouseClicked
        this.index = tblCatergory.getSelectedRow();
        this.edit();
    }//GEN-LAST:event_tblCatergoryMouseClicked

    private void txtFind_CategoryKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFind_CategoryKeyReleased
                    if (timer == null) {
        timer = new Timer(DELAY, e -> findCategoryByName());
        timer.setRepeats(false);
        }
        timer.restart();
    }//GEN-LAST:event_txtFind_CategoryKeyReleased

    private void txtFind_CategoryKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFind_CategoryKeyTyped

    }//GEN-LAST:event_txtFind_CategoryKeyTyped

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        this.findCategoryByName();
    }//GEN-LAST:event_jButton13ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane TabCatergory;
    private javax.swing.JButton btnCancel_Catergory;
    private javax.swing.JButton btnCreate_Catergory;
    private javax.swing.JButton btnDelete_Catergory;
    private javax.swing.JButton btnUpdate_Catergory;
    private javax.swing.JComboBox<String> cboParent_ID;
    private javax.swing.JButton jButton13;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel93;
    private javax.swing.JPanel jPanel3;
    private com.raven.swing.PanelBorder panelBorder1;
    private javax.swing.JPanel pnCatergoryForm;
    private javax.swing.JPanel pnCatergoryTable;
    private javax.swing.JScrollPane spTable;
    private com.raven.swing.Table tblCatergory;
    private javax.swing.JTextField txtFind_Category;
    private javax.swing.JTextField txtID_Catergory;
    private javax.swing.JTextField txtNameCatergory;
    // End of variables declaration//GEN-END:variables
}
