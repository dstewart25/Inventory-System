import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ManagerOrderView extends JPanel {
    // Used to view order information
    private static int numOfColumns = 0;
    private final String[] column = new String[] {"Name", "Pkg Size", "Current Inventory", "Amount", "Delete?"}; // Holds names of columns in the table

    public ManagerOrderView() {
        setLayout(new BorderLayout());
        initialize();
    }

    private void initialize() {
        // Sub-panel to hold buttons
        JPanel buttonPanel = new JPanel(new BorderLayout());

        // New Item and Order buttons
        JButton newItemButton = new JButton("New Item");
        buttonPanel.add(newItemButton, BorderLayout.WEST);
        newItemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addAColumn();
            }
        });
        JButton orderButton = new JButton("Order");
        buttonPanel.add(orderButton, BorderLayout.EAST);
        orderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ManagerView.changeToConfirmOrder();
            }
        });

        // Setting up table to show inventory information
        /*
        The 0 below would be the size of the primary key in the inventory database
         */
        String[][] data = new String[0][column.length]; // Holds information for columns
        DefaultTableModel orderTableModel = new DefaultTableModel(data, column);
        JTable orderTable = new JTable(orderTableModel);
        JScrollPane orderScrollPane = new JScrollPane(orderTable,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        orderScrollPane.setPreferredSize(new Dimension(600, 350));

        add(orderScrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /*
    Used to add a column to the table holding order information
    Couldn't figure this shit out but I'm working on it (it's a lot more confusing than it seems
     */
    private void addAColumn() {
        numOfColumns++;

        System.out.println(numOfColumns);
    }
}
