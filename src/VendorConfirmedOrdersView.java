import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VendorConfirmedOrdersView extends JPanel {
    private DefaultTableModel orderTableModel;

    private JPanel confirmedOrdersPanel;
    private JPanel orderDetailPanel;
    private boolean showingConfirmedOrders = false;
    private boolean showingOrderDetail = false;

    private ArrayList<Order> confirmedOrders = new ArrayList<>();

    public VendorConfirmedOrdersView() {
        setLayout(new BorderLayout());
        initialize();
    }

    private void initialize() {
        showConfirmedOrders();
    }

    private void showConfirmedOrders() {
        confirmedOrdersPanel = new JPanel(new BorderLayout());

        // Sub-panel to hold buttons
        JPanel buttonPanel = new JPanel(new BorderLayout());

        importConfirmedOrders();
        JTable orderTable = new JTable(orderTableModel);

        // New Item and Order buttons
        JButton newItemButton = new JButton("Order Details");
        buttonPanel.add(newItemButton, BorderLayout.EAST);
        newItemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    showOrderDetail(confirmedOrders.get(orderTable.getSelectedRow()));
                } catch (ArrayIndexOutOfBoundsException e1) {
                    System.out.println(e1);
                }
            }
        });

        // Setting up table to show inventory information
        orderTable.getColumnModel().getColumn(1).setMinWidth(260);
        orderTable.getColumnModel().getColumn(3).setMinWidth(150);
        orderTable.setShowGrid(true);
        JScrollPane orderScrollPane = new JScrollPane(orderTable,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        orderScrollPane.setPreferredSize(new Dimension(600, 350));

        confirmedOrdersPanel.add(orderScrollPane, BorderLayout.CENTER);
        confirmedOrdersPanel.add(buttonPanel, BorderLayout.SOUTH);

        if (showingConfirmedOrders) {
            remove(confirmedOrdersPanel);
            showingConfirmedOrders = false;
        }
        if (showingOrderDetail) {
            remove(orderDetailPanel);
            showingOrderDetail = false;
        }
        add(confirmedOrdersPanel, BorderLayout.CENTER);
        showingConfirmedOrders = true;
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
                showConfirmedOrders();
            }
        });

        JButton confirmButton = new JButton("Out for Delivery");
        if (order.isDelivered()) {
            confirmButton.setEnabled(false);
        }
        buttonPanel.add(confirmButton, BorderLayout.EAST);
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                order.setDelivered(true);
                DeliverOrder deliverOrder = new DeliverOrder(order);
                deliverOrder.execute();
                while (!deliverOrder.isDone()) {}
                showConfirmedOrders();
            }
        });

        orderDetailPanel.add(orderScrollPane, BorderLayout.CENTER);
        orderDetailPanel.add(buttonPanel, BorderLayout.SOUTH);

        if (showingConfirmedOrders) {
            remove(confirmedOrdersPanel);
            showingConfirmedOrders = false;
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

    private void importConfirmedOrders() {
        confirmedOrders = new ArrayList<>();

        if (!VendorView.orders.isEmpty()) {
            for (int i=0; i<VendorView.orders.size(); i++) {
                if (VendorView.orders.get(i).isConfirmed())
                    confirmedOrders.add(VendorView.orders.get(i));
            }
        }

        String[] column = {"Company", "Address", "Order Number", "Order Date", "Delivered"};
        String[][] data = new String[confirmedOrders.size()][column.length]; // Holds information for columns
        for (int i=0; i<confirmedOrders.size(); i++) {
            data[i][0] = confirmedOrders.get(i).getCompanyName();
            data[i][1] = confirmedOrders.get(i).getAddress();
            data[i][2] = String.valueOf(confirmedOrders.get(i).getOrderNumber());
            data[i][3] = confirmedOrders.get(i).getFormattedTime();
            if (confirmedOrders.get(i).isDelivered())
                data[i][4] = "Yes";
            else
                data[i][4] = "No";
        }
        orderTableModel = new DefaultTableModel(data, column) {
            @Override
            public boolean isCellEditable(int i, int i1) {
                return false;
            }
        };
    }

    private static class DeliverOrder extends SwingWorker<Integer, String> {
        private Order order;

        public DeliverOrder(Order order) {
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
                        "SET delivered=? " +
                        "WHERE order_number=?";
                PreparedStatement orderPS = conn.prepareStatement(query);
                orderPS.setBoolean(1, order.isDelivered());
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
