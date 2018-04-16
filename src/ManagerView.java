import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ManagerView extends JPanel {
    private JFrame frame;
    private static JTabbedPane tabbedPane;
    private static ManagerOrderView orderView = new ManagerOrderView();
    public static ArrayList<Alert> messagesToManager = new ArrayList<>();
    public static ArrayList<Product> products = new ArrayList<>();
    public static ArrayList<Order> orders = new ArrayList<>();

    public ManagerView(JFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());
        initialize();
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
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
        ManagerInventoryView inventoryView = new ManagerInventoryView(0);
        ManagerAlertsView alertsView = new ManagerAlertsView();
        SalesView salesView = new SalesView();
        tabbedPane = new JTabbedPane();
        tabbedPane.add("Order", orderView);
        tabbedPane.add("Inventory", inventoryView);
        tabbedPane.add("Sales", salesView);
        tabbedPane.add("Messages", alertsView);

        // add sub-panels to the main panel
        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }


    public static void changeToAllCategory() {
        ManagerInventoryView allInvView = new ManagerInventoryView(0);
        tabbedPane.setComponentAt(1, allInvView);
    }

    public static void changeToFoodCategory() {
        ManagerInventoryView foodInvView = new ManagerInventoryView(3);
        tabbedPane.setComponentAt(1, foodInvView);
    }

    public static void changeToNonAlcoholicCategory() {
        ManagerInventoryView nonAlcInvView = new ManagerInventoryView(4);
        tabbedPane.setComponentAt(1, nonAlcInvView);
    }

    public static void changeToAlcoholicCategory() {
        ManagerInventoryView alcInvView = new ManagerInventoryView(5);
        tabbedPane.setComponentAt(1, alcInvView);
    }

    /*
    Connects to database and imports information from the alert table
    Then puts the information into an ArrayList of Alert
     */
    public static void importMessagesToManager() {
        messagesToManager = new ArrayList<>(); // Resetting alerts so there are no duplicates

        try {
            // Connecting to database
            Class.forName("com.mysql.jdbc.Driver");
            DriverManager.setLoginTimeout(10);
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://mfis-db-instance.ch7fymzvlb8l.us-east-1.rds.amazonaws.com:3306/MFIS_DB",
                    "root","password");
            Statement statement = conn.createStatement();

            // Import messagesToManager table information into rsManager
            ResultSet rsManager = statement.executeQuery("select * from messagesToManager");

            // Putting information from rsManager into the messagesToManager ArrayList
            //int index = 0;
            while (rsManager.next()) {
                Alert tempManager = new Alert(); // temp alert to hold current alert being imported

            /*
            Getting information from rs
            columnIndex: 1-id, 2-subject, 3-body, 4-date
             */
                tempManager.setId(rsManager.getInt(1));
                tempManager.setSubject(rsManager.getString(2));
                tempManager.setBody(rsManager.getString(3));
                tempManager.setTime(rsManager.getTimestamp(4));

                //
                messagesToManager.add(tempManager);
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
        products = new ArrayList<>();
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
                temp.setConfirmed(rs.getBoolean(4));
                temp.setDelivered(rs.getBoolean(5));

                ResultSet rs2 = statement2.executeQuery("select * from order_products " +
                        "inner join inv_order io ON order_products.order_number = io.order_number " +
                        "where io.order_number=" + temp.getOrderNumber() +";");
                HashMap<Product, Integer> temp2 = new HashMap<>();
                while (rs2.next()) {
                    for (int i=0; i<products.size(); i++) {
                        if (products.get(i).getUpc().equals(rs.getString(1))) {
                            temp2.put(products.get(i), rs.getInt(3));
                        }
                    }
                }

                orders.add(temp);
            }

            conn.close();
        } catch(Exception e) {
            System.out.println(e);
        }
    }
}
