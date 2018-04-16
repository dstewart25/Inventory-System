import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class VendorCurrentOrdersView extends JPanel {
    private DefaultTableModel orderTableModel;

    private JPanel currentOrdersPanel;
    private JPanel orderDetailPanel;
    private boolean showingCurrentOrders = false;
    private boolean showingOrderDetail = false;

    private ArrayList<Order> orders = new ArrayList<>();

    public VendorCurrentOrdersView() {
        setLayout(new BorderLayout());
        initialize();
    }

    private void initialize() {
        showCurrentOrders();
    }

    private void showCurrentOrders() {
        currentOrdersPanel = new JPanel(new BorderLayout());

        // Sub-panel to hold buttons
        JPanel buttonPanel = new JPanel(new BorderLayout());

        importOrders();
        JTable orderTable = new JTable(orderTableModel);

        // New Item and Order buttons
        JButton newItemButton = new JButton("Order Details");
        buttonPanel.add(newItemButton, BorderLayout.EAST);
        newItemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    showOrderDetail(orders.get(orderTable.getSelectedRow()));
                } catch (ArrayIndexOutOfBoundsException e1) {
                    System.out.println(e1);
                }
            }
        });

        orderTable.getColumnModel().getColumn(1).setMinWidth(260);
        orderTable.getColumnModel().getColumn(3).setMinWidth(150);
        orderTable.setShowGrid(true);
        JScrollPane orderScrollPane = new JScrollPane(orderTable,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        orderScrollPane.setPreferredSize(new Dimension(600, 350));

        currentOrdersPanel.add(orderScrollPane, BorderLayout.CENTER);
        currentOrdersPanel.add(buttonPanel, BorderLayout.SOUTH);

        if (showingOrderDetail) {
            remove(orderDetailPanel);
            showingOrderDetail = false;
        }
        if (showingCurrentOrders) {
            remove(currentOrdersPanel);
            showingCurrentOrders = false;
        }
        add(currentOrdersPanel, BorderLayout.CENTER);
        showingCurrentOrders = true;
        repaint();
        revalidate();
    }

    private void showOrderDetail(Order order) {
        orderDetailPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new BorderLayout());

        HashMap<Product, Integer> orderDetailProducts = order.getProducts();
        // Setting up table to show inventory information
        String[] column = new String[] {"Name", "Pkg Size", "Amount Ordered"}; // Holds names of columns in the table
        String[][] data = new String[orderDetailProducts.size()][column.length]; // Holds information for columns

        int i=0;
        for (Map.Entry<Product, Integer> entry : orderDetailProducts.entrySet()) {
            data[i][0] = entry.getKey().getName();
            data[i][1] = String.valueOf(entry.getKey().getPkgSize());
            data[i][2] = String.valueOf(entry.getValue());
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
        buttonPanel.add(backButton, BorderLayout.WEST);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showCurrentOrders();
            }
        });

        JButton confirmButton = new JButton("Confirm Order");
        if (order.isConfirmed()) {
            confirmButton.setEnabled(false);
        }
        buttonPanel.add(confirmButton, BorderLayout.EAST);
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                order.setConfirmed(true);
                ConfirmOrder confirmOrder = new ConfirmOrder(order);
                confirmOrder.execute();
                while (!confirmOrder.isDone()) {}
                showCurrentOrders();
            }
        });

        orderDetailPanel.add(orderScrollPane, BorderLayout.CENTER);
        orderDetailPanel.add(buttonPanel, BorderLayout.SOUTH);

        if (showingCurrentOrders) {
            remove(currentOrdersPanel);
            showingCurrentOrders = false;
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

    private void importOrders() {
        orders = new ArrayList<>();

        // Setting up table to show inventory information
        String[] column = {"Company", "Address", "Order No", "Order Date", "Confirmed", "Delivered"};
        String[][] data = new String[VendorView.orders.size()][column.length]; // Holds information for columns
        if (!VendorView.orders.isEmpty()) {
            for (int i=0; i<VendorView.orders.size(); i++) {
                orders.add(VendorView.orders.get(i));
                data[i][0] = VendorView.orders.get(i).getCompanyName();
                data[i][1] = VendorView.orders.get(i).getAddress();
                data[i][2] = String.valueOf(VendorView.orders.get(i).getOrderNumber());
                data[i][3] = VendorView.orders.get(i).getFormattedTime();
                if (VendorView.orders.get(i).isConfirmed())
                    data[i][4] = "Yes";
                else
                    data[i][4] = "No";
                if (VendorView.orders.get(i).isDelivered())
                    data[i][5] = "Yes";
                else
                    data[i][5] = "No";
            }
        }
        orderTableModel = new DefaultTableModel(data, column) {
            @Override
            public boolean isCellEditable(int i, int i1) {
                return false;
            }
        };
    }

    private static class ConfirmOrder extends SwingWorker<Integer, String> {
        private Order order;

        public ConfirmOrder(Order order) {
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

                String query = "UPDATE inv_order " +
                        "SET confirmed=? " +
                        "WHERE order_number=?";
                PreparedStatement orderPS = conn.prepareStatement(query);
                orderPS.setBoolean(1, order.isConfirmed());
                orderPS.setInt(2, order.getOrderNumber());
                orderPS.execute();


                conn.close();

                VendorView.importOrdersFromDatabase();

                System.out.println("Order Confirmed");
            } catch(Exception e) {
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(),
                        "Unable to confirm order.",
                        "Database Error",
                        JOptionPane.WARNING_MESSAGE);
                System.out.println(e);
            } finally {
                //loader.setVisible(false);
            }
            return 1;
        }
    }
}
