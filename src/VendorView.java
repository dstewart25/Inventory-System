import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class VendorView extends JPanel {
    private JFrame frame;
    public static ArrayList<Alert> messagesToVendor = new ArrayList<>();

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
}
