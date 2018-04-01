import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

public class ManagerView extends JPanel {
    private JFrame frame;
    private static JTabbedPane tabbedPane;
    private static ManagerOrderView orderView = new ManagerOrderView();
    private static ManagerConfirmOrderView confirmOrderView = new ManagerConfirmOrderView();
    public static ArrayList<Alert> messagesToManager = new ArrayList<>();
    public static ArrayList<Product> products = new ArrayList<>();

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
        ManagerInventoryView inventoryView = new ManagerInventoryView();
        ManagerAlertsView alertsView = new ManagerAlertsView();
        SalesView salesView = new SalesView();
        tabbedPane = new JTabbedPane();
        tabbedPane.add("Order", orderView);
        tabbedPane.add("Inventory", inventoryView);
        tabbedPane.add("Messages", alertsView);
        tabbedPane.add("Sales",salesView);

        // add sub-panels to the main panel
        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
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
            int index = 0;
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
                messagesToManager.add(index, tempManager);
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
            ResultSet rs = statement.executeQuery("select * from products");

            // Putting information from rs into the alert ArrayList
            int index = 0;
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

                products.add(index, temp);
            }
            conn.close();
        } catch(Exception e) {
            System.out.println(e);
        }
    }

    /*
    Changes view of order pane to a confirm order view
     */
    public static void changeToConfirmOrder() {
        tabbedPane.setComponentAt(0, confirmOrderView);
        confirmOrderView.repaint();
    }

    /*
    Changes view of order pane to an order view
     */
    public static void changeToOrder() {
        tabbedPane.setComponentAt(0, orderView);
        orderView.repaint();
    }
}
