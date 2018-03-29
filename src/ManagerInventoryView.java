import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

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
        // Setting up combo box to show different options for categories
        String[] strCategories = {"Popular", "Required", "Candy", "Drinks", "Tobacco"};
        JComboBox categories = new JComboBox(strCategories);
        categories.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });

        // Setting up table to show inventory information
        importProductsToTable();
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
    private void importProductsToTable() {
        String[] column = {"Name", "Pkg Size", "Current Inventory"}; // Holds names of columns
        String[][] data = new String[ManagerView.products.size()][column.length]; // Holds information for columns

        if (!ManagerView.products.isEmpty()) {
            for (int i = 0; i < ManagerView.products.size(); i++) {
                data[i][0] = ManagerView.products.get(i).getName();
                data[i][1] = String.valueOf(ManagerView.products.get(i).getPkgSize());
                data[i][2] = String.valueOf(new Integer(new Random().nextInt(50) + 1));
            }
        }
        tableModel = new DefaultTableModel(data, column) {
            @Override
            public boolean isCellEditable(int i, int i1) {
                return false;
            }
        };

        tableModel = new DefaultTableModel(data, column);
    }
}
