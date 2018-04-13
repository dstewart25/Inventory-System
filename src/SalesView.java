
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Properties;

public class SalesView extends JPanel {
    private static String initialTime = String.valueOf(LocalDateTime.now());
    private static Pie_Chart salesChart = new Pie_Chart("Day",initialTime);

    public SalesView(){
        setLayout(new BorderLayout());
        initialize();
    }

    private void initialize(){
        String [] viewBy = {"Today","Week to date","Month to date",
                "Year to date","Specific Day"};

        JPanel sales = new JPanel(new GridLayout(5,1));

        JComboBox viewByBox = new JComboBox(viewBy);
        JPanel combo = new JPanel(new FlowLayout());
        JLabel view = new JLabel("View By:");
        view.setFont(new Font("Monospaced",Font.BOLD,20));
        combo.add(view);
        combo.add(viewByBox);

        JPanel chart = new JPanel();
        chart.add(salesChart);

        viewByBox.addActionListener(e -> {
            String time = String.valueOf(LocalDateTime.now());
            //time format: Mon Apr 16 19:25:54 EDT 2018
            String selected = (String) viewByBox.getSelectedItem();
            switch (selected){
                case "Today":
                    Pie_Chart dayChart = new Pie_Chart("Day",time);
                    chart.removeAll();
                    chart.add(dayChart);
                    break;
                case "Week to date":
                    Pie_Chart weekChart = new Pie_Chart("Week",time);
                    chart.removeAll();
                    chart.add(weekChart);
                    break;
                case "Month to date":
                    Pie_Chart monthChart = new Pie_Chart("Month",time);
                    chart.removeAll();
                    chart.add(monthChart);
                    break;
                case "Year to date":
                    Pie_Chart yearChart = new Pie_Chart("Year",time);
                    chart.removeAll();
                    chart.add(yearChart);
                    break;
                case "Specific Day":
                    JLabel dateSelect = new JLabel("Select Date:");
                    dateSelect.setFont(new Font("Monospaced",Font.BOLD,18));

                    //create calendar view
                    UtilDateModel model = new UtilDateModel();

                    Properties p = new Properties();
                    p.put("text.today", "Today");
                    p.put("text.month", "Month");
                    p.put("text.year", "Year");

                    JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
                    JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
                    combo.add(dateSelect);
                    combo.add(datePicker);
                    combo.revalidate();

                    datePicker.addActionListener(e1 -> {
                        Date selectedDate = (Date) datePicker.getModel().getValue();
                        String date= String.valueOf(selectedDate);
                        //date format: Mon Apr 16 19:25:54 EDT 2018
                        Pie_Chart specificChart = new Pie_Chart("Specific",date);

                        chart.removeAll();
                        chart.add(specificChart);
                        chart.revalidate();
                        chart.repaint();
                    });

                    break;
            }
        });

        JLabel foodLabel = new JLabel("Food Sales:");
        foodLabel.setFont(new Font("Monospaced",Font.PLAIN,20));

        JLabel beverageLabel = new JLabel("Beverage Sales:");
        beverageLabel.setFont(new Font("Monospaced",Font.PLAIN,20));

        JLabel tobaccoLabel = new JLabel("Tobacco Sales:");
        tobaccoLabel.setFont(new Font("Monospaced",Font.PLAIN,20));

        JLabel miscLabel = new JLabel("Misc. Sales:");
        miscLabel.setFont(new Font("Monospaced",Font.PLAIN,20));

        JLabel totalSalesLabel = new JLabel("Total Sales:");
        totalSalesLabel.setFont(new Font("Monospaced",Font.BOLD,25));

        sales.add(foodLabel);
        //add food sales total from DB
        sales.add(beverageLabel);
        //add beverage sales total from DB
        sales.add(tobaccoLabel);
        //add tobacco sales total from DB
        sales.add(miscLabel);
        //add misc sales total from DB
        sales.add(totalSalesLabel);
        //add total sales from DB

        add(combo,BorderLayout.NORTH);
        add(chart,BorderLayout.CENTER);
        add(sales,BorderLayout.EAST);
    }

}
