import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

/*
Shows the inventory levels for the managers to view
 */
public class ManagerInventoryView extends JPanel {
    private DefaultTableModel tableModel;
    private HashMap<String, String> newInvLevels = new HashMap<>();
    private JTable inventoryTable;
    private JScrollPane scrollPane;
    private int categoryIndex;

    public ManagerInventoryView(int categoryIndex) {
        setLayout(new BorderLayout());
        this.categoryIndex = categoryIndex;
        initialize(); // Initializing the view
    }

    /*
    Filling the panel
     */
    private void initialize() {
        // Panel to hold save button for inventory updates
        JPanel buttonPanel = new JPanel(new BorderLayout());

        // Setting up combo box to show different options for categories
        String[] strCategories = {"All", "Popular", "Required", "Food", "Non-Alcoholic", "Alcohol"};
        JComboBox categories = new JComboBox(strCategories);
        categories.setSelectedIndex(categoryIndex);
        categories.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = categories.getSelectedIndex();
                switch (selectedIndex) {
                    case 0: //  All Products
                        ManagerView.changeToAllCategory();
                        break;
                    case 3: // Food
                        ManagerView.changeToFoodCategory();
                        break;
                    case 4: // Non-Alcoholic
                        ManagerView.changeToNonAlcoholicCategory();
                        break;
                    case 5: // Alcohol
                        ManagerView.changeToAlcoholicCategory();
                        break;
                    default:
                        break;
                }
            }
        });

        JButton saveButton = new JButton("Save");
        saveButton.setEnabled(false);
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UpdateInventoryLevels updateInventoryLevels = new UpdateInventoryLevels(newInvLevels);
                updateInventoryLevels.execute();
                newInvLevels = new HashMap<>();

                saveButton.setEnabled(false);
            }
        });
        buttonPanel.add(saveButton, BorderLayout.EAST);

        // Setting up table to show inventory information
        switch (categoryIndex) {
            case 0: //  All Products
                importAllProducts();
                break;
            case 3: // Food
                importFoodProducts();
                break;
            case 4: // Non-Alcoholic
                importNonAlcoholicProducts();
                break;
            case 5: // Alcohol
                importAlcoholicProducts();
                break;
            default:
                break;
        }
        inventoryTable = new JTable(tableModel);
        scrollPane = new JScrollPane(inventoryTable,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(600, 350));
        tableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (!saveButton.isEnabled())
                    saveButton.setEnabled(true);

                // Holding new inventory levels with their upc as key
                newInvLevels.put(ManagerView.products.get(e.getLastRow()).getUpc(),
                        String.valueOf(inventoryTable.getValueAt(e.getLastRow(), e.getColumn())));
            }
        });

        add(categories, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /*
    This method is used to get information from the database and
    use it to populate the table with said information
     */
    private void importAllProducts() {
        String[] column = {"Name", "Pkg Size", "Current Inventory"}; // Holds names of columns
        String[][] data = new String[ManagerView.products.size()][column.length]; // Holds information for columns

        if (!ManagerView.products.isEmpty()) {
            for (int i = 0; i < ManagerView.products.size(); i++) {
                data[i][0] = ManagerView.products.get(i).getName();
                data[i][1] = String.valueOf(ManagerView.products.get(i).getPkgSize());
                data[i][2] = String.valueOf(ManagerView.products.get(i).getInventoryLevel());
            }
        }
        tableModel = new DefaultTableModel(data, column) {
            @Override
            public boolean isCellEditable(int row, int col) {
                switch (col) {
                    case 2:
                        return true;
                    default:
                        return false;
                }
            }
        };
    }

    private void importFoodProducts() {
        String[] column = {"Name", "Pkg Size", "Current Inventory"}; // Holds names of columns
        String[][] data = {{"Invalid"},{"Input"},{"IDK man"}};
        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> pkgSizes = new ArrayList<>();
        ArrayList<String> invLevels = new ArrayList<>();

        if (!ManagerView.products.isEmpty()) {
            for (int i = 0; i < ManagerView.products.size(); i++) {
                if (ManagerView.products.get(i).getProdType().equals("Food")) {
                    names.add(ManagerView.products.get(i).getName());
                    pkgSizes.add(String.valueOf(ManagerView.products.get(i).getPkgSize()));
                    invLevels.add(String.valueOf(ManagerView.products.get(i).getInventoryLevel()));
                }
            }

            data = new String[names.size()][column.length]; // Holds information for columns
            for (int i=0; i<names.size(); i++) {
                data[i][0] = names.get(i);
                data[i][1] = pkgSizes.get(i);
                data[i][2] = invLevels.get(i);
            }
        }
        tableModel = new DefaultTableModel(data, column) {
            @Override
            public boolean isCellEditable(int row, int col) {
                switch (col) {
                    case 2:
                        return true;
                    default:
                        return false;
                }
            }
        };
    }

    private void importAlcoholicProducts() {
        String[] column = {"Name", "Pkg Size", "Current Inventory"}; // Holds names of columns
        String[][] data = {{"Invalid"},{"Input"},{"IDK man"}};
        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> pkgSizes = new ArrayList<>();
        ArrayList<String> invLevels = new ArrayList<>();

        if (!ManagerView.products.isEmpty()) {
            for (int i = 0; i < ManagerView.products.size(); i++) {
                if (ManagerView.products.get(i).getProdType().equals("Alcohol")) {
                    names.add(ManagerView.products.get(i).getName());
                    pkgSizes.add(String.valueOf(ManagerView.products.get(i).getPkgSize()));
                    invLevels.add(String.valueOf(ManagerView.products.get(i).getInventoryLevel()));
                }
            }

            data = new String[names.size()][column.length]; // Holds information for columns
            for (int i=0; i<names.size(); i++) {
                data[i][0] = names.get(i);
                data[i][1] = pkgSizes.get(i);
                data[i][2] = invLevels.get(i);
            }
        }
        tableModel = new DefaultTableModel(data, column) {
            @Override
            public boolean isCellEditable(int row, int col) {
                switch (col) {
                    case 2:
                        return true;
                    default:
                        return false;
                }
            }
        };
    }

    private void importNonAlcoholicProducts() {
        String[] column = {"Name", "Pkg Size", "Current Inventory"}; // Holds names of columns
        String[][] data = {{"Invalid"},{"Input"},{"IDK man"}};
        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> pkgSizes = new ArrayList<>();
        ArrayList<String> invLevels = new ArrayList<>();

        if (!ManagerView.products.isEmpty()) {
            for (int i = 0; i < ManagerView.products.size(); i++) {
                if (ManagerView.products.get(i).getProdType().equals("Non-Alcoholic")) {
                    names.add(ManagerView.products.get(i).getName());
                    pkgSizes.add(String.valueOf(ManagerView.products.get(i).getPkgSize()));
                    invLevels.add(String.valueOf(ManagerView.products.get(i).getInventoryLevel()));
                }
            }

            data = new String[names.size()][column.length]; // Holds information for columns
            for (int i=0; i<names.size(); i++) {
                data[i][0] = names.get(i);
                data[i][1] = pkgSizes.get(i);
                data[i][2] = invLevels.get(i);
            }
        }
        tableModel = new DefaultTableModel(data, column) {
            @Override
            public boolean isCellEditable(int row, int col) {
                switch (col) {
                    case 2:
                        return true;
                    default:
                        return false;
                }
            }
        };
    }

    private static class UpdateInventoryLevels extends SwingWorker<Integer, String> {
        private Map<String, String> newInvLevels;

        public UpdateInventoryLevels(Map<String, String> newInvLevels) {
            this.newInvLevels = newInvLevels;
        }

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

                String query = "UPDATE inv_levels " +
                        "SET current_level=? " +
                        "WHERE upc=?";

                for (Map.Entry<String, String> entry : newInvLevels.entrySet()) {
                    System.out.println(entry.getKey() + ": " + entry.getValue());

                    PreparedStatement ps = conn.prepareStatement(query);
                    ps.setInt(1, Integer.valueOf(entry.getValue())); // New inv level
                    ps.setString(2, entry.getKey()); // UPC

                    ps.execute();
                }

                conn.close();
            } catch(Exception e) {
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(),
                        "Unable to updated inventory levels.",
                        "Database Error",
                        JOptionPane.WARNING_MESSAGE);
                System.out.println(e);
            }
            System.out.println("Saved");
            return 1;
        }
    }
}
