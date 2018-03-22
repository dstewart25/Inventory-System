import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class ManagerAlertsView extends JPanel {
    private DefaultTableModel alertsTableModel;
    private JTable alertsTable;
    public static int alertIndex;

    public ManagerAlertsView() {
        setLayout(new BorderLayout());
        initialize();
    }

    private void initialize() {
        JPanel buttonPanel = new JPanel(new BorderLayout());

        /*
        Setting up table to show alerts
         */
        importAlertsToTable(); // Importing alerts from an array of all the alerts
        alertsTable = new JTable(alertsTableModel);
        JScrollPane alertScrollPane = new JScrollPane(alertsTable,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        alertScrollPane.setPreferredSize(new Dimension(600, 350));

        JButton viewAlertButton = new JButton("View Alert");
        buttonPanel.add(viewAlertButton, BorderLayout.EAST);
        viewAlertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!ManagerView.alerts.isEmpty()) {
                    alertIndex = alertsTable.getSelectedRow();
                    ManagerView.changeToAlertDetailView();
                }
            }
        });

        JButton deleteAlertButton = new JButton("Delete Alert");
        buttonPanel.add(deleteAlertButton, BorderLayout.WEST);
        deleteAlertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!ManagerView.alerts.isEmpty()) {
                    alertIndex = alertsTable.getSelectedRow();
                    deleteSelectedAlert();
                }
            }
        });

        add(alertScrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void importAlertsToTable() {
        String[] column = {"Subject", "Date Received"};
        String[][] data = new String[ManagerView.alerts.size()][column.length]; // Holds information for columns
        if (!ManagerView.alerts.isEmpty()) {
            for (int i = 0; i < ManagerView.alerts.size(); i++) {
                data[i][0] = ManagerView.alerts.get(i).getSubject();
                data[i][1] = ManagerView.alerts.get(i).getTime().toString();
            }
        }
        alertsTableModel = new DefaultTableModel(data, column) {
            @Override
            public boolean isCellEditable(int i, int i1) {
                return false;
            }
        };
    }

    private void deleteSelectedAlert() {
        // Removing the row from the table
        alertsTableModel.removeRow(alertIndex);
        alertsTable.clearSelection();

        // Deleting alert from the database
        try {
            // Connecting to database
            Class.forName("com.mysql.jdbc.Driver");
            DriverManager.setLoginTimeout(10);
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://mfis-db-instance.ch7fymzvlb8l.us-east-1.rds.amazonaws.com:3306/MFIS_DB",
                    "root","password");
            Statement statement = conn.createStatement();
            statement.executeUpdate("DELETE FROM alert " +
                    "WHERE id=" + ManagerView.alerts.get(alertIndex).getId() + ";");
            conn.close();
        } catch(Exception e) {
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(),
                    "Unable to delete alert.",
                    "Database Error",
                    JOptionPane.WARNING_MESSAGE);
            System.out.println(e);
        }

        // Reimporting table information from the database
        ManagerView.importAlertsFromDatabase();
        importAlertsToTable();
    }
}
