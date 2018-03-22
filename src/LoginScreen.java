import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/*
Shows the login screen to either login as the user or vendor
 */
public class LoginScreen extends JPanel {
    private JFrame frame;
    private String username;
    private String password;

    public LoginScreen(JFrame frame) {
        this.frame = frame;
        frame.setSize(400,275);
        setLayout(new GridBagLayout());
        initialize(); // Initializing the view

        // Creating empty border for the panel
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    /*
    Filling the panel
     */
    private void initialize() {
        frame.setTitle("MFIS");

        GridBagConstraints c = new GridBagConstraints();

        // Creating login label
        JLabel loginLabel = new JLabel();
        loginLabel.setText("Login");
        loginLabel.setFont(new Font(loginLabel.getFont().getName(), Font.PLAIN, 30));
        loginLabel.setHorizontalAlignment(JLabel.CENTER);

        // Creating username and password text field
        JTextField usernameField = new JTextField("Username");
        JPasswordField passwordField = new JPasswordField("password");
        usernameField.setColumns(20);
        usernameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyChar() == KeyEvent.VK_ENTER) {
                    // If 'ENTER' key is pressed then try to login
                    // Getting username and password inputting by user
                    username = usernameField.getText();
                    password = passwordField.getText();
                    if (username.equals("")) {
                        JOptionPane.showMessageDialog(JOptionPane.getRootFrame(),
                                "Please enter your username.",
                                "No Username",
                                JOptionPane.WARNING_MESSAGE);
                    } else if (password.equals("")) {
                        JOptionPane.showMessageDialog(JOptionPane.getRootFrame(),
                                "Please enter your password.",
                                "No Password",
                                JOptionPane.WARNING_MESSAGE);
                    } else if (!checkLogin()) {
                        JOptionPane.showMessageDialog(JOptionPane.getRootFrame(),
                                "Username or password is incorrect.",
                                "Error",
                                JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });
        usernameField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                usernameField.setText("");
            }
        });
        passwordField.setColumns(20);
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyChar() == KeyEvent.VK_ENTER) {
                    // If 'ENTER' key is pressed then try to login
                    // Getting username and password inputting by user
                    username = usernameField.getText();
                    password = passwordField.getText();
                    if (username.equals("")) {
                        JOptionPane.showMessageDialog(JOptionPane.getRootFrame(),
                                "Please enter your username.",
                                "No Username",
                                JOptionPane.WARNING_MESSAGE);
                    } else if (password.equals("")) {
                        JOptionPane.showMessageDialog(JOptionPane.getRootFrame(),
                                "Please enter your password.",
                                "No Password",
                                JOptionPane.WARNING_MESSAGE);
                    } else if (!checkLogin()) {
                        JOptionPane.showMessageDialog(JOptionPane.getRootFrame(),
                                "Username or password is incorrect.",
                                "Error",
                                JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });
        passwordField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                passwordField.setText("");
            }
        });

        // Creating login button
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Getting username and password inputting by user
                username = usernameField.getText();
                password = passwordField.getText();
                if (username.equals("")) {
                    JOptionPane.showMessageDialog(JOptionPane.getRootFrame(),
                            "Please enter your username.",
                            "No Username",
                            JOptionPane.WARNING_MESSAGE);
                } else if (password.equals("")) {
                    JOptionPane.showMessageDialog(JOptionPane.getRootFrame(),
                            "Please enter your password.",
                            "No Password",
                            JOptionPane.WARNING_MESSAGE);
                } else if (!checkLogin()) {
                    JOptionPane.showMessageDialog(JOptionPane.getRootFrame(),
                            "Username or password is incorrect.",
                            "Error",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // Creating exit button to exit the program
        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(1);
            }
        });

        // Adding components to the frame
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 1;
        add(loginLabel, c);

        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 3;
        c.insets = new Insets(20, 0, 10, 0);
        add(usernameField, c);

        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 3;
        c.insets = new Insets(0, 0, 40, 0);
        add(passwordField, c);

        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 1;
        add(loginButton, c);

        c.gridx = 2;
        c.gridy = 3;
        c.gridwidth = 1;
        add(exitButton, c);
    }

    private boolean checkLogin() {
        try {
            // Connecting to database
            Class.forName("com.mysql.jdbc.Driver");
            DriverManager.setLoginTimeout(10);
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://mfis-db-instance.ch7fymzvlb8l.us-east-1.rds.amazonaws.com:3306/MFIS_DB",
                    "root", "password");
            Statement statement = conn.createStatement();

            // Import user_manager table information into rs_manager
            ResultSet rs_manager = statement.executeQuery("select * from user_manager");
            // Testing to see if login information is correct for the manager side
            while (rs_manager.next()) {
                if (rs_manager.getString(1).toLowerCase().equals(username.toLowerCase()) &&
                        rs_manager.getString(2).equals(password)) {
                    ManagerView.importAlertsFromDatabase();
                    conn.close();

                    // Login to inventory screen for manager
                    frame.getContentPane().removeAll();
                    frame.getContentPane().add(new ManagerView(frame));
                    frame.pack();
                    frame.getContentPane().setVisible(true);
                    return true;
                }
            }

            ResultSet rs_vendor = statement.executeQuery("select * from user_vendor");
            // Testing to see if login information is correct for the vendor side
            while (rs_vendor.next()) {
                if (rs_vendor.getString(1).toLowerCase().equals(username.toLowerCase()) &&
                        rs_vendor.getString(2).equals(password)) {
                    conn.close();

                    // Login to inventory screen for manager
                    frame.getContentPane().removeAll();
                    frame.getContentPane().add(new VendorView(frame));
                    frame.pack();
                    frame.getContentPane().setVisible(true);
                    return true;
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return false;
    }
}
