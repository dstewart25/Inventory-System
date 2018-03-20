import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ManagerAlertDetailView extends JPanel {
    public ManagerAlertDetailView() {
        setLayout(new BorderLayout());
        initialize();
    }

    private void initialize() {
        // Sub-panel to hold back button
        JPanel buttonPanel = new JPanel(new BorderLayout());

        // Getting body and subject text for alerts ArrayList
        String alertBodyText = ManagerView.alerts.get(ManagerAlertsView.alertIndex).getBody();
        String alertSubjectText = ManagerView.alerts.get(ManagerAlertsView.alertIndex).getSubject();

        // Setting up label for subject of alert
        JLabel alertSubject = new JLabel(alertSubjectText);
        add(alertSubject, BorderLayout.NORTH);

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
        add(alertBodyScrollPane, BorderLayout.CENTER);

        // setting up back button
        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ManagerView.changeToAlertView();
            }
        });
        buttonPanel.add(backButton, BorderLayout.EAST);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}
