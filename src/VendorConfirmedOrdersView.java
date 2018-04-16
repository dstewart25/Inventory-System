import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VendorConfirmedOrdersView extends JPanel {
    public VendorConfirmedOrdersView() {
        setLayout(new BorderLayout());
        initialize();
    }

    private void initialize() {
        // Sub-panel to hold buttons
        JPanel buttonPanel = new JPanel(new BorderLayout());

        // New Item and Order buttons
        JButton newItemButton = new JButton("Order Details");
        buttonPanel.add(newItemButton, BorderLayout.EAST);
        newItemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });

        // Setting up table to show inventory information
        /*
        The 0 below would be the size of the primary key in the inventory database
         */
        String[] column = {"Company Name", "Address", "Order Number"};
        String[][] data = new String[0][column.length]; // Holds information for columns
        DefaultTableModel orderTableModel = new DefaultTableModel(data, column);
        JTable orderTable = new JTable(orderTableModel);
        orderTable.setShowGrid(true);
        JScrollPane orderScrollPane = new JScrollPane(orderTable,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        orderScrollPane.setPreferredSize(new Dimension(600, 350));

        add(orderScrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}
