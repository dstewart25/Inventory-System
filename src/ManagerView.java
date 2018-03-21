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
    private static ManagerAlertsView alertsView;
    private static ManagerAlertDetailView alertDetailView;
    public static ArrayList<Alert> alerts = new ArrayList<>();

    public ManagerView(JFrame frame) {
        this.frame = frame;
        frame.setSize(600,400);
        setLayout(new BorderLayout());
        initialize();
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    }

    private void initialize() {
        frame.setTitle("MFIS");

        // Importing alerts from the database
        alertsView = new ManagerAlertsView();
        alertDetailView = new ManagerAlertDetailView();

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
        tabbedPane = new JTabbedPane();
        tabbedPane.add("Order", orderView);
        tabbedPane.add("View Inventory", inventoryView);
        tabbedPane.add("View Alerts", alertsView);

        // add sub-panels to the main panel
        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }

    /*
    Connects to database and imports information from the alert table
    Then puts the information into an ArrayList of Alert
     */
    public static void importAlertsFromDatabase() {
        alerts = new ArrayList<>(); // Resetting alerts so there are no duplicates

        try {
            // Connecting to database
            Class.forName("com.mysql.jdbc.Driver");
            DriverManager.setLoginTimeout(10);
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://mfis-db-instance.ch7fymzvlb8l.us-east-1.rds.amazonaws.com:3306/MFIS_DB",
                    "root","password");
            Statement statement = conn.createStatement();

            // Import alert table information into rs
            ResultSet rs = statement.executeQuery("select * from alert");

            // Putting information from rs into the alert ArrayList
            int index = 0;
            while(rs.next()) {
                Alert temp = new Alert(); // temp alert to hold current alert being imported

                /*
                Getting information from rs
                columnIndex: 1-id, 2-subject, 3-body, 4-date
                 */
                temp.setSubject(rs.getString(2));
                temp.setBody(rs.getString(3));
                temp.setTime(rs.getTimestamp(4));

                //
                alerts.add(index, temp);
            }
            conn.close();
        } catch(Exception e) {
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(),
                    "Unable to login.",
                    "Database Error",
                    JOptionPane.WARNING_MESSAGE);
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

    /*
    Changes view of alert detail view to alert view
     */
    public static void changeToAlertView() {
        alertsView = new ManagerAlertsView();
        alertDetailView = new ManagerAlertDetailView();
        tabbedPane.setComponentAt(2, alertsView);
        confirmOrderView.repaint();
    }

    /*
    Changes view of alert view to alert detail view
     */
    public static void changeToAlertDetailView() {
        alertsView = new ManagerAlertsView();
        alertDetailView = new ManagerAlertDetailView();
        tabbedPane.setComponentAt(2, alertDetailView);
        orderView.repaint();
    }
}
