import sun.rmi.runtime.Log;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.text.NumberFormat;
import java.util.*;

public class ManagerOrderView extends JPanel {
    // Used to view order information
    private JPanel newOrderPanel;
    private JPanel newItemPanel;
    private JPanel pastOrdersPanel;
    private JPanel confirmNewOrderPanel;
    private JPanel orderDetailPanel;
    private boolean showingCreateNewOrder = false;
    private boolean showingViewPastOrders = false;
    private boolean showingConfirmNewOrder = false;
    private boolean showingAddNewItem = false;
    private boolean showingOrderDetail = false;

    // Holding information for this gas station
    private String address = "1234 Gas Station Blvd, Fort Myers, FL 33928";
    private String companyName = "Station 1";
    private static HashMap<Product, Integer> orderProducts = new HashMap<>();
    private Product temp = new Product();
    private Order newOrder = new Order();
    private ArrayList<Order> pastOrders = new ArrayList<>();

    // Information for orders
    private DefaultTableModel orderTableModel;
    private DefaultTableModel pastOrderTableModel;
    private JComboBox itemComboBox = new JComboBox();

    // Holds item names for inputting a new item
    private ArrayList<String> items;

    public ManagerOrderView() {
        setLayout(new BorderLayout());
        initialize();
    }

    private void initialize() {
        JPanel orderOptionsPanel = new JPanel(new GridLayout(2, 1));
        JPanel newOrder = new JPanel(new BorderLayout()); // holds icon and label for new order button
        JPanel pastOrders = new JPanel(new BorderLayout());

        /* Setting up images for buttons */
        BufferedImage newOrderBI = new BufferedImage(512, 512, BufferedImage.TYPE_4BYTE_ABGR);
        try {
            newOrderBI = ImageIO.read(new File("Assets/new.png"));
        } catch (IOException e) {
            System.out.println(e);
        }
        ImageIcon newOrderII = new ImageIcon(newOrderBI);
        Image newOrderImage = newOrderII.getImage();
        newOrderImage = newOrderImage.getScaledInstance(128,128,Image.SCALE_SMOOTH);

        BufferedImage pastOrderBI = new BufferedImage(512, 512, BufferedImage.TYPE_4BYTE_ABGR);
        try {
            pastOrderBI = ImageIO.read(new File("Assets/history.png"));
        } catch (IOException e) {
            System.out.println(e);
        }
        ImageIcon pastOrderII = new ImageIcon(pastOrderBI);
        Image pastOrderImage = pastOrderII.getImage();
        pastOrderImage = pastOrderImage.getScaledInstance(128,128,Image.SCALE_SMOOTH);

        showCreateNewOrder();

        /*
        Adding buttons for creating a new order and viewing past orders
         */
        JButton newOrderButton = new JButton();
        newOrderButton.setLayout(new BorderLayout());
        newOrderButton.add(BorderLayout.CENTER, new JLabel(new ImageIcon(newOrderImage)));
        JLabel newOrderLabel = new JLabel("New Order");
        newOrderLabel.setHorizontalAlignment(JLabel.CENTER);
        newOrderButton.add(BorderLayout.SOUTH, newOrderLabel);
        newOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!showingCreateNewOrder) {
                    showCreateNewOrder();
                }
            }
        });
        orderOptionsPanel.add(newOrderButton);
        JButton pastOrdersButton = new JButton();
        pastOrdersButton.setLayout(new BorderLayout());
        pastOrdersButton.add(BorderLayout.CENTER, new JLabel(new ImageIcon(pastOrderImage)));
        JLabel pastOrdersLabel = new JLabel("Past Orders");
        pastOrdersLabel.setHorizontalAlignment(JLabel.CENTER);
        pastOrdersButton.add(BorderLayout.SOUTH, pastOrdersLabel);
        pastOrdersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!showingViewPastOrders) {
                    showViewPastOrders();
                }
            }
        });
        orderOptionsPanel.add(pastOrdersButton);

        add(orderOptionsPanel, BorderLayout.WEST);
    }

    private void showCreateNewOrder() {
        newOrderPanel = new JPanel(new BorderLayout());

        // Sub-panel to hold buttons
        JPanel buttonPanel = new JPanel(new BorderLayout());
        JPanel leftButtonPanel = new JPanel(new GridLayout(1,3));
        importOrderInformation();
        JTextField field = createTextField();
        final TableCellEditor editor = new DefaultCellEditor(field);
        JTable orderTable = new JTable(orderTableModel) {
            @Override
            public TableCellEditor getCellEditor(int row, int col) {
                int modelColumn = convertColumnIndexToModel(col);

                if (modelColumn == 3)
                    return editor;
                else
                    return super.getCellEditor(row, col);
            }
        };
        orderTable.getColumnModel().getColumn(0).setMinWidth(200);
        orderTableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                // Changing saved amount from table
                //System.out.println(orderTable.getValueAt(e.getLastRow(), e.getColumn()));
                for (Map.Entry<Product, Integer> entry : orderProducts.entrySet()) {
                    if (entry.getKey().getName().equals(orderTable.getValueAt(e.getLastRow(), 0))) {
                        orderProducts.replace(entry.getKey(), Integer.valueOf(String.valueOf(orderTable.getValueAt(e.getLastRow(), e.getColumn()))));
                        //System.out.println("success");
                    }
                }
            }
        });

        // New Item and Order buttons
        JButton newItemButton = new JButton("New Item");
        leftButtonPanel.add(newItemButton, BorderLayout.WEST);
        newItemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddNewItem();
            }
        });
        JButton deleteItemButton = new JButton("Delete Item");
        leftButtonPanel.add(deleteItemButton);
        deleteItemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String name = String.valueOf(orderTable.getValueAt(orderTable.getSelectedRow(), 0));
                    Iterator it = orderProducts.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry)it.next();
                        Product temp = (Product) pair.getKey();
                        if (temp.getName().equals(name)) {
                            orderProducts.remove(temp);
                        }
                    }
                    showAddNewItem();
                    showCreateNewOrder();
                } catch (ArrayIndexOutOfBoundsException e1) {
                    System.out.println(e1);
                }
            }
        });
        JButton clearOrderButton = new JButton("Clear Order");
        leftButtonPanel.add(clearOrderButton);
        clearOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearOrder();
            }
        });

        JButton orderButton = new JButton("Order");
        buttonPanel.add(orderButton, BorderLayout.EAST);
        orderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!orderProducts.isEmpty()) {
                    changeToConfirmOrder();
                }
            }
        });
        buttonPanel.add(leftButtonPanel, BorderLayout.WEST);

        // Setting up table to show inventory information
        orderTable.setShowGrid(true);
        JScrollPane orderScrollPane = new JScrollPane(orderTable,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        orderScrollPane.setPreferredSize(new Dimension(600, 350));

        newOrderPanel.add(orderScrollPane, BorderLayout.CENTER);
        newOrderPanel.add(buttonPanel, BorderLayout.SOUTH);

        if (showingViewPastOrders) {
            remove(pastOrdersPanel);
            showingViewPastOrders = false;
        }
        if (showingConfirmNewOrder) {
            remove(confirmNewOrderPanel);
            showingConfirmNewOrder = false;
        }
        if (showingAddNewItem) {
            remove(newItemPanel);
            showingAddNewItem = false;
        }
        if (showingCreateNewOrder) {
            remove(newOrderPanel);
            showingCreateNewOrder = false;
        }
        if (showingOrderDetail) {
            remove(orderDetailPanel);
            showingOrderDetail = false;
        }
        add(newOrderPanel, BorderLayout.CENTER);
        showingCreateNewOrder = true;
        repaint();
        revalidate();
    }

    private void changeToConfirmOrder() {
        confirmNewOrderPanel = new JPanel(new BorderLayout());

        // Sub-panel to hold buttons
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        // Title label
        JLabel titleLabel = new JLabel("Review Order");
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.PLAIN, 18));
        confirmNewOrderPanel.add(titleLabel, BorderLayout.NORTH);

        // Total labels
        double total = 0;
        Iterator it = orderProducts.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            Product temp = (Product) pair.getKey();
            total += temp.getPrice() * Double.valueOf(String.valueOf(pair.getValue()));
        }
        JLabel totalLabel = new JLabel("Total: $" + total);
        bottomPanel.add(totalLabel, BorderLayout.WEST);

        // New Item and Order buttons
        JButton backButton = new JButton("Back");
        buttonPanel.add(backButton);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showCreateNewOrder();
            }
        });
        JButton confirmButton = new JButton("Confirm");
        buttonPanel.add(confirmButton);
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                newOrder.setProducts(orderProducts);
                newOrder.setCompanyName(companyName);
                newOrder.setAddress(address);
                newOrder.setTime(timestamp);
                newOrder.setConfirmed(false);
                newOrder.setDelivered(false);
                SendOrderToDatabase sendOrderToDatabase = new SendOrderToDatabase(newOrder);
                sendOrderToDatabase.execute();
                while (!sendOrderToDatabase.isDone()) {}
                System.out.println("Clearing Order");
                clearOrder();
                importPastOrders();
            }
        });

        importConfirmOrderInformation();
        JTable orderTable = new JTable(orderTableModel);
        orderTable.getColumnModel().getColumn(0).setMinWidth(200);
        orderTable.setShowGrid(true);
        JScrollPane orderScrollPane = new JScrollPane(orderTable,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        orderScrollPane.setPreferredSize(new Dimension(600, 350));

        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        confirmNewOrderPanel.add(orderScrollPane, BorderLayout.CENTER);
        confirmNewOrderPanel.add(bottomPanel, BorderLayout.SOUTH);

        if (showingViewPastOrders) {
            remove(pastOrdersPanel);
            showingViewPastOrders = false;
        }
        if (showingCreateNewOrder) {
            remove(newOrderPanel);
            showingCreateNewOrder = false;
        }
        if (showingAddNewItem) {
            remove(newItemPanel);
            showingAddNewItem = false;
        }
        if (showingConfirmNewOrder) {
            remove(confirmNewOrderPanel);
            showingConfirmNewOrder = false;
        }
        if (showingOrderDetail) {
            remove(orderDetailPanel);
            showingOrderDetail = false;
        }
        add(confirmNewOrderPanel, BorderLayout.CENTER);
        showingConfirmNewOrder = true;
        repaint();
        revalidate();
    }

    private void showViewPastOrders() {
        pastOrdersPanel = new JPanel(new BorderLayout());

        // Sub-panel to hold buttons
        JPanel buttonPanel = new JPanel(new BorderLayout());

        importPastOrders();
        JTable pastOrderTable = new JTable(pastOrderTableModel);

        // New Item and Order buttons
        JButton newItemButton = new JButton("Order Details");
        buttonPanel.add(newItemButton, BorderLayout.EAST);
        newItemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    showOrderDetail(pastOrders.get(pastOrderTable.getSelectedRow()));
                } catch (ArrayIndexOutOfBoundsException e1) {
                    System.out.println(e1);
                }
            }
        });

        // Setting up table to show inventory information
        pastOrderTable.setShowGrid(true);
        pastOrderTable.getColumnModel().getColumn(1).setMinWidth(200);
        JScrollPane pastOrderScrollPane = new JScrollPane(pastOrderTable,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        pastOrderScrollPane.setPreferredSize(new Dimension(600, 350));

        pastOrdersPanel.add(pastOrderScrollPane, BorderLayout.CENTER);
        pastOrdersPanel.add(buttonPanel, BorderLayout.SOUTH);

        if (showingCreateNewOrder) {
            remove(newOrderPanel);
            showingCreateNewOrder = false;
        }
        if (showingConfirmNewOrder) {
            remove(confirmNewOrderPanel);
            showingConfirmNewOrder = false;
        }
        if (showingAddNewItem) {
            remove(newItemPanel);
            showingAddNewItem = false;
        }
        if (showingViewPastOrders) {
            remove(pastOrdersPanel);
            showingViewPastOrders = false;
        }
        if (showingOrderDetail) {
            remove(orderDetailPanel);
            showingOrderDetail = false;
        }
        add(pastOrdersPanel, BorderLayout.CENTER);
        showingViewPastOrders = true;
        repaint();
        revalidate();
    }

    private void showOrderDetail(Order order) {
        orderDetailPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new BorderLayout());

        HashMap<Product, Integer> orderDetailProducts = order.getProducts();
        // Setting up table to show inventory information
        String[] column = new String[] {"Name", "Pkg Size", "Current Inventory", "Amount Ordered"}; // Holds names of columns in the table
        String[][] data = new String[orderDetailProducts.size()][column.length]; // Holds information for columns
        Iterator it = orderDetailProducts.entrySet().iterator();
        int i=0;
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            Product temp = (Product) pair.getKey();
            data[i][0] = temp.getName();
            data[i][1] = String.valueOf(temp.getPkgSize());
            data[i][2] = String.valueOf(temp.getInventoryLevel());
            data[i][3] = String.valueOf(pair.getValue());
            i++;
        }
        DefaultTableModel orderDetailTableModel = new DefaultTableModel(data, column) {
            @Override
            public boolean isCellEditable(int i, int i1) {
                return false;
            }
        };
        JTable orderDetailTable = new JTable(orderDetailTableModel);
        orderDetailTable.setShowGrid(true);
        JScrollPane orderScrollPane = new JScrollPane(orderDetailTable,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        orderScrollPane.setPreferredSize(new Dimension(600, 350));

        JButton backButton = new JButton("Back");
        buttonPanel.add(backButton, BorderLayout.EAST);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showViewPastOrders();
            }
        });

        orderDetailPanel.add(orderScrollPane, BorderLayout.CENTER);
        orderDetailPanel.add(buttonPanel, BorderLayout.SOUTH);

        if (showingCreateNewOrder) {
            remove(newOrderPanel);
            showingCreateNewOrder = false;
        }
        if (showingConfirmNewOrder) {
            remove(confirmNewOrderPanel);
            showingConfirmNewOrder = false;
        }
        if (showingAddNewItem) {
            remove(newItemPanel);
            showingAddNewItem = false;
        }
        if (showingViewPastOrders) {
            remove(pastOrdersPanel);
            showingViewPastOrders = false;
        }
        if (showingOrderDetail) {
            remove(orderDetailPanel);
            showingOrderDetail = false;
        }
        add(orderDetailPanel, BorderLayout.CENTER);
        showingOrderDetail = true;
        repaint();
        revalidate();
    }

    private void showAddNewItem() {
        newItemPanel = new JPanel(new BorderLayout());
        JPanel itemSelectionPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        importAllItems();
        itemComboBox.setModel(new DefaultComboBoxModel(items.toArray()));
        JTextField amountField = new JTextField();

        JPanel buttonPanel = new JPanel(new BorderLayout());
        JButton addButton = new JButton("Add Item");

        String[] categories = {"Select a Category", "All", "Food", "Non-Alcoholic", "Alcohol"};
        JLabel categoryLabel = new JLabel("Category:");
        JComboBox categoryComboBox = new JComboBox(categories);
        categoryComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int categoryIndex = categoryComboBox.getSelectedIndex();
                switch(categoryIndex) {
                    case 1:
                        importAllItems();
                        itemComboBox.setEnabled(true);
                        break;
                    case 2:
                        importFoodItems();
                        itemComboBox.setEnabled(true);
                        break;
                    case 3:
                        importNonAlcoholicItems();
                        itemComboBox.setEnabled(true);
                        break;
                    case 4:
                        importAlcoholItems();
                        itemComboBox.setEnabled(true);
                        break;
                    default:
                        itemComboBox.setEnabled(false);
                        break;
                }
            }
        });

        JLabel itemLabel = new JLabel("Item:");
        itemComboBox.setEnabled(false);
        itemComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int itemIndex = itemComboBox.getSelectedIndex();
                    switch (itemIndex) {
                        case 0:
                            amountField.setEnabled(false);
                            addButton.setEnabled(false);
                            break;
                        default:
                            amountField.setEnabled(true);
                            addButton.setEnabled(true);
                            for (int i=0; i<ManagerView.products.size(); i++) {
                                if (ManagerView.products.get(i).getName().equals(items.get(itemIndex))) {
                                    temp = ManagerView.products.get(i);
                                    //System.out.println(items.get(itemIndex));
                                }
                            }
                            break;
                    }
                } catch (ArrayIndexOutOfBoundsException e1) {}
            }
        });

        JLabel amountLabel = new JLabel("Amount:");
        amountField.setEnabled(false);
        amountField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!((c >= '0') && (c <= '9') ||
                        (c == KeyEvent.VK_BACK_SPACE) ||
                        (c == KeyEvent.VK_DELETE))) {
                    getToolkit().beep();
                    e.consume();
                }
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showCreateNewOrder();
            }
        });
        buttonPanel.add(cancelButton, BorderLayout.WEST);

        addButton.setEnabled(false);
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (amountField.getText().equals("")) {
                    JOptionPane.showMessageDialog(JOptionPane.getRootFrame(),
                            "Please enter an amount to be ordered.",
                            "No Amount",
                            JOptionPane.WARNING_MESSAGE);
                } else {
                    orderProducts.put(temp, Integer.valueOf(amountField.getText()));
                    showCreateNewOrder();
                }
            }
        });
        buttonPanel.add(addButton, BorderLayout.EAST);

        c.gridx=0;
        c.gridy=0;
        itemSelectionPanel.add(categoryLabel, c);
        c.gridx=1;
        c.gridy=0;
        itemSelectionPanel.add(categoryComboBox, c);
        c.gridx=0;
        c.gridy=1;
        itemSelectionPanel.add(itemLabel, c);
        c.gridx=1;
        c.gridy=1;
        itemSelectionPanel.add(itemComboBox, c);
        c.gridx=0;
        c.gridy=2;
        itemSelectionPanel.add(amountLabel, c);
        c.gridx=1;
        c.gridy=2;
        amountField.setColumns(20);
        itemSelectionPanel.add(amountField, c);

        newItemPanel.add(itemSelectionPanel, BorderLayout.CENTER);
        newItemPanel.add(buttonPanel, BorderLayout.SOUTH);

        if (showingCreateNewOrder) {
            remove(newOrderPanel);
            showingCreateNewOrder = false;
        }
        if (showingConfirmNewOrder) {
            remove(confirmNewOrderPanel);
            showingConfirmNewOrder = false;
        }
        if (showingViewPastOrders) {
            remove(pastOrdersPanel);
            showingViewPastOrders = false;
        }
        if (showingAddNewItem) {
            remove(newItemPanel);
            showingAddNewItem = false;
        }
        if (showingOrderDetail) {
            remove(orderDetailPanel);
            showingOrderDetail = false;
        }
        add(newItemPanel, BorderLayout.CENTER);
        showingAddNewItem = true;
        repaint();
        revalidate();
    }

    private void importAllItems() {
        items = new ArrayList<>();
        items.add("Select an Item");
        itemComboBox.removeAllItems();
        itemComboBox.addItem("Select an Item");
        for (int i=0; i<ManagerView.products.size(); i++) {
            items.add(ManagerView.products.get(i).getName());
            itemComboBox.addItem(ManagerView.products.get(i).getName());
        }
    }

    private void importFoodItems() {
        items = new ArrayList<>();
        items.add("Select an Item");
        itemComboBox.removeAllItems();
        itemComboBox.addItem("Select an Item");
        for (int i=0; i<ManagerView.products.size(); i++) {
            if (ManagerView.products.get(i).getProdType().equals("Food")) {
                items.add(ManagerView.products.get(i).getName());
                itemComboBox.addItem(ManagerView.products.get(i).getName());
            }
        }
    }

    private void importNonAlcoholicItems() {
        items = new ArrayList<>();
        items.add("Select an Item");
        itemComboBox.removeAllItems();
        itemComboBox.addItem("Select an Item");
        for (int i=0; i<ManagerView.products.size(); i++) {
            if (ManagerView.products.get(i).getProdType().equals("Non-Alcoholic")) {
                items.add(ManagerView.products.get(i).getName());
                itemComboBox.addItem(ManagerView.products.get(i).getName());
            }
        }
    }

    private void importAlcoholItems() {
        items = new ArrayList<>();
        items.add("Select an Item");
        itemComboBox.removeAllItems();
        itemComboBox.addItem("Select an Item");
        for (int i=0; i<ManagerView.products.size(); i++) {
            if (ManagerView.products.get(i).getProdType().equals("Alcohol")) {
                items.add(ManagerView.products.get(i).getName());
                itemComboBox.addItem(ManagerView.products.get(i).getName());
            }
        }
    }

    private void importOrderInformation() {
        // Setting up table to show inventory information
        String[] column = new String[] {"Name", "Pkg Size", "Current Level", "Amount", "Price"}; // Holds names of columns in the table
        String[][] data = new String[orderProducts.size()][column.length]; // Holds information for columns
        Iterator it = orderProducts.entrySet().iterator();
        int i=0;
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            Product temp = (Product) pair.getKey();
            data[i][0] = temp.getName();
            data[i][1] = String.valueOf(temp.getPkgSize());
            data[i][2] = String.valueOf(temp.getInventoryLevel());
            data[i][3] = String.valueOf(pair.getValue());
            data[i][4] = String.valueOf(temp.getPrice());
            i++;
        }
        orderTableModel = new DefaultTableModel(data, column) {
            @Override
            public boolean isCellEditable(int row, int col) {
                switch (col) {
                    case 3:
                        return true;
                    default:
                        return false;
                }
            }
        };
    }

    private void importConfirmOrderInformation() {
        // Setting up table to show inventory information
        String[] column = new String[] {"Name", "Pkg Size", "Current Level", "Amount", "Price"}; // Holds names of columns in the table
        String[][] data = new String[orderProducts.size()][column.length]; // Holds information for columns
        Iterator it = orderProducts.entrySet().iterator();
        int i=0;
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            Product temp = (Product) pair.getKey();
            data[i][0] = temp.getName();
            data[i][1] = String.valueOf(temp.getPkgSize());
            data[i][2] = String.valueOf(temp.getInventoryLevel());
            data[i][3] = String.valueOf(pair.getValue());
            data[i][4] = String.valueOf(temp.getPrice());
            i++;
        }
        orderTableModel = new DefaultTableModel(data, column) {
            @Override
            public boolean isCellEditable(int i, int i1) {
                return false;
            }
        };
    }

    private void importPastOrders() {
        pastOrders = new ArrayList<>();

        String[] column = {"Order Number", "Order Date", "Confirmed", "Delivered"};
        String[][] data = new String[ManagerView.orders.size()][column.length]; // Holds information for columns
        if (!ManagerView.orders.isEmpty()) {
            for (int i=0; i<ManagerView.orders.size(); i++) {
                pastOrders.add(ManagerView.orders.get(i));
                data[i][0] = String.valueOf(ManagerView.orders.get(i).getOrderNumber());
                data[i][1] = ManagerView.orders.get(i).getFormattedTime();
                if (ManagerView.orders.get(i).isConfirmed())
                    data[i][2] = "Yes";
                else
                    data[i][2] = "No";
                if (ManagerView.orders.get(i).isDelivered())
                    data[i][3] = "Yes";
                else
                    data[i][3] = "No";
            }
        }
        pastOrderTableModel = new DefaultTableModel(data, column) {
            @Override
            public boolean isCellEditable(int i, int i1) {
                return false;
            }
        };
    }

    private static class SendOrderToDatabase extends SwingWorker<Integer, String> {
        private Order order;

        public SendOrderToDatabase(Order order) {
            this.order = order;
        }

        @Override
        protected Integer doInBackground() throws Exception {
            try {
                // Connecting to database
                Class.forName("com.mysql.jdbc.Driver");
                DriverManager.setLoginTimeout(15);
                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://mfis-db-instance.ch7fymzvlb8l.us-east-1.rds.amazonaws.com:3306/MFIS_DB",
                        "root","password");
                Statement statement = conn.createStatement();

                String queryOrder = "INSERT INTO inv_order (address, company_name, date, confirmed, delivered) " +
                        "VALUES (?,?,?,?,?);";
                PreparedStatement orderPS = conn.prepareStatement(queryOrder);
                orderPS.setString(1, order.getAddress());
                orderPS.setString(2, order.getCompanyName());
                orderPS.setTimestamp(3, order.getTime());
                orderPS.setBoolean(4, order.isConfirmed());
                orderPS.setBoolean(5, order.isDelivered());
                orderPS.execute();

                String queryProduct = "INSERT INTO order_products (order_number, upc, amount) " +
                        "VALUES (?,?,?)";
                ResultSet rs = statement.executeQuery("SELECT * from inv_order ORDER BY order_number DESC LIMIT 1;");
                rs.next();
                Iterator it = orderProducts.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry)it.next();
                    Product temp = (Product) pair.getKey();
                    PreparedStatement productsPS = conn.prepareStatement(queryProduct);
                    productsPS.setInt(1, rs.getInt(1));
                    productsPS.setString(2, temp.getUpc());
                    productsPS.setInt(3, Integer.valueOf(String.valueOf(pair.getValue())));
                    productsPS.execute();
                }
                conn.close();

                ManagerView.importOrdersFromDatabase();

                System.out.println("Order Sent");
            } catch(Exception e) {
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(),
                        "Unable to send order.",
                        "Error",
                        JOptionPane.WARNING_MESSAGE);
                System.out.println(e);
            } finally {
                //loader.setVisible(false);
            }
            return 1;
        }
    }

    private void clearOrder() {
        orderProducts = new HashMap<>();
        showAddNewItem();
        showCreateNewOrder();
    }

    private JTextField createTextField() {
        final JTextField field = new JTextField();
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int off, String str, AttributeSet attr)
                    throws BadLocationException {
                int length = field.getDocument().getLength();
                if (length + str.length() <= 1) {
                    fb.insertString(off, str.replaceAll("[^1-9]", ""), attr);  // remove non-digits
                }
            }

            @Override
            public void replace(FilterBypass fb, int off, int len, String str, AttributeSet attr)
                    throws BadLocationException {
                int length = field.getDocument().getLength();
                if (length + str.length() <= 1) {
                    fb.replace(off, len, str.replaceAll("[^1-9]", ""), attr);  // remove non-digits
                }
            }
        });
        return field;
    }
}
