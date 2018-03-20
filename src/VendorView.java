import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VendorView extends JPanel {
    private JFrame frame;

    public VendorView(JFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());
        initialize();
    }

    private void initialize() {
        frame.setTitle("Vendor");

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
            }
        });

        // Setting up tabbed pane
        VendorCurrentOrdersView currentOrdersView = new VendorCurrentOrdersView();
        VendorConfirmedOrdersView confirmedOrdersView = new VendorConfirmedOrdersView();
        VendorCreateAlertView createAlertView = new VendorCreateAlertView();
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("Current Orders", currentOrdersView);
        tabbedPane.add("Confirmed Orders", confirmedOrdersView);
        tabbedPane.add("Create Alert", createAlertView);

        // add sub-panels to the main panel
        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }
}
