import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public class VendorView extends JPanel {
    private JFrame frame;
    public static ArrayList<Alert> messagesToVendor = new ArrayList<>();
    public static ArrayList<Product> products = new ArrayList<>();
    public static ArrayList<Order> orders = new ArrayList<>();

    public VendorView(JFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());
        initialize();
    }

    private void initialize() {
        frame.setTitle("MFIS");

        // Sub-panel to hold label and log out button
        JPanel topPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("INVENTORY SYSTEM");
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.PLAIN, 24));
        topPanel.add(titleLabel, BorderLayout.WEST);
        JButton logOutButton = new JButton("Log Out");
        topPanel.add(logOutButton, BorderLayout.EAST);
        logOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Removing manager view and showing login screen
                frame.getContentPane().removeAll();
                frame.getContentPane().add(new LoginScreen(frame));
                frame.pack();
                frame.getContentPane().setVisible(true);
                frame.setSize(400,275);
            }
        });

        // Setting up tabbed pane
        VendorCurrentOrdersView currentOrdersView = new VendorCurrentOrdersView();
        VendorConfirmedOrdersView confirmedOrdersView = new VendorConfirmedOrdersView();
        VendorAlertsView createAlertView = new VendorAlertsView();
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("Current Orders", currentOrdersView);
        tabbedPane.add("Confirmed Orders", confirmedOrdersView);
        tabbedPane.add("Messages", createAlertView);

        // add sub-panels to the main panel
        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }

    /*
    Connects to database and imports information from the alert table
    Then puts the information into an ArrayList of Alert
     */
    public static void importMessagesToVendor() {
        messagesToVendor = new ArrayList<>(); // Resetting alerts so there are no duplicates

        try {
            // Connecting to database
            Class.forName("com.mysql.jdbc.Driver");
            DriverManager.setLoginTimeout(10);
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://mfis-db-instance.ch7fymzvlb8l.us-east-1.rds.amazonaws.com:3306/MFIS_DB",
                    "root","password");
            Statement statement = conn.createStatement();

            // Import messagesToVendor table information into rsManager
            ResultSet rsVendor = statement.executeQuery("select * from messagesToVendor");

            // Putting information from rsVendor into the messageToVendor ArrayList
            int index = 0;
            while (rsVendor.next()) {
                Alert tempVendor = new Alert(); // temp alert to hold current alert being imported

            /*
            Getting information from rs
            columnIndex: 1-id, 2-subject, 3-body, 4-date
             */
                tempVendor.setId(rsVendor.getInt(1));
                tempVendor.setSubject(rsVendor.getString(2));
                tempVendor.setBody(rsVendor.getString(3));
                tempVendor.setTime(rsVendor.getTimestamp(4));

                //
                messagesToVendor.add(index, tempVendor);
            }

            conn.close();
        } catch(Exception e) {
            System.out.println(e);
        }
    }

    /*
    Connects to database and imports information from the product table
    Then puts the information into an ArrayList of type Product
     */
    public static void importProductsFromDatabase() {
        products = new ArrayList<>();
        try {
            // Connecting to database
            Class.forName("com.mysql.jdbc.Driver");
            DriverManager.setLoginTimeout(10);
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://mfis-db-instance.ch7fymzvlb8l.us-east-1.rds.amazonaws.com:3306/MFIS_DB",
                    "root","password");
            Statement statement = conn.createStatement();

            // Import alert table information into rs
            ResultSet rs = statement.executeQuery("select p.*, l.current_level" +
                    " from products p" +
                    " inner join inv_levels l ON p.upc = l.upc");

            // Putting information from rs into the alert ArrayList
            //int index = 0;
            while (rs.next()) {
                Product temp = new Product(); // temp alert to hold current alert being imported

                /*
                Getting information from rs
                columnIndex: 1-id, 2-subject, 3-body, 4-date
                 */
                temp.setUpc(rs.getString(1));
                temp.setName(rs.getString(2));
                temp.setPkgSize(rs.getInt(3));
                temp.setPrice(rs.getDouble(4));
                temp.setProdType(rs.getString(5));
                temp.setRequired(rs.getBoolean(6));
                temp.setInventoryLevel(rs.getInt(7));


                products.add(temp);
            }
            conn.close();
        } catch(Exception e) {
            System.out.println(e);
        }
    }

    public static void importOrdersFromDatabase() {
        orders = new ArrayList<>();
        try {
            // Connecting to database
            Class.forName("com.mysql.jdbc.Driver");
            DriverManager.setLoginTimeout(10);
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://mfis-db-instance.ch7fymzvlb8l.us-east-1.rds.amazonaws.com:3306/MFIS_DB",
                    "root","password");
            Statement statement1 = conn.createStatement();
            Statement statement2 = conn.createStatement();

            // Import alert table information into rs
            ResultSet rs = statement1.executeQuery("select * from inv_order;");

            // Putting information from rs into the order ArrayList
            while (rs.next()) {
                Order temp = new Order(); // temp alert to hold current alert being imported

                /*
                Getting information from rs
                columnIndex: 1-id, 2-subject, 3-body, 4-date
                 */
                temp.setOrderNumber(rs.getInt(1));
                temp.setAddress(rs.getString(2));
                temp.setCompanyName(rs.getString(3));
                temp.setTime(rs.getTimestamp(4));
                temp.setConfirmed(rs.getBoolean(5));
                temp.setDelivered(rs.getBoolean(6));

                ResultSet rs2 = statement2.executeQuery("select op.upc, op.amount from order_products op " +
                        "inner join inv_order io ON op.order_number = io.order_number " +
                        "where io.order_number=" + temp.getOrderNumber() +";");
                HashMap<Product, Integer> temp2 = new HashMap<>();
                while (rs2.next()) {
                    for (int i=0; i<products.size(); i++) {
                        if (products.get(i).getUpc().equals(rs2.getString(1))) {
                            temp2.put(products.get(i), rs2.getInt(2));
                        }
                    }
                }

                temp.setProducts(temp2);

                orders.add(temp);
            }

            conn.close();
        } catch(Exception e) {
            System.out.println(e);
        }
    }
}
