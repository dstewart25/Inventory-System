
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

public class SalesView extends JPanel {
    private String initialTime = String.valueOf(LocalDateTime.now());
    private Pie_Chart salesChart = new Pie_Chart("Day",initialTime);

    private Font monoBold20 = new Font("Monospaced",Font.BOLD,20);
    private Font monoPlain18 = new Font("Monospaced",Font.PLAIN,18);

    private JLabel foodLabel = new JLabel("Food Sales:");
    private JLabel foodSalesLabel = new JLabel();
    private JLabel beverageLabel = new JLabel("Beverage Sales:");
    private JLabel beverageSalesLabel = new JLabel();
    private JLabel tobaccoLabel = new JLabel("Tobacco Sales:");
    private JLabel tobaccoSalesLabel = new JLabel();
    private JLabel miscLabel = new JLabel("Misc. Sales:");
    private JLabel miscSalesLabel = new JLabel();
    private JLabel totalLabel = new JLabel("Total Sales:");
    private JLabel totalSalesLabel = new JLabel();

    private Random random = new Random();

    public SalesView(){
        setLayout(new BorderLayout());
        initialize();
    }

    private void initialize(){
        String [] viewBy = {"Today","Week to date","Month to date",
                "Year to date","Specific Day"};

        JPanel sales = new JPanel(new GridLayout(10,1));

        JComboBox viewByBox = new JComboBox(viewBy);
        JPanel combo = new JPanel(new FlowLayout());
        JLabel view = new JLabel("View By:");
        view.setFont(new Font("Monospaced",Font.BOLD,20));
        combo.add(view);
        combo.add(viewByBox);

        JPanel chart = new JPanel();
        setSalesLabels("Today",initialTime);
        chart.add(salesChart);

        viewByBox.addActionListener(e -> {
            String time = String.valueOf(LocalDateTime.now());
            //time format: Mon Apr 30 19:25:54 EDT 2018
            String selected = (String) viewByBox.getSelectedItem();
            switch (selected){
                case "Today":
                    Pie_Chart dayChart = new Pie_Chart("Day",time);
                    chart.removeAll();
                    chart.add(dayChart);
                    combo.removeAll();
                    combo.add(view);
                    combo.add(viewByBox);

                    setSalesLabels("Today",time);

                    chart.revalidate();
                    chart.repaint();
                    sales.revalidate();
                    combo.revalidate();
                    combo.repaint();
                    break;
                case "Week to date":
                    Pie_Chart weekChart = new Pie_Chart("Week",time);
                    chart.removeAll();
                    chart.add(weekChart);
                    combo.removeAll();
                    combo.add(view);
                    combo.add(viewByBox);

                    setSalesLabels("Week to date",time);

                    chart.revalidate();
                    chart.repaint();
                    sales.revalidate();
                    combo.revalidate();
                    combo.repaint();
                    break;
                case "Month to date":
                    Pie_Chart monthChart = new Pie_Chart("Month",time);
                    chart.removeAll();
                    chart.add(monthChart);
                    combo.removeAll();
                    combo.add(view);
                    combo.add(viewByBox);

                    setSalesLabels("Month to date",time);

                    chart.revalidate();
                    chart.repaint();
                    sales.revalidate();
                    combo.revalidate();
                    combo.repaint();
                    break;
                case "Year to date":
                    Pie_Chart yearChart = new Pie_Chart("Year",time);
                    chart.removeAll();
                    chart.add(yearChart);
                    combo.removeAll();
                    combo.add(view);
                    combo.add(viewByBox);

                    setSalesLabels("Year to date",time);

                    chart.revalidate();
                    chart.repaint();
                    sales.revalidate();
                    combo.revalidate();
                    combo.repaint();
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
                    combo.repaint();

                    datePicker.addActionListener(e1 -> {
                        Date selectedDate = (Date) datePicker.getModel().getValue();
                        String date= String.valueOf(selectedDate);
                        long millis=System.currentTimeMillis();
                        Date currentDate = new Date(millis);

                        if(selectedDate.after(currentDate)){
                            datePicker.getModel().setValue(null);
                            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(),
                                    "We unfortunately can't predict future sales!",
                                    "Invalid date",
                                    JOptionPane.WARNING_MESSAGE);
                        }else {
                            //date format: Mon Apr 30 19:25:54 EDT 2018
                            Pie_Chart specificChart = new Pie_Chart("Specific", date);

                            chart.removeAll();
                            chart.add(specificChart);
                            setSalesLabels("Specific Day", date);
                            sales.revalidate();
                            chart.revalidate();
                            chart.repaint();
                        }
                    });

                    break;
            }
        });

