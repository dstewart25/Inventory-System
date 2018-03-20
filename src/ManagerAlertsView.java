import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ManagerAlertsView extends JPanel {
    private DefaultTableModel alertsTableModel;
    public static int alertIndex;

    public ManagerAlertsView() {
        setLayout(new BorderLayout());
        initialize();
    }

    private void initialize() {
        JPanel buttonPanel = new JPanel(new BorderLayout());

        /*
        Setting up table to show alerts
        The 0 below would be the size of the primary key in the inventory database
         */
        importAlertsToTable(); // Importing alerts from an array of all the alerts
        JTable alertsTable = new JTable(alertsTableModel);
        JScrollPane alertScrollPane = new JScrollPane(alertsTable,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        alertScrollPane.setPreferredSize(new Dimension(600, 350));

        JButton viewAlertButton = new JButton("View Alert");
        buttonPanel.add(viewAlertButton, BorderLayout.EAST);
        viewAlertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                alertIndex = alertsTable.getSelectedRow();
                ManagerView.changeToAlertDetailView();
            }
        });

        add(alertScrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void importAlertsToTable() {
        String[] column = {"Subject", "Date Received"};
        String[][] data = new String[ManagerView.alerts.size()][column.length]; // Holds information for columns
        if (ManagerView.alerts.size() != 0) {
            for (int i = 0; i < ManagerView.alerts.size(); i++) {
                data[i][0] = ManagerView.alerts.get(i).getSubject();
                data[i][1] = ManagerView.alerts.get(i).getTime().toString();
            }
        }
        alertsTableModel = new DefaultTableModel(data, column);
    }
}
