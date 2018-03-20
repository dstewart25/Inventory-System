import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.*;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;

public class VendorCreateAlertView extends JPanel {
    public VendorCreateAlertView() {
        setLayout(new BorderLayout());
        initialize();
    }

    private void initialize() {
        // Sub-panels to hold different components for the view
        JPanel alertSubjectPanel = new JPanel(new BorderLayout());
        JPanel alertBodyPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new BorderLayout());

        // Creating subject components
        JLabel alertSubjectLabel = new JLabel("Subject:");
        alertSubjectPanel.add(alertSubjectLabel, BorderLayout.NORTH);
        JTextField alertSubjectField = new JTextField();
        alertSubjectField.setColumns(20);
        alertSubjectPanel.add(alertSubjectField, BorderLayout.CENTER);

        // Creating body components
        JLabel alertBodyLabel = new JLabel("Body:");
        alertBodyPanel.add(alertBodyLabel, BorderLayout.NORTH);
        JEditorPane alertBodyPane = new JEditorPane();
        alertBodyPanel.add(alertBodyPane, BorderLayout.CENTER);

        // Buttons for creating an alert
        JButton resetAlert = new JButton("Reset");
        resetAlert.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Erasing text from subject and body fields
                alertSubjectField.setText("");
                alertBodyPane.setText("");
            }
        });
        buttonPanel.add(resetAlert, BorderLayout.WEST);
        JButton sendAlert = new JButton("Send");
        sendAlert.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (alertSubjectField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(JOptionPane.getRootFrame(),
                            "Unable to send alert with no subject.",
                            "No Subject",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                } else if (alertSubjectField.getText().length() > 100) {
                    JOptionPane.showMessageDialog(JOptionPane.getRootFrame(),
                            "Unable to send alert with a subject longer than 100 characters.",
                            "Subject Too Long",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (alertBodyPane.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(JOptionPane.getRootFrame(),
                            "Unable to send alert with no body.",
                            "No Body",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Setting up connection to database
                sendAlertToDatabase(alertSubjectField.getText(), alertBodyPane.getText());
            }
        });
        buttonPanel.add(sendAlert, BorderLayout.EAST);

        // Adding sub-panels to the main panel
        add(alertSubjectPanel, BorderLayout.NORTH);
        add(alertBodyPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void sendAlertToDatabase(String subject, String body) {
        try {
            // Getting current time
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());

            // Connecting to database
            Class.forName("com.mysql.jdbc.Driver");
            DriverManager.setLoginTimeout(15);
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://mfis-db-instance.ch7fymzvlb8l.us-east-1.rds.amazonaws.com:3306/MFIS_DB",
                    "root","password");
            Statement statement = conn.createStatement();
            statement.executeUpdate("INSERT INTO alert (subject, body, date) VALUES ('" +
                    subject + "','" + body +"','" + timestamp + "');");

            // Shows all alert table information
            ResultSet rs = statement.executeQuery("select * from alert");
            while(rs.next())
                System.out.println(rs.getInt(1)+"  "+rs.getString(2)+"  "
                        +rs.getString(3)+"  "+rs.getTime(4));
            conn.close();
        } catch(Exception e) {
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(),
                    "Unable to send alert.",
                    "Error",
                    JOptionPane.WARNING_MESSAGE);
            System.out.println(e);
        }
    }
}
