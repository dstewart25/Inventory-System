import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/*
Shows the inventory levels for the managers to view
 */
public class ManagerInventoryView extends JPanel {
    // Used to view inventory information
    private JTable inventoryTable;
    private JScrollPane scrollPane;
    private DefaultTableModel tableModel;

    public ManagerInventoryView() {
        setLayout(new BorderLayout());
        initialize(); // Initializing the view
    }

    /*
    Filling the panel
     */
    private void initialize() {
        populateTable(); // Getting information from database for inventory

        // Setting up combo box to show different options for categories
        String[] strCategories = {"Popular", "Required", "Candy", "Drinks", "Tobacco"};
        JComboBox categories = new JComboBox(strCategories);
        categories.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });

        // Setting up table to show inventory information
        inventoryTable = new JTable(tableModel);
        scrollPane = new JScrollPane(inventoryTable,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(600, 350));

        add(categories, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    /*
    This method is used to get information from the database and
    use it to populate the table with said information
     */
    private void populateTable() {
        String[] column = new String[] {"Name", "Pkg Size", "Current Inventory"}; // Holds names of columns
        /*
        The 10 below would be the size of the primary key in the inventory database
         */
        String[][] data = new String[10][column.length]; // Holds information for columns

        tableModel = new DefaultTableModel(data, column);
    }
}