        sales.add(foodLabel);
        sales.add(foodSalesLabel);
        sales.add(beverageLabel);
        sales.add(beverageSalesLabel);
        sales.add(tobaccoLabel);
        sales.add(tobaccoSalesLabel);
        sales.add(miscLabel);
        sales.add(miscSalesLabel);
        sales.add(totalLabel);
        sales.add(totalSalesLabel);

        add(combo,BorderLayout.NORTH);
        add(chart,BorderLayout.CENTER);
        add(sales,BorderLayout.EAST);
    }

    //Sets sales labels font, and retrieves sales data depending upon param and date.
    private void setSalesLabels(String param,String date){
        foodLabel.setFont(monoBold20);
        foodSalesLabel.setFont(monoPlain18);
        beverageLabel.setFont(monoBold20);
        beverageSalesLabel.setFont(monoPlain18);
        tobaccoLabel.setFont(monoBold20);
        tobaccoSalesLabel.setFont(monoPlain18);
        miscLabel.setFont(monoBold20);
        miscSalesLabel.setFont(monoPlain18);
        totalLabel.setFont(monoBold20);
        totalSalesLabel.setFont(monoPlain18);

        switch(param){
            case "Today":
                retrieveSalesData(date);
                break;
            case "Week to date":
                retrieveSalesData("Week",date);
                break;
            case "Month to date":
                retrieveSalesData("Month",date);
                break;
            case "Year to date":
                retrieveSalesData("Year",date);
                break;
            case "Specific Day":
                retrieveSalesData(date);
                break;
        }

    }

    //searches DB for the specified date(time) and sets the text for sales labels.
    private void retrieveSalesData(String time){
        //time format: Mon Apr 30 19:25:54 EDT 2018
         String temp=time;
        /**Connect to the DB and grab the data for the specified day
         * then assign those values to the given sales labels
         try {
         // Connecting to database
         Class.forName("com.mysql.jdbc.Driver");
         DriverManager.setLoginTimeout(10);
         Connection conn = DriverManager.getConnection(
         "jdbc:mysql://mfis-db-instance.ch7fymzvlb8l.us-east-1.rds.amazonaws.com:3306/MFIS_DB",
         "root", "password");
         Statement statement = conn.createStatement();

         } catch (ClassNotFoundException e) {
         e.printStackTrace();
         } catch (SQLException e) {
         e.printStackTrace();
         }
         **/

        //temporarily generates random sales values until DB is setup
        int foodSales=random.nextInt(2000);
        int beverageSales=random.nextInt(2000);
        int tobaccoSales=random.nextInt(2000);
        int miscSales=random.nextInt(1000);
        int totalSales=foodSales+beverageSales+tobaccoSales+miscSales;

        foodSalesLabel.setText("$"+String.valueOf(foodSales));
        beverageSalesLabel.setText("$"+String.valueOf(beverageSales));
        tobaccoSalesLabel.setText("$"+String.valueOf(tobaccoSales));
        miscSalesLabel.setText("$"+String.valueOf(miscSales));
        totalSalesLabel.setText("$"+String.valueOf(totalSales));
    }

    //search DB for specified week/month/year(type,time) and sets the text for sales labels.
    private void retrieveSalesData(String type,String time){
        //time format: Mon Apr 30 19:25:54 EDT 2018
        String temp=type;
        String temp2=time;
        /**Connect to the DB and grab the data for the specified week/month/year
         * then assign those values to the given sales labels
         try {
         // Connecting to database
         Class.forName("com.mysql.jdbc.Driver");
         DriverManager.setLoginTimeout(10);
         Connection conn = DriverManager.getConnection(
         "jdbc:mysql://mfis-db-instance.ch7fymzvlb8l.us-east-1.rds.amazonaws.com:3306/MFIS_DB",
         "root", "password");
         Statement statement = conn.createStatement();

         } catch (ClassNotFoundException e) {
         e.printStackTrace();
         } catch (SQLException e) {
         e.printStackTrace();
         }
         **/

        //temporarily generates random sales values until DB is setup
        int foodSales=random.nextInt(20000);
        int beverageSales=random.nextInt(20000);
        int tobaccoSales=random.nextInt(20000);
        int miscSales=random.nextInt(10000);
        int totalSales=foodSales+beverageSales+tobaccoSales+miscSales;

        foodSalesLabel.setText("$"+String.valueOf(foodSales));
        beverageSalesLabel.setText("$"+String.valueOf(beverageSales));
        tobaccoSalesLabel.setText("$"+String.valueOf(tobaccoSales));
        miscSalesLabel.setText("$"+String.valueOf(miscSales));
        totalSalesLabel.setText("$"+String.valueOf(totalSales));
    }

}
