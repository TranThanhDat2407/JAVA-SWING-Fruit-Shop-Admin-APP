package com.raven.form;

import com.raven.model.Category;
import com.raven.model.Gallery;
import com.raven.model.Product;
import com.raven.model.Product_Item;
import com.raven.swing.ScrollBar;
import dao.CategoryDAO;
import dao.GalleryDAO;
import dao.ProductDAO;
import dao.Product_ItemDAO;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.io.File;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import utils.ThongBao;
import utils.XImage;

public class Form_Products extends javax.swing.JPanel {
    private Timer timer;
    private static final int DELAY = 1000;
    private JFileChooser fileChooser;

    /**
     * Creates new form Form_1
     */
    public Form_Products() {
        initComponents();
        fileChooser = new JFileChooser();
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        init();
    }
    ProductDAO productDAO = new ProductDAO();
    GalleryDAO galleryDAO = new GalleryDAO();
    Product_ItemDAO product_ItemDAO = new Product_ItemDAO();
    int index = -1;
    int no = 1;
    DefaultTableModel modelProduct, modelGallery, modelProduct_Item;

    void init() {
        fillTableProduct();
        
        fillTableGallery();
        fillTableProduct_Item();
        loadCategoryList();
        loadProduct_ID_Gallery();
        loadProduct_ID_Stock();
    }

    void fillTableProduct() {
        DefaultTableModel modelProduct = (DefaultTableModel) tblProducts.getModel();
        modelProduct.setRowCount(0);
        int no = 1;
        try {
          
            List<Product> list = productDAO.selectAll();
            for (Product pr : list) {
                Object[] row = {
                    no,
                    pr.getId(),
                    pr.getCategory_id(),
                    pr.getName(),
                    pr.getDescription(),
                    pr.getCreate_at(),
                    pr.getUpdate_at()};

                modelProduct.addRow(row);
                no++;
            }
        } catch (Exception e) {
            ThongBao.alert(this, "Lỗi truy vấn dữ liệu!");
        }
    }

    void fillTableProduct(String keyword) {
        DefaultTableModel modelProduct = (DefaultTableModel) tblProducts.getModel();
        modelProduct.setRowCount(0);
        int no = 1;
        try {
            List<Product> list = productDAO.selectByName(keyword);
            for (Product pr : list) {
                Object[] row = {
                    no,
                    pr.getId(),
                    pr.getCategory_id(),
                    pr.getName(),
                    pr.getDescription(),
                    pr.getCreate_at(),
                    pr.getUpdate_at()};

                modelProduct.addRow(row);
                no++;
            }
        } catch (Exception e) {
            ThongBao.alert(this, "Lỗi truy vấn dữ liệu!");
        }
    }

    Product getFormProduct() {
        Product pr = new Product();
        if (txtProduct_Id.getText().equals("")) {
            pr.setId(Integer.parseInt("0"));

        } else {
            pr.setId(Integer.parseInt(txtProduct_Id.getText()));
        }

 
        Object selectedItem = cboCategory_ID.getSelectedItem();
        if (selectedItem != null && selectedItem instanceof Category) {
            Category selectedCategory = (Category) selectedItem;
            pr.setCategory_id(selectedCategory.getId());
        } else {
            pr.setCategory_id(0);
        }
  
        pr.setName(txtProduct_Name.getText().trim());
        pr.setDescription(txtDescription.getText().trim());
        System.out.println("get " + pr);
        return pr;

    }

    void setFormProducts(Product pr) {
        txtProduct_Id.setText(String.valueOf(pr.getId()));
        if (pr != null) {
            Category category = (Category) cboCategory_ID.getModel().getSelectedItem();
           cboCategory_ID.getModel().setSelectedItem(category);
          category.setParent_id(pr.getCategory_id());
            System.out.println("set Category ID: "+pr.getCategory_id());
        } else {
            cboCategory_ID.setSelectedItem(null);
        }
        
        txtProduct_Name.setText(pr.getName());
        txtDescription.setText(pr.getDescription());
        System.out.println("set " + pr);
    }

    void loadCategoryList() {
        DefaultComboBoxModel model = (DefaultComboBoxModel) cboCategory_ID.getModel();
        model.removeAllElements();
        CategoryDAO categoryDAO = new CategoryDAO();
        List<Category> categories = categoryDAO.selectAll();
        for (Category category : categories) {
            if (category != null) {
                model.addElement(category);
            }
        }
    }

    boolean validateProductForm() {

        if (txtProduct_Name.getText().trim().isEmpty()) {
            ThongBao.alert(this, "Tên sản phẩm không được để trống!");
            return false;
        }

        return true;
    }

    void clearFormProducts() {

        Product u = new Product();
        this.setFormProducts(u);

        txtProduct_Id.setText("");
        cboCategory_ID.setSelectedIndex(0);
        txtProduct_Name.setText("");
        txtDescription.setText("");
        this.index = -1;

    }

    void editFormProducts() {
        int id = Integer.valueOf(tblProducts.getValueAt(index, 1).toString());

        Product pr = productDAO.selectById(id);
        this.setFormProducts(pr);
        System.out.println("edit " + pr.getId());
        
        // Cập nhật lại dữ liệu trong cơ sở dữ liệu
        Product pro = getFormProduct();
        pro.setId(id);
        System.out.println("id " + id);

        TabProduct.setSelectedIndex(0);

    }

    void insertProducts() {
        if (validateProductForm()) {
            Product u = this.getFormProduct();
            if (u != null) {
                try {
                    productDAO.insert(u);
                    this.fillTableProduct();
                    this.clearFormProducts();
                    ThongBao.alert(this, "Thêm mới thành công!");
                } catch (Exception e) {
                    ThongBao.alert(this, "Thêm mới thất bại!");
                    e.printStackTrace();
                }
            }
        }
    }

    void updateProducts() {
        Product u = this.getFormProduct();
        if (validateProductForm()) {

            try {
                productDAO.update(u);
                this.fillTableProduct();
                this.clearFormProducts();
                ThongBao.alert(this, "Cập nhật thành công!");
            } catch (Exception e) {
                ThongBao.alert(this, "Cập nhật thất bại!");
            }
        }
    }

    void deleteProducts() {
        int id = Integer.parseInt(txtProduct_Id.getText());
        ThongBao.confirm(this, "Bạn thực sự muốn xóa danh mục này?");
        try {
            productDAO.delete(id);
            this.fillTableProduct();
            this.clearFormProducts();
            ThongBao.alert(this, "Xóa thành công!");
        } catch (Exception e) {
            ThongBao.alert(this, "Xóa thất bại!");
        }

    }

