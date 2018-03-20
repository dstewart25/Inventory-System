import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ManagerConfirmOrderView extends JPanel {
    public ManagerConfirmOrderView() {
        setLayout(new BorderLayout());
        initialize();
    }

    public void initialize() {
        // Sub-panel to hold buttons
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        // Title label
        JLabel titleLabel = new JLabel("Review Order");
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.PLAIN, 18));
        add(titleLabel, BorderLayout.NORTH);

        // Total labels
        JLabel totalLabel = new JLabel("Total: $0.00");
        bottomPanel.add(totalLabel, BorderLayout.WEST);

        // New Item and Order buttons
        JButton backButton = new JButton("Back");
        buttonPanel.add(backButton);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ManagerView.changeToOrder();
            }
        });
        JButton confirmButton = new JButton("Confirm");
        buttonPanel.add(confirmButton);
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });

        // Setting up table to show inventory information
        String[] column = new String[] {"Name", "Pkg Size", "Current Inventory", "Amount", "Price"}; // Holds names of columns in the table
        /*
        The 0 below would be the size of the primary key in the inventory database
         */
        String[][] data = new String[0][column.length]; // Holds information for columns
        DefaultTableModel inventoryTableModel = new DefaultTableModel(data, column);
        JTable inventoryTable = new JTable(inventoryTableModel);
        JScrollPane inventoryScrollPane = new JScrollPane(inventoryTable,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        inventoryScrollPane.setPreferredSize(new Dimension(600, 350));

        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        add(inventoryScrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
}
