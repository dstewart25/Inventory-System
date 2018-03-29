import javax.swing.*;
import java.awt.*;

/*
This class is used to initialize the program by:
    Showing the first screen for the program
    Importing the database (if needed)
    etc..
 */
public class InventorySystemController extends JFrame {
    public static void main(String[] args) {
        InventorySystemController frame = new InventorySystemController();
        frame.setVisible(true);
    }

    public InventorySystemController() {
        // Setting up the frame
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());
        JFrame myFrame = this;

        // Showing login screen when the Inventory System starts
        getContentPane().removeAll();
        getContentPane().add(new LoginScreen(myFrame), BorderLayout.CENTER);
        pack();
        getContentPane().setVisible(true);
        myFrame.setSize(400,275);
        //myFrame.setLocationRelativeTo(null);
    }
}