    void findProductByName(){
        String keyword = txtFind_Product.getText();
        List<Product> pr = productDAO.selectByName(keyword);
        if (pr != null && !pr.isEmpty()) {
            fillTableProduct(keyword);
           
        } else {
         fillTableProduct();
            System.out.println(keyword);
        }
    }
    /*
     ------------------Gallery------------------------------------
     */
    void fillTableGallery() {

        DefaultTableModel modelGallery = (DefaultTableModel) tblGallery.getModel();
        modelGallery.setRowCount(0);
        int no = 1;
        try {
            List<Gallery> list = galleryDAO.selectAll();
            for (Gallery g : list) {

                Object[] row = {
                    no,
                    g.getId(),
                    g.getProduct_id(),
                    g.getThumbnail()};

                modelGallery.addRow(row);
                no++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            ThongBao.alert(this, "Lỗi truy vấn dữ liệu!");
        }
    }

    void fillTableGallery(int id) {

        DefaultTableModel modelGallery = (DefaultTableModel) tblGallery.getModel();
        modelGallery.setRowCount(0);
        int no = 1;
        try {
            List<Gallery> list = galleryDAO.selectByIDGallerys(id);
            for (Gallery g : list) {

                Object[] row = {
                    no,
                    g.getId(),
                    g.getProduct_id(),
                    g.getThumbnail()};

                modelGallery.addRow(row);
                no++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            ThongBao.alert(this, "Lỗi truy vấn dữ liệu!");
        }
    }
    
    
    Gallery getFormGallery() {
        Gallery gl = new Gallery();
        if (txtGallery_ID.getText().equals("")) {
            gl.setId(Integer.parseInt("0"));

        } else {
            gl.setId(Integer.parseInt(txtGallery_ID.getText()));
        }

        Object selectedItem = cboProduct_ID_Gallery.getSelectedItem();
        if (selectedItem != null && selectedItem instanceof Product) {
            Product seleProduct = (Product) selectedItem;
            gl.setProduct_id(seleProduct.getId());
            
            System.out.println("Selected Product ID: " + seleProduct.getId());
        } else {
            gl.setProduct_id(0);
             
        }
        gl.setThumbnail(lblThumbnail.getToolTipText());
   
        System.out.println("Get GL: " + gl);
        return gl;
    }

    void setFormGallery(Gallery gl) {
        txtGallery_ID.setText(String.valueOf(gl.getId()));
        
       if (gl != null) {
           Product product = (Product) cboProduct_ID_Gallery.getModel().getSelectedItem();
           cboCategory_ID.getModel().setSelectedItem(product);
           product.setId(gl.getProduct_id());
            System.out.println("set Product ID: ");
        } else {
            cboCategory_ID.setSelectedItem(null);
        }
        
        
            String imagePath = gl.getThumbnail();
        if (imagePath != null && !imagePath.isEmpty()) {
            lblThumbnail.setToolTipText(imagePath);
            lblThumbnail.setIcon(XImage.readProductImg(imagePath));
        } else {
            lblThumbnail.setToolTipText("");
            lblThumbnail.setIcon(null);
            System.out.println(lblThumbnail);
        }


        System.out.println("Set GL: " + gl);
    }
    

    void loadProduct_ID_Gallery() {
        DefaultComboBoxModel model = (DefaultComboBoxModel) cboProduct_ID_Gallery.getModel();
        model.removeAllElements();
        ProductDAO proDAO = new ProductDAO();
         try {
        List<Product> products = productDAO.selectAll(); 
       
        for (Product product : products) {
            if(product != null){
                 model.addElement(product);
            }
           
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    }

    void selecPhoto() {
        JFileChooser fileChooser = new JFileChooser();
           fileChooser.setCurrentDirectory(new File("D:\\FPT Polytechnic\\Ki4\\DuAn1\\admin\\java-ui-dashboard-001\\part 3\\ui-dashboard-001\\src\\com\\raven\\imgs\\products"));
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            fileChooser.setDialogTitle("Choose Image");
            File file = fileChooser.getSelectedFile();
            XImage.save(file);
            ImageIcon icon = XImage.readProductImg(file.getName());
            lblThumbnail.setToolTipText(file.getName());
            lblThumbnail.setIcon(icon);
        }
    }

    boolean validateFormGallery() {
        if (cboProduct_ID_Gallery.getSelectedItem().equals("")) {
            ThongBao.alert(this, "Bạn chưa chọn mã danh mục");

        }

        if (lblThumbnail.getIcon() == null) {
            ThongBao.alert(this, "Ảnh không được để trống");
            return false;
        }

        return true;
    }

    void clearGallery() {
        Gallery u = new Gallery();
        this.setFormGallery(u);

        txtGallery_ID.setText("");
        cboProduct_ID_Gallery.setSelectedIndex(0);
        lblThumbnail.setIcon(null);
       

        this.index = -1;

    }

    void editGallery() {
        int id = Integer.valueOf(tblGallery.getValueAt(index, 1).toString());
        Gallery pr = galleryDAO.selectById(id);
        this.setFormGallery(pr);
        
        System.out.println("edit " + pr.getThumbnail());

        // Cập nhật lại dữ liệu trong cơ sở dữ liệu
        Gallery updatedGallery = getFormGallery();
        updatedGallery.setId(id);
        System.out.println("id " + id);

        TabProduct.setSelectedIndex(2);

    }

    void insertGallery() {
        if (validateFormGallery()) {
            Gallery u = this.getFormGallery();
            if (u != null) {
                try {
                    galleryDAO.insert(u);
                    this.fillTableGallery();
                    this.clearGallery();
                    ThongBao.alert(this, "Thêm mới thành công!");
                } catch (Exception e) {
                    ThongBao.alert(this, "Thêm mới thất bại!");
                    e.printStackTrace();
                }
            }
        }
    }

    void updateGallery() {
        Gallery u = this.getFormGallery();
        
        if (validateFormGallery()) {

            try {
                galleryDAO.update(u);
                this.fillTableGallery();
                this.clearGallery();
                ThongBao.alert(this, "Cập nhật thành công!");
            } catch (Exception e) {
                ThongBao.alert(this, "Cập nhật thất bại!");
                System.out.println("Error "+u);
                System.out.println(e.getMessage());
            }
            
        }
    }

    void deleteGallery() {
        int id = Integer.parseInt(txtGallery_ID.getText());
        ThongBao.confirm(this, "Bạn thực sự muốn xóa danh mục này?");
        try {
            galleryDAO.delete(id);
            this.fillTableGallery();
            this.clearGallery();
            ThongBao.alert(this, "Xóa thành công!");
        } catch (Exception e) {
            ThongBao.alert(this, "Xóa thất bại!");
        }

    }
    
   void findGalleryID() {
    try {
        int id = Integer.parseInt(txtFind_Gallery.getText());
        List<Gallery> gl = galleryDAO.selectByIDGallerys(id);
        if (gl != null) {
            fillTableGallery(id);
        } else {
            fillTableGallery();
        }
    } catch (NumberFormatException e) {
        ThongBao.alert(this, "ID không hợp lệ. Vui lòng nhập một số.");
    }
}


    /*
    ---------------------Product Item--------------------------------------
     */
    void fillTableProduct_Item() {
        DefaultTableModel modelProduct_Item = (DefaultTableModel) tblStock.getModel();
        modelProduct_Item.setRowCount(0);
        int no = 1;
        try {
            List<Product_Item> list = product_ItemDAO.selectAll();
            for (Product_Item pr : list) {
                Object[] row = {
                    no,
                    pr.getId(),
                    pr.getProduct_id(),
                    pr.getQty_in_stock(),
                    pr.getPrice(),
                    pr.getOriginal_price(),
                    pr.getCreate_at(),
                    pr.getUpdate_at()
                };

                modelProduct_Item.addRow(row);
                no++;
            }
        } catch (Exception e) {
            ThongBao.alert(this, "Lỗi truy vấn dữ liệu!");
        }
    }
void fillTableProduct_Item(int id) {
        DefaultTableModel modelProduct_Item = (DefaultTableModel) tblStock.getModel();
        modelProduct_Item.setRowCount(0);
        int no = 1;
        try {
            List<Product_Item> list = product_ItemDAO.selectByIDProduct_Items(id);
            for (Product_Item pr : list) {
                Object[] row = {
                    no,
                    pr.getId(),
                    pr.getProduct_id(),
                    pr.getQty_in_stock(),
                    pr.getPrice(),
                    pr.getOriginal_price(),
                    pr.getCreate_at(),
                    pr.getUpdate_at()
                };

                modelProduct_Item.addRow(row);
                no++;
            }
        } catch (Exception e) {
            ThongBao.alert(this, "Lỗi truy vấn dữ liệu!");
        }
    }
    Product_Item getFormProduct_Item() {
        Product_Item product_Item = new Product_Item();

        if (txtStock_id.getText().equals("")) {
            product_Item.setId(Integer.parseInt("0"));

        } else {
            product_Item.setId(Integer.parseInt(txtStock_id.getText()));
        }

        Product product = (Product) cboProductID_Stock.getSelectedItem();
        if (product != null) {
            System.out.println(product.getId());
            product_Item.setProduct_id(product.getId());
        } else {
            ThongBao.alert(this, "Sản phẩm không được tìm thấy.");
        }
        product_Item.setQty_in_stock(Integer.parseInt(txtQuantity_In_Stock.getText()));
        product_Item.setPrice(Float.parseFloat(txtPrice.getText()));
        product_Item.setOriginal_price(Float.parseFloat(txtOriginal_price.getText()));

        System.out.println(product_Item);
        return product_Item;

    }

    void setFormProduct_Item(Product_Item pr) {

        txtStock_id.setText(String.valueOf(pr.getId()));

        ProductDAO productDAO = new ProductDAO();

        // Lấy đối tượng Product từ ID sản phẩm
        Product product = productDAO.selectById(pr.getProduct_id());
        if (product != null) {
            cboProductID_Stock.getModel().setSelectedItem(product);
        } else {
            cboProductID_Stock.setSelectedIndex(-1);
        }

        txtQuantity_In_Stock.setText(String.valueOf(pr.getQty_in_stock()));
        txtPrice.setText(String.valueOf(pr.getPrice()));
        txtOriginal_price.setText(String.valueOf(pr.getOriginal_price()));
    }

    void loadProduct_ID_Stock() {
        DefaultComboBoxModel model = (DefaultComboBoxModel) cboProductID_Stock.getModel();
        model.removeAllElements();
        ProductDAO proDAO = new ProductDAO();
        List<Product> products = proDAO.selectAll();
        for (Product pr : products) {
            model.addElement(pr);
//            System.out.println(pr);
        }
    }

    boolean validateFormProduct_Item() {

        if (txtQuantity_In_Stock.getText().isEmpty()) {
            ThongBao.alert(this, "Vui lòng nhập số lượng hàng nhập kho.");
            return false;
        }

        if (!isNumeric(txtQuantity_In_Stock.getText())) {
            ThongBao.alert(this, "Số lượng trong kho phải là số.");
            return false;
        }

        if (txtPrice.getText().isEmpty()) {
            ThongBao.alert(this, "Vui lòng nhập giá sản phẩm.");
            return false;
        }

        if (!isNumeric(txtPrice.getText())) {
            ThongBao.alert(this, "Giá sản phẩm phải là số.");
            return false;
        }

        float price = Float.parseFloat(txtPrice.getText());
        if (price < 0) {
            ThongBao.alert(this, "Giá sản phẩm phải là số dương.");
            return false;
        }

        if (txtOriginal_price.getText().isEmpty()) {
            ThongBao.alert(this, "Vui lòng nhập giá gốc sản phẩm.");
            return false;
        }

        if (!isNumeric(txtOriginal_price.getText())) {
            ThongBao.alert(this, "Giá gốc sản phẩm phải là số.");
            return false;
        }

        float originalPrice = Float.parseFloat(txtOriginal_price.getText());
        if (originalPrice < 0) {
            ThongBao.alert(this, "Giá gốc sản phẩm phải là số dương.");
            return false;
        }

        return true;
    }

    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    void clearProduct_item() {
        Product_Item u = new Product_Item();
        this.setFormProduct_Item(u);
        txtStock_id.setText("");
        cboProductID_Stock.setSelectedIndex(0);
        txtQuantity_In_Stock.setText("");
        txtPrice.setText("");
        txtOriginal_price.setText("");
        this.index = -1;

    }

    void editProduct_item() {
        int id = Integer.valueOf(tblStock.getValueAt(index, 1).toString());

        Product_Item pr = product_ItemDAO.selectById(id);
        this.setFormProduct_Item(pr);
        TabProduct.setSelectedIndex(4);
    }

    void insertAndUpdateProduct_item() {
        if (validateFormProduct_Item()) {
            Product_Item u = this.getFormProduct_Item();
            u.setId(0);
            Product prod = (Product) cboProductID_Stock.getSelectedItem();
            if (u != null) {
                try {
                    product_ItemDAO.insert(prod.getId(),Integer.parseInt(txtQuantity_In_Stock.getText()),Double.parseDouble(txtPrice.getText()),Double.parseDouble(txtOriginal_price.getText()));
                    this.fillTableProduct_Item();
                    this.clearProduct_item();
                    ThongBao.alert(this, "Thêm mới thành công!");
                } catch (Exception e) {
                    ThongBao.alert(this, "Thêm mới thất bại!"+e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }


    void deleteProduct_item() {
        int id = Integer.parseInt(txtStock_id.getText());
        ThongBao.confirm(this, "Bạn thực sự muốn xóa danh mục này?");
        try {
            product_ItemDAO.delete(id);
            this.fillTableProduct_Item();
            this.clearProduct_item();
            ThongBao.alert(this, "Xóa thành công!");
        } catch (Exception e) {
            ThongBao.alert(this, "Xóa thất bại!");
            e.printStackTrace();
        }

    }
    
        void findByProductItemID() {
    try {
        int id = Integer.parseInt(txtFind_Stock.getText());
        List<Product_Item> product_Items = product_ItemDAO.selectByIDProduct_Items(id);
        if (product_Items != null) {
            fillTableProduct_Item(id);
        } else {
            fillTableProduct_Item();
        }
    } catch (NumberFormatException e) {
        ThongBao.alert(this, "ID không hợp lệ. Vui lòng nhập một số.");
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

        jPanel4 = new javax.swing.JPanel();
        TabProduct = new javax.swing.JTabbedPane();
        pnProductForm = new javax.swing.JPanel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        btnCreate_Product = new javax.swing.JButton();
        btnUpdate_Product = new javax.swing.JButton();
        btnCancel_Product = new javax.swing.JButton();
        txtProduct_Id = new javax.swing.JTextField();
        txtProduct_Name = new javax.swing.JTextField();
        jScrollPane14 = new javax.swing.JScrollPane();
        txtDescription = new javax.swing.JTextArea();
        btnDelete_Product = new javax.swing.JButton();
        cboCategory_ID = new javax.swing.JComboBox<>();
        pnProductTable = new javax.swing.JPanel();
        panelBorder1 = new com.raven.swing.PanelBorder();
        jLabel1 = new javax.swing.JLabel();
        spTable = new javax.swing.JScrollPane();
        tblProducts = new com.raven.swing.Table();
        txtFind_Product = new javax.swing.JTextField();
        jButton13 = new javax.swing.JButton();
        pnGalleryForm = new javax.swing.JPanel();
        pnAddressForm_list1 = new javax.swing.JPanel();
        jLabel46 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        btnCreate_Gallery = new javax.swing.JButton();
        btnDelete_Gallery = new javax.swing.JButton();
        btnCancel_Gallery = new javax.swing.JButton();
        txtGallery_ID = new javax.swing.JTextField();
        btnUpdate_Gallery = new javax.swing.JButton();
        lblThumbnail = new javax.swing.JLabel();
        cboProduct_ID_Gallery = new javax.swing.JComboBox<>();
        pnGalleryTable = new javax.swing.JPanel();
        pnTable_Gallery = new com.raven.swing.PanelBorder();
        jLabel8 = new javax.swing.JLabel();
        spTable1 = new javax.swing.JScrollPane();
        tblGallery = new com.raven.swing.Table();
        txtFind_Gallery = new javax.swing.JTextField();
        jButton16 = new javax.swing.JButton();
        pnStockForm = new javax.swing.JPanel();
        jLabel60 = new javax.swing.JLabel();
        txtStock_id = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtPrice = new javax.swing.JTextField();
        txtOriginal_price = new javax.swing.JTextField();
        btnDelete_Stock = new javax.swing.JButton();
        btnCancel_Stock = new javax.swing.JButton();
        txtQuantity_In_Stock = new javax.swing.JTextField();
        btnUpdate_Stock1 = new javax.swing.JButton();
        cboProductID_Stock = new javax.swing.JComboBox<>();
        pnStockTable = new javax.swing.JPanel();
        panelBorder2 = new com.raven.swing.PanelBorder();
        jLabel11 = new javax.swing.JLabel();
        spTable2 = new javax.swing.JScrollPane();
        tblStock = new com.raven.swing.Table();
        txtFind_Stock = new javax.swing.JTextField();
        btnFindStockID = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel100 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setPreferredSize(new java.awt.Dimension(0, 0));

        TabProduct.setPreferredSize(new java.awt.Dimension(0, 0));
        TabProduct.setRequestFocusEnabled(false);

        pnProductForm.setBackground(new java.awt.Color(255, 255, 255));

        jLabel26.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel26.setText("Product Form");

        jLabel27.setText("Please fill all the information.");

        jLabel28.setText("ID");

        jLabel29.setText("Category ID");

        jLabel30.setText("Product Name");

        jLabel31.setText("Description");

        btnCreate_Product.setBackground(new java.awt.Color(0, 102, 102));
        btnCreate_Product.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnCreate_Product.setForeground(new java.awt.Color(255, 255, 255));
        btnCreate_Product.setText("Create");
        btnCreate_Product.setBorderPainted(false);
        btnCreate_Product.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreate_ProductActionPerformed(evt);
            }
        });

        btnUpdate_Product.setBackground(new java.awt.Color(0, 102, 102));
        btnUpdate_Product.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnUpdate_Product.setForeground(new java.awt.Color(255, 255, 255));
        btnUpdate_Product.setText("Update");
        btnUpdate_Product.setBorderPainted(false);
        btnUpdate_Product.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdate_ProductActionPerformed(evt);
            }
        });

        btnCancel_Product.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnCancel_Product.setText("Cancel");
        btnCancel_Product.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancel_ProductActionPerformed(evt);
            }
        });

