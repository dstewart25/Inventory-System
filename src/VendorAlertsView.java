import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.*;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class VendorAlertsView extends JLayeredPane {
    private DefaultTableModel alertsTableModel;
    private JTable alertsTable;
    public static int alertIndex;
    private boolean showingViewCreate = false;
    private boolean showingViewDetails = false;
    private boolean showingViewMessages = false;
    private JPanel viewMessagesPanel;
    private JPanel viewMessageDetailPanel;
    private JPanel createMessagePanel;
    private final static ImageIcon loading = new ImageIcon("Assets/ajax-loader.gif");
    private final static JLabel loader = new JLabel("loading... ", loading, JLabel.CENTER);

    public VendorAlertsView() {
        setLayout(new BorderLayout());
        initialize();
    }

    private void initialize() {
        // Holds button on left side to show/create messages
        JPanel alertsOptionPanel = new JPanel(new GridLayout(2,1));

        showViewMessages();

        /* Setting up images for buttons */
        BufferedImage createMessageBI = new BufferedImage(512, 512, BufferedImage.TYPE_4BYTE_ABGR);
        try {
            createMessageBI = ImageIO.read(new File("Assets/create.png"));
        } catch (IOException e) {
            System.out.println(e);
        }
        ImageIcon createMessageII = new ImageIcon(createMessageBI);
        Image createMessageImage = createMessageII.getImage();
        createMessageImage = createMessageImage.getScaledInstance(128,128,Image.SCALE_SMOOTH);

        BufferedImage viewMessageBI = new BufferedImage(512, 512, BufferedImage.TYPE_4BYTE_ABGR);
        try {
            viewMessageBI = ImageIO.read(new File("Assets/view.png"));
        } catch (IOException e) {
            System.out.println(e);
        }
        ImageIcon viewMessageII = new ImageIcon(viewMessageBI);
        Image viewMessageImage = viewMessageII.getImage();
        viewMessageImage = viewMessageImage.getScaledInstance(128,128,Image.SCALE_SMOOTH);

        /*
         ** Adding buttons for viewing alerts and creating messages
         */
        JButton createMessage = new JButton();
        createMessage.setLayout(new BorderLayout());
        createMessage.add(BorderLayout.CENTER, new JLabel(new ImageIcon(createMessageImage)));
        JLabel createMessageLabel = new JLabel("Create Message");
        createMessageLabel.setHorizontalAlignment(JLabel.CENTER);
        createMessage.add(BorderLayout.SOUTH, createMessageLabel);
        alertsOptionPanel.add(createMessage);
        createMessage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!showingViewCreate) {
                    showCreateMessage();
                }
            }
        });
        JButton viewMessages = new JButton();
        viewMessages.setLayout(new BorderLayout());
        viewMessages.add(BorderLayout.CENTER, new JLabel(new ImageIcon(viewMessageImage)));
        JLabel viewMessageLabel = new JLabel("View Messages");
        viewMessageLabel.setHorizontalAlignment(JLabel.CENTER);
        viewMessages.add(BorderLayout.SOUTH, viewMessageLabel);
        alertsOptionPanel.add(viewMessages);
        viewMessages.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!showingViewMessages) {
                    showViewMessages();
                }
            }
        });

        add(alertsOptionPanel, BorderLayout.WEST);
    }

    private void showViewMessages() {
        viewMessagesPanel = new JPanel(new BorderLayout()); // Holds the components to create a message
        JPanel buttonPanel = new JPanel(new BorderLayout()); // Holds view and delete message buttons

        /*
        Setting up table to show alerts
         */
        importAlertsToTable(); // Importing alerts from an array of all the alerts
        alertsTable = new JTable(alertsTableModel);
        alertsTable.setShowGrid(true);
        JScrollPane alertScrollPane = new JScrollPane(alertsTable,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        alertScrollPane.setPreferredSize(new Dimension(600, 350));

        JButton viewAlertButton = new JButton("View Message");
        buttonPanel.add(viewAlertButton, BorderLayout.EAST);
        viewAlertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!VendorView.messagesToVendor.isEmpty()) {
                    try {
                        alertIndex = alertsTable.getSelectedRow();
                        showViewMessageDetail();
                    } catch (ArrayIndexOutOfBoundsException e1) {
                        System.out.println(e1);
                    }
                }
            }
        });

        JButton deleteAlertButton = new JButton("Delete Message");
        buttonPanel.add(deleteAlertButton, BorderLayout.WEST);
        deleteAlertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!VendorView.messagesToVendor.isEmpty()) {
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

        // Adding all components to the view
        viewMessagesPanel.add(alertScrollPane, BorderLayout.CENTER);
        viewMessagesPanel.add(buttonPanel, BorderLayout.SOUTH);
        if (showingViewCreate) {
            remove(createMessagePanel);
            showingViewCreate = false;
        }
        if (showingViewDetails) {
            remove(viewMessageDetailPanel);
            showingViewDetails = false;
        }
        add(viewMessagesPanel, BorderLayout.CENTER);
        showingViewMessages = true;
        repaint();
    }

    private void showViewMessageDetail() {
        viewMessageDetailPanel = new JPanel(new BorderLayout());

        // Sub-panel to hold back button
        JPanel buttonPanel = new JPanel(new BorderLayout());

        // Getting body and subject text for alerts ArrayList
        String alertBodyText = VendorView.messagesToVendor.get(alertIndex).getBody();
        String alertSubjectText = VendorView.messagesToVendor.get(alertIndex).getSubject();

        // Setting up label for subject of alert
        JLabel alertSubject = new JLabel(alertSubjectText);
        viewMessageDetailPanel.add(alertSubject, BorderLayout.NORTH);

        // Setting up text area for body of alert
        JTextArea alertBody = new JTextArea();
        alertBody.setEditable(false);
        alertBody.setLineWrap(true);
        alertBody.setWrapStyleWord(true);
        JScrollPane alertBodyScrollPane = new JScrollPane(alertBody,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        alertBodyScrollPane.setPreferredSize(new Dimension(500, 350));
        alertBody.setText(alertBodyText);
        viewMessageDetailPanel.add(alertBodyScrollPane, BorderLayout.CENTER);

        // setting up back button
        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                remove(viewMessageDetailPanel);
                showViewMessages();
            }
        });
        buttonPanel.add(backButton, BorderLayout.EAST);
        viewMessageDetailPanel.add(buttonPanel, BorderLayout.SOUTH);
        if (showingViewMessages) {
            remove(viewMessagesPanel);
            showingViewMessages = false;
        }
        if (showingViewCreate) {
            remove(createMessagePanel);
            showingViewCreate = false;
        }
        add(viewMessageDetailPanel, BorderLayout.CENTER);
        showingViewMessages = true;
        repaint();
    }

    private void showCreateMessage() {
        // Sub-panels to hold different components for the view
        JPanel alertSubjectPanel = new JPanel(new BorderLayout());
        JPanel alertBodyPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new BorderLayout());
        createMessagePanel = new JPanel(new BorderLayout());

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
        alertBodyPane.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                // Getting the loader ready to be shown
                showLoader();
                loader.setVisible(false);
            }
        });
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
                loader.setVisible(true); // showing loader while sending alert to the database
                // Checking for empty subject, subject too long, or empty body
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

                // Sending the alert to the database (in the background)
                SendMessageToManager sendAlertToDatabase = new SendMessageToManager(
                        alertSubjectField.getText(), alertBodyPane.getText());
                sendAlertToDatabase.execute();

                // Erasing text from subject and body fields
                alertSubjectField.setText("");
                alertBodyPane.setText("");
            }
        });
        buttonPanel.add(sendAlert, BorderLayout.EAST);

        // Adding sub-panels to the main panel
        createMessagePanel.add(alertSubjectPanel, BorderLayout.NORTH);
        createMessagePanel.add(alertBodyPanel, BorderLayout.CENTER);
        createMessagePanel.add(buttonPanel, BorderLayout.SOUTH);
        if (showingViewMessages) {
            remove(viewMessagesPanel);
            showingViewMessages = false;
        }
        if (showingViewDetails) {
            remove(viewMessageDetailPanel);
            showingViewDetails = false;
        }
        add(createMessagePanel, BorderLayout.CENTER);
        showingViewCreate = true;
        repaint();
    }

    private static class SendMessageToManager extends SwingWorker<Integer, String> {
        private String subject, body;

        public SendMessageToManager(String subject, String body) {
            this.subject = subject;
            this.body = body;
        }

        @Override
        protected Integer doInBackground() throws Exception {
            // Checking for apostrophe's in subject and body (causes syntax error in sql)
            subject = subject.replaceAll("'", "''");
            body = body.replaceAll("'", "''");

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
                statement.executeUpdate("INSERT INTO messagesToManager (subject, body, date) VALUES ('" +
                        subject + "','" + body +"','" + timestamp + "');");

                // Shows all alert table information
                ResultSet rs = statement.executeQuery("select * from messagesToManager");
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
            } finally {
                loader.setVisible(false);
            }
            return 1;
        }
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
                statement.executeUpdate("DELETE FROM messagesToVendor " +
                        "WHERE id=" + VendorView.messagesToVendor.get(alertIndex).getId() + ";");
                conn.close();
            } catch(Exception e) {
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(),
                        "Unable to delete alert.",
                        "Database Error",
                        JOptionPane.WARNING_MESSAGE);
                System.out.println(e);
            }

            // Reimporting table information from the database
            VendorView.importMessagesToVendor();
            return 1;
        }
    }

    private void importAlertsToTable() {
        String[] column = {"Subject", "Date Received"};
        String[][] data = new String[VendorView.messagesToVendor.size()][column.length]; // Holds information for columns
        if (!VendorView.messagesToVendor.isEmpty()) {
            for (int i = 0; i < VendorView.messagesToVendor.size(); i++) {
                data[i][0] = VendorView.messagesToVendor.get(i).getSubject();
                data[i][1] = VendorView.messagesToVendor.get(i).getFormattedTime();
            }
        }
        alertsTableModel = new DefaultTableModel(data, column) {
            @Override
            public boolean isCellEditable(int i, int i1) {
                return false;
            }
        };
    }

    private void showLoader() {
        try {
            add(loader);
            moveToFront(loader);
            repaint();
        } catch (IllegalArgumentException e) {
            System.out.println(e);
        }
    }
}
