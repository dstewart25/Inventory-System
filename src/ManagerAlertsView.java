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
                    try {
                        alertIndex = alertsTable.getSelectedRow();
                        ManagerView.changeToAlertDetailView();
                    } catch (ArrayIndexOutOfBoundsException e1) {
                        System.out.println(e1);
                    }
                }
            }
        });

        JButton deleteAlertButton = new JButton("Delete Alert");
        buttonPanel.add(deleteAlertButton, BorderLayout.WEST);
        deleteAlertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!ManagerView.alerts.isEmpty()) {
                    try {
                        // Removing the row from the table
                        alertIndex = alertsTable.getSelectedRow();
                        alertsTableModel.removeRow(alertIndex);
                        // Choosing the correct row to select now
                        if (alertsTable.getRowCount() == 0)
                            alertsTable.clearSelection();
                        else if (alertsTable.getRowCount() <= alertIndex)
                            alertsTable.setRowSelectionInterval(alertIndex-1, alertIndex-1);
                        else
                            alertsTable.setRowSelectionInterval(alertIndex, alertIndex);
                        repaint(); // refreshing the view

                        // Deleting the alert from the database (in the background)
                        DeleteAlertFromDB deleteAlertFromDB = new DeleteAlertFromDB();
                        deleteAlertFromDB.execute();
                    } catch (ArrayIndexOutOfBoundsException e1) {
                        System.out.println(e1);
                    }
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
                data[i][1] = ManagerView.alerts.get(i).getFormattedTime();
            }
        }
        alertsTableModel = new DefaultTableModel(data, column) {
            @Override
            public boolean isCellEditable(int i, int i1) {
                return false;
            }
        };
    }

    private static class DeleteAlertFromDB extends SwingWorker<Integer, String> {
        @Override
        protected Integer doInBackground() throws Exception {
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
            return 1;
        }
    }
}