        txtProduct_Id.setEditable(false);

        txtDescription.setColumns(20);
        txtDescription.setRows(5);
        jScrollPane14.setViewportView(txtDescription);

        btnDelete_Product.setBackground(new java.awt.Color(0, 102, 102));
        btnDelete_Product.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnDelete_Product.setForeground(new java.awt.Color(255, 255, 255));
        btnDelete_Product.setText("Delete");
        btnDelete_Product.setBorderPainted(false);
        btnDelete_Product.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDelete_ProductActionPerformed(evt);
            }
        });

        cboCategory_ID.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout pnProductFormLayout = new javax.swing.GroupLayout(pnProductForm);
        pnProductForm.setLayout(pnProductFormLayout);
        pnProductFormLayout.setHorizontalGroup(
            pnProductFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnProductFormLayout.createSequentialGroup()
                .addGroup(pnProductFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnProductFormLayout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addGroup(pnProductFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel31)
                            .addComponent(jLabel30)
                            .addComponent(jLabel29)
                            .addComponent(jLabel28)
                            .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel26)))
                    .addGroup(pnProductFormLayout.createSequentialGroup()
                        .addGap(220, 220, 220)
                        .addGroup(pnProductFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtProduct_Id, javax.swing.GroupLayout.DEFAULT_SIZE, 684, Short.MAX_VALUE)
                            .addComponent(cboCategory_ID, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtProduct_Name)
                            .addComponent(jScrollPane14, javax.swing.GroupLayout.DEFAULT_SIZE, 684, Short.MAX_VALUE))))
                .addContainerGap(93, Short.MAX_VALUE))
            .addGroup(pnProductFormLayout.createSequentialGroup()
                .addGap(85, 85, 85)
                .addComponent(btnCreate_Product)
                .addGap(34, 34, 34)
                .addComponent(btnUpdate_Product)
                .addGap(40, 40, 40)
                .addComponent(btnDelete_Product)
                .addGap(39, 39, 39)
                .addComponent(btnCancel_Product)
                .addGap(0, 489, Short.MAX_VALUE))
        );
        pnProductFormLayout.setVerticalGroup(
            pnProductFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnProductFormLayout.createSequentialGroup()
                .addComponent(jLabel26)
                .addGap(14, 14, 14)
                .addComponent(jLabel27)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnProductFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28)
                    .addComponent(txtProduct_Id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnProductFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel29)
                    .addComponent(cboCategory_ID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnProductFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtProduct_Name, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30))
                .addGap(18, 18, 18)
                .addGroup(pnProductFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnProductFormLayout.createSequentialGroup()
                        .addComponent(jLabel31)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 176, Short.MAX_VALUE)
                        .addGroup(pnProductFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnCreate_Product)
                            .addComponent(btnUpdate_Product)
                            .addComponent(btnDelete_Product)
                            .addComponent(btnCancel_Product))
                        .addGap(159, 159, 159))
                    .addGroup(pnProductFormLayout.createSequentialGroup()
                        .addComponent(jScrollPane14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        TabProduct.addTab("Product Form", pnProductForm);

        pnProductTable.setBackground(new java.awt.Color(255, 255, 255));
        pnProductTable.setPreferredSize(new java.awt.Dimension(0, 0));

        panelBorder1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("sansserif", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(127, 127, 127));
        jLabel1.setText("Standard Table Design");

        spTable.setBorder(null);

        tblProducts.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No.", "Product ID", "Category ID", "Product Name", "Description", "Create at", "Update at"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblProducts.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblProductsMouseClicked(evt);
            }
        });
        spTable.setViewportView(tblProducts);

        txtFind_Product.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFind_ProductKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtFind_ProductKeyTyped(evt);
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
                .addGroup(panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelBorder1Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txtFind_Product, javax.swing.GroupLayout.PREFERRED_SIZE, 570, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton13))
                    .addComponent(spTable, javax.swing.GroupLayout.PREFERRED_SIZE, 930, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(53, Short.MAX_VALUE))
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
                            .addComponent(txtFind_Product, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(21, 21, 21)
                .addComponent(spTable, javax.swing.GroupLayout.DEFAULT_SIZE, 474, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnProductTableLayout = new javax.swing.GroupLayout(pnProductTable);
        pnProductTable.setLayout(pnProductTableLayout);
        pnProductTableLayout.setHorizontalGroup(
            pnProductTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelBorder1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pnProductTableLayout.setVerticalGroup(
            pnProductTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelBorder1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        TabProduct.addTab("Product Table", pnProductTable);

        pnAddressForm_list1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel46.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel46.setText("Gallery Form");

        jLabel47.setText("Please fill all the information.");

        jLabel48.setText("ID");

        jLabel51.setText("Product ID");

        jLabel52.setText("Thumbnail");

        btnCreate_Gallery.setBackground(new java.awt.Color(0, 102, 102));
        btnCreate_Gallery.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnCreate_Gallery.setForeground(new java.awt.Color(255, 255, 255));
        btnCreate_Gallery.setText("Create");
        btnCreate_Gallery.setBorderPainted(false);
        btnCreate_Gallery.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreate_GalleryActionPerformed(evt);
            }
        });

        btnDelete_Gallery.setBackground(new java.awt.Color(0, 102, 102));
        btnDelete_Gallery.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnDelete_Gallery.setForeground(new java.awt.Color(255, 255, 255));
        btnDelete_Gallery.setText("Delete");
        btnDelete_Gallery.setBorderPainted(false);
        btnDelete_Gallery.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDelete_GalleryActionPerformed(evt);
            }
        });

        btnCancel_Gallery.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnCancel_Gallery.setText("Cancel");
        btnCancel_Gallery.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancel_GalleryActionPerformed(evt);
            }
        });

        txtGallery_ID.setEditable(false);
        txtGallery_ID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtGallery_IDActionPerformed(evt);
            }
        });

        btnUpdate_Gallery.setBackground(new java.awt.Color(0, 102, 102));
        btnUpdate_Gallery.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnUpdate_Gallery.setForeground(new java.awt.Color(255, 255, 255));
        btnUpdate_Gallery.setText("Update");
        btnUpdate_Gallery.setBorderPainted(false);
        btnUpdate_Gallery.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdate_GalleryActionPerformed(evt);
            }
        });

        lblThumbnail.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        lblThumbnail.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblThumbnailMouseClicked(evt);
            }
        });

        cboProduct_ID_Gallery.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout pnAddressForm_list1Layout = new javax.swing.GroupLayout(pnAddressForm_list1);
        pnAddressForm_list1.setLayout(pnAddressForm_list1Layout);
        pnAddressForm_list1Layout.setHorizontalGroup(
            pnAddressForm_list1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnAddressForm_list1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnAddressForm_list1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnAddressForm_list1Layout.createSequentialGroup()
                        .addGroup(pnAddressForm_list1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel46)
                            .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 792, Short.MAX_VALUE))
                    .addGroup(pnAddressForm_list1Layout.createSequentialGroup()
                        .addGroup(pnAddressForm_list1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnAddressForm_list1Layout.createSequentialGroup()
                                .addGap(51, 51, 51)
                                .addComponent(btnCreate_Gallery)
                                .addGap(50, 50, 50)
                                .addComponent(btnUpdate_Gallery)
                                .addGap(49, 49, 49)
                                .addComponent(btnDelete_Gallery)
                                .addGap(47, 47, 47)
                                .addComponent(btnCancel_Gallery))
                            .addGroup(pnAddressForm_list1Layout.createSequentialGroup()
                                .addGroup(pnAddressForm_list1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel48)
                                    .addGroup(pnAddressForm_list1Layout.createSequentialGroup()
                                        .addComponent(jLabel51)
                                        .addGap(133, 133, 133)
                                        .addGroup(pnAddressForm_list1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(txtGallery_ID, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                                            .addComponent(cboProduct_ID_Gallery, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                                .addGap(20, 20, 20)
                                .addComponent(jLabel52)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblThumbnail, javax.swing.GroupLayout.PREFERRED_SIZE, 364, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        pnAddressForm_list1Layout.setVerticalGroup(
            pnAddressForm_list1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnAddressForm_list1Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(jLabel46)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel47)
                .addGroup(pnAddressForm_list1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnAddressForm_list1Layout.createSequentialGroup()
                        .addGroup(pnAddressForm_list1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnAddressForm_list1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(pnAddressForm_list1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel48)
                                    .addComponent(txtGallery_ID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(34, 34, 34)
                                .addGroup(pnAddressForm_list1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel51)
                                    .addComponent(cboProduct_ID_Gallery, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(pnAddressForm_list1Layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(jLabel52)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 183, Short.MAX_VALUE)
                        .addGroup(pnAddressForm_list1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnCreate_Gallery)
                            .addComponent(btnCancel_Gallery)
                            .addComponent(btnUpdate_Gallery)
                            .addComponent(btnDelete_Gallery))
                        .addGap(201, 201, 201))
                    .addGroup(pnAddressForm_list1Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(lblThumbnail, javax.swing.GroupLayout.PREFERRED_SIZE, 289, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        javax.swing.GroupLayout pnGalleryFormLayout = new javax.swing.GroupLayout(pnGalleryForm);
        pnGalleryForm.setLayout(pnGalleryFormLayout);
        pnGalleryFormLayout.setHorizontalGroup(
            pnGalleryFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnAddressForm_list1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pnGalleryFormLayout.setVerticalGroup(
            pnGalleryFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnAddressForm_list1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        TabProduct.addTab("Gallery Form", pnGalleryForm);

        pnGalleryTable.setBackground(new java.awt.Color(255, 255, 255));
        pnGalleryTable.setPreferredSize(new java.awt.Dimension(0, 0));

        pnTable_Gallery.setBackground(new java.awt.Color(255, 255, 255));

        jLabel8.setFont(new java.awt.Font("sansserif", 1, 24)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(127, 127, 127));
        jLabel8.setText("Standard Table Design");

        spTable1.setBorder(null);

        tblGallery.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No.", "ID", "Product ID", "Thumbnail"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblGallery.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblGalleryMouseClicked(evt);
            }
        });
        spTable1.setViewportView(tblGallery);

        txtFind_Gallery.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFind_GalleryActionPerformed(evt);
            }
        });
        txtFind_Gallery.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtFind_GalleryKeyTyped(evt);
            }
        });

        jButton16.setBackground(new java.awt.Color(0, 102, 102));
        jButton16.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton16.setForeground(new java.awt.Color(255, 255, 255));
        jButton16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/raven/icon/search.png"))); // NOI18N
        jButton16.setBorderPainted(false);
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton16ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnTable_GalleryLayout = new javax.swing.GroupLayout(pnTable_Gallery);
        pnTable_Gallery.setLayout(pnTable_GalleryLayout);
        pnTable_GalleryLayout.setHorizontalGroup(
            pnTable_GalleryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnTable_GalleryLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnTable_GalleryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(pnTable_GalleryLayout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addGap(32, 32, 32)
                        .addComponent(txtFind_Gallery, javax.swing.GroupLayout.PREFERRED_SIZE, 560, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton16))
                    .addComponent(spTable1, javax.swing.GroupLayout.PREFERRED_SIZE, 908, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(67, Short.MAX_VALUE))
        );
        pnTable_GalleryLayout.setVerticalGroup(
            pnTable_GalleryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnTable_GalleryLayout.createSequentialGroup()
                .addGroup(pnTable_GalleryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnTable_GalleryLayout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addGroup(pnTable_GalleryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton16, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtFind_Gallery, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pnTable_GalleryLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)))
                .addComponent(spTable1, javax.swing.GroupLayout.PREFERRED_SIZE, 443, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout pnGalleryTableLayout = new javax.swing.GroupLayout(pnGalleryTable);
        pnGalleryTable.setLayout(pnGalleryTableLayout);
        pnGalleryTableLayout.setHorizontalGroup(
            pnGalleryTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnGalleryTableLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(pnTable_Gallery, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnGalleryTableLayout.setVerticalGroup(
            pnGalleryTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnTable_Gallery, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        TabProduct.addTab("Gallery Table", pnGalleryTable);

        pnStockForm.setBackground(new java.awt.Color(255, 255, 255));

        jLabel60.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel60.setText("Stock Form");

        txtStock_id.setEditable(false);
        txtStock_id.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtStock_idActionPerformed(evt);
            }
        });

        jLabel4.setText("Please fill all the information.");

        jLabel5.setText("ID");

        jLabel6.setText("Product ID");

        jLabel7.setText("Quantity In Stock");

        jLabel9.setText("Price");

        jLabel10.setText("Original_Price");

        txtPrice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPriceActionPerformed(evt);
            }
        });

        btnDelete_Stock.setBackground(new java.awt.Color(0, 102, 102));
        btnDelete_Stock.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnDelete_Stock.setForeground(new java.awt.Color(255, 255, 255));
        btnDelete_Stock.setText("Delete");
        btnDelete_Stock.setBorderPainted(false);
        btnDelete_Stock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDelete_StockActionPerformed(evt);
            }
        });

        btnCancel_Stock.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnCancel_Stock.setText("Cancel");
        btnCancel_Stock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancel_StockActionPerformed(evt);
            }
        });

        btnUpdate_Stock1.setBackground(new java.awt.Color(0, 102, 102));
        btnUpdate_Stock1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnUpdate_Stock1.setForeground(new java.awt.Color(255, 255, 255));
        btnUpdate_Stock1.setText("Save");
        btnUpdate_Stock1.setBorderPainted(false);
        btnUpdate_Stock1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdate_Stock1ActionPerformed(evt);
            }
        });

        cboProductID_Stock.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout pnStockFormLayout = new javax.swing.GroupLayout(pnStockForm);
        pnStockForm.setLayout(pnStockFormLayout);
        pnStockFormLayout.setHorizontalGroup(
            pnStockFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnStockFormLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(pnStockFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10)
                    .addComponent(jLabel60)
                    .addComponent(jLabel4)
                    .addComponent(jLabel7)
                    .addGroup(pnStockFormLayout.createSequentialGroup()
                        .addGap(133, 133, 133)
                        .addComponent(btnUpdate_Stock1)))
                .addGroup(pnStockFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnStockFormLayout.createSequentialGroup()
                        .addGap(48, 48, 48)
                        .addComponent(btnDelete_Stock)
                        .addGap(66, 66, 66)
                        .addComponent(btnCancel_Stock)
                        .addGap(0, 508, Short.MAX_VALUE))
                    .addGroup(pnStockFormLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnStockFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtOriginal_price, javax.swing.GroupLayout.DEFAULT_SIZE, 538, Short.MAX_VALUE)
                            .addComponent(txtPrice)
                            .addComponent(txtQuantity_In_Stock)
                            .addComponent(cboProductID_Stock, 0, 538, Short.MAX_VALUE)
                            .addComponent(txtStock_id))
                        .addContainerGap(227, Short.MAX_VALUE))))
        );
        pnStockFormLayout.setVerticalGroup(
            pnStockFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnStockFormLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel60)
                .addGap(4, 4, 4)
                .addComponent(jLabel4)
                .addGap(37, 37, 37)
                .addGroup(pnStockFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtStock_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addGroup(pnStockFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnStockFormLayout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(25, 25, 25)
                        .addGroup(pnStockFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(txtQuantity_In_Stock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(20, 20, 20)
                        .addGroup(pnStockFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9))
                        .addGap(40, 40, 40)
                        .addGroup(pnStockFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(txtOriginal_price, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 176, Short.MAX_VALUE)
                        .addGroup(pnStockFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnDelete_Stock)
                            .addComponent(btnCancel_Stock)
                            .addComponent(btnUpdate_Stock1))
                        .addGap(51, 51, 51))
                    .addGroup(pnStockFormLayout.createSequentialGroup()
                        .addComponent(cboProductID_Stock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        TabProduct.addTab("Stock Form", pnStockForm);

        pnStockTable.setBackground(new java.awt.Color(255, 255, 255));

        panelBorder2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel11.setFont(new java.awt.Font("sansserif", 1, 24)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(127, 127, 127));
        jLabel11.setText("Standard Table Design");

        spTable2.setBorder(null);

        tblStock.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No.", "ID", "Product ID", "Quantity", "Price", "Original Price", "Create at", "Update at"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, true, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblStock.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblStockMouseClicked(evt);
            }
        });
        spTable2.setViewportView(tblStock);

        txtFind_Stock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFind_StockActionPerformed(evt);
            }
        });
        txtFind_Stock.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtFind_StockKeyTyped(evt);
            }
        });

        btnFindStockID.setBackground(new java.awt.Color(0, 102, 102));
        btnFindStockID.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnFindStockID.setForeground(new java.awt.Color(255, 255, 255));
        btnFindStockID.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/raven/icon/search.png"))); // NOI18N
        btnFindStockID.setBorderPainted(false);
        btnFindStockID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFindStockIDActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelBorder2Layout = new javax.swing.GroupLayout(panelBorder2);
        panelBorder2.setLayout(panelBorder2Layout);
        panelBorder2Layout.setHorizontalGroup(
            panelBorder2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelBorder2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(spTable2, javax.swing.GroupLayout.PREFERRED_SIZE, 904, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelBorder2Layout.createSequentialGroup()
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtFind_Stock, javax.swing.GroupLayout.PREFERRED_SIZE, 546, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnFindStockID)))
                .addGap(0, 65, Short.MAX_VALUE))
        );
        panelBorder2Layout.setVerticalGroup(
            panelBorder2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelBorder2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelBorder2Layout.createSequentialGroup()
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelBorder2Layout.createSequentialGroup()
                        .addGroup(panelBorder2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(txtFind_Stock)
                            .addComponent(btnFindStockID, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(27, 27, 27)))
                .addComponent(spTable2, javax.swing.GroupLayout.PREFERRED_SIZE, 452, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout pnStockTableLayout = new javax.swing.GroupLayout(pnStockTable);
        pnStockTable.setLayout(pnStockTableLayout);
        pnStockTableLayout.setHorizontalGroup(
            pnStockTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnStockTableLayout.createSequentialGroup()
                .addComponent(panelBorder2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnStockTableLayout.setVerticalGroup(
            pnStockTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelBorder2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        TabProduct.addTab("Stock Table", pnStockTable);

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(204, 204, 255));
        jLabel3.setText("Products");

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/raven/icon/apple-red-icon.png"))); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addComponent(jLabel100, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(792, Short.MAX_VALUE))
            .addComponent(TabProduct, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel100)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(0, 15, Short.MAX_VALUE))
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(TabProduct, javax.swing.GroupLayout.DEFAULT_SIZE, 585, Short.MAX_VALUE)
                .addGap(14, 14, 14))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1002, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 1002, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 670, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 670, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnCreate_ProductActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreate_ProductActionPerformed
        this.insertProducts();
    }//GEN-LAST:event_btnCreate_ProductActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        this.findProductByName();
    }//GEN-LAST:event_jButton13ActionPerformed

    private void btnCreate_GalleryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreate_GalleryActionPerformed
        this.insertGallery();
    }//GEN-LAST:event_btnCreate_GalleryActionPerformed

    private void btnCancel_GalleryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancel_GalleryActionPerformed
        this.clearGallery();
    }//GEN-LAST:event_btnCancel_GalleryActionPerformed

    private void txtFind_GalleryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFind_GalleryActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFind_GalleryActionPerformed

    private void jButton16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton16ActionPerformed
        this.findGalleryID();
    }//GEN-LAST:event_jButton16ActionPerformed

    private void txtStock_idActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtStock_idActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtStock_idActionPerformed

    private void txtPriceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPriceActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPriceActionPerformed

    private void btnCancel_StockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancel_StockActionPerformed
        this.clearProduct_item();
    }//GEN-LAST:event_btnCancel_StockActionPerformed

    private void txtFind_StockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFind_StockActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFind_StockActionPerformed

    private void btnFindStockIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFindStockIDActionPerformed
       this.findByProductItemID();
    }//GEN-LAST:event_btnFindStockIDActionPerformed

    private void btnUpdate_ProductActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdate_ProductActionPerformed
        this.updateProducts();
    }//GEN-LAST:event_btnUpdate_ProductActionPerformed

    private void btnDelete_ProductActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDelete_ProductActionPerformed
        this.deleteProducts();
    }//GEN-LAST:event_btnDelete_ProductActionPerformed

    private void btnCancel_ProductActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancel_ProductActionPerformed
        this.clearFormProducts();
    }//GEN-LAST:event_btnCancel_ProductActionPerformed

    private void txtGallery_IDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtGallery_IDActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtGallery_IDActionPerformed

    private void btnUpdate_Stock1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdate_Stock1ActionPerformed
        this.insertAndUpdateProduct_item();
    }//GEN-LAST:event_btnUpdate_Stock1ActionPerformed

    private void btnDelete_StockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDelete_StockActionPerformed
        this.deleteProduct_item();
    }//GEN-LAST:event_btnDelete_StockActionPerformed

    private void lblThumbnailMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblThumbnailMouseClicked
        this.selecPhoto();
    }//GEN-LAST:event_lblThumbnailMouseClicked

    private void btnUpdate_GalleryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdate_GalleryActionPerformed
        this.updateGallery();
    }//GEN-LAST:event_btnUpdate_GalleryActionPerformed

    private void btnDelete_GalleryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDelete_GalleryActionPerformed
        this.deleteGallery();
    }//GEN-LAST:event_btnDelete_GalleryActionPerformed

    private void tblProductsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblProductsMouseClicked
        this.index = tblProducts.getSelectedRow();
        this.editFormProducts();
    }//GEN-LAST:event_tblProductsMouseClicked

    private void txtFind_ProductKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFind_ProductKeyReleased
//           if (timer == null) {
//        timer = new Timer(DELAY, e -> findProductByName());
//        timer.setRepeats(false);
//        }
//        timer.restart();
    
    }//GEN-LAST:event_txtFind_ProductKeyReleased

    private void txtFind_ProductKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFind_ProductKeyTyped
                 if (timer == null) {
        timer = new Timer(DELAY, e -> findProductByName());
        timer.setRepeats(false);
        }
        timer.restart();
    }//GEN-LAST:event_txtFind_ProductKeyTyped

    private void tblGalleryMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblGalleryMouseClicked
        this.index = tblGallery.getSelectedRow();
        this.editGallery();
    }//GEN-LAST:event_tblGalleryMouseClicked

    private void tblStockMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblStockMouseClicked
        this.index = tblStock.getSelectedRow();
        this.editProduct_item();
    }//GEN-LAST:event_tblStockMouseClicked

    private void txtFind_GalleryKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFind_GalleryKeyTyped
                 if (timer == null) {
        timer = new Timer(DELAY, e -> findGalleryID());
        timer.setRepeats(false);
        }
        timer.restart();
    }//GEN-LAST:event_txtFind_GalleryKeyTyped

    private void txtFind_StockKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFind_StockKeyTyped
                if (timer == null) {
        timer = new Timer(DELAY, e -> findByProductItemID());
        timer.setRepeats(false);
        }
        timer.restart();
    }//GEN-LAST:event_txtFind_StockKeyTyped


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane TabProduct;
    private javax.swing.JButton btnCancel_Gallery;
    private javax.swing.JButton btnCancel_Product;
    private javax.swing.JButton btnCancel_Stock;
    private javax.swing.JButton btnCreate_Gallery;
    private javax.swing.JButton btnCreate_Product;
    private javax.swing.JButton btnDelete_Gallery;
    private javax.swing.JButton btnDelete_Product;
    private javax.swing.JButton btnDelete_Stock;
    private javax.swing.JButton btnFindStockID;
    private javax.swing.JButton btnUpdate_Gallery;
    private javax.swing.JButton btnUpdate_Product;
    private javax.swing.JButton btnUpdate_Stock1;
    private javax.swing.JComboBox<String> cboCategory_ID;
    private javax.swing.JComboBox<String> cboProductID_Stock;
    private javax.swing.JComboBox<String> cboProduct_ID_Gallery;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton16;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel100;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane14;
    private javax.swing.JLabel lblThumbnail;
    private com.raven.swing.PanelBorder panelBorder1;
    private com.raven.swing.PanelBorder panelBorder2;
    private javax.swing.JPanel pnAddressForm_list1;
    private javax.swing.JPanel pnGalleryForm;
    private javax.swing.JPanel pnGalleryTable;
    private javax.swing.JPanel pnProductForm;
    private javax.swing.JPanel pnProductTable;
    private javax.swing.JPanel pnStockForm;
    private javax.swing.JPanel pnStockTable;
    private com.raven.swing.PanelBorder pnTable_Gallery;
    private javax.swing.JScrollPane spTable;
    private javax.swing.JScrollPane spTable1;
    private javax.swing.JScrollPane spTable2;
    private com.raven.swing.Table tblGallery;
    private com.raven.swing.Table tblProducts;
    private com.raven.swing.Table tblStock;
    private javax.swing.JTextArea txtDescription;
    private javax.swing.JTextField txtFind_Gallery;
    private javax.swing.JTextField txtFind_Product;
    private javax.swing.JTextField txtFind_Stock;
    private javax.swing.JTextField txtGallery_ID;
    private javax.swing.JTextField txtOriginal_price;
    private javax.swing.JTextField txtPrice;
    private javax.swing.JTextField txtProduct_Id;
    private javax.swing.JTextField txtProduct_Name;
    private javax.swing.JTextField txtQuantity_In_Stock;
    private javax.swing.JTextField txtStock_id;
    // End of variables declaration//GEN-END:variables

}
