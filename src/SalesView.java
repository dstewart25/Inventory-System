import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SalesView extends JPanel {
    //default view is Current Day
    private Pie_Chart salesChart=new Pie_Chart("Day");

    public SalesView(){
        setLayout(new BorderLayout());
        initialize();
    }

    private void initialize(){
        String [] viewBy = {"Current Day","Current Week","Current Month",
                "Current Year","Specific Day/Week/Month/Year"};

        JComboBox viewByBox = new JComboBox(viewBy);
        JPanel combo = new JPanel(new FlowLayout());
        JLabel view = new JLabel("View By:");
        view.setFont(new Font("Monospaced",Font.BOLD,20));
        combo.add(view);
        combo.add(viewByBox);

        JPanel chart = new JPanel();
        chart.add(salesChart);

        viewByBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //System.out.println(e.getActionCommand());
                String selected = (String) viewByBox.getSelectedItem();
                switch (selected){
                    case "Current Day":
                        Pie_Chart dayChart = new Pie_Chart("Day");
                        chart.removeAll();
                        chart.add(dayChart);
                        break;
                    case "Current Week":
                        Pie_Chart weekChart = new Pie_Chart("Week");
                        chart.removeAll();
                        chart.add(weekChart);
                        break;
                    case "Current Month":
                        Pie_Chart monthChart = new Pie_Chart("Month");
                        chart.removeAll();
                        chart.add(monthChart);
                        break;
                    case "Current Year":
                        Pie_Chart yearChart = new Pie_Chart("Year");
                        chart.removeAll();
                        chart.add(yearChart);
                        break;
                    case "Specific Day/Week/Month/Year":
                        Pie_Chart specificChart = new Pie_Chart("Specific");
                        chart.removeAll();
                        chart.add(specificChart);
                        break;
                }
            }
        });

        JLabel foodSales = new JLabel("Food Sales:");
        foodSales.setFont(new Font("Monospaced",Font.PLAIN,20));
        JLabel beverageSales = new JLabel("Beverage Sales:");
        beverageSales.setFont(new Font("Monospaced",Font.PLAIN,20));
        JLabel tobaccoSales = new JLabel("Tobacco Sales:");
        tobaccoSales.setFont(new Font("Monospaced",Font.PLAIN,20));
        JLabel miscSales = new JLabel("Misc. Sales:");
        miscSales.setFont(new Font("Monospaced",Font.PLAIN,20));
        JLabel totalSales = new JLabel("Total Sales:");
        totalSales.setFont(new Font("Monospaced",Font.BOLD,25));

        JPanel sales = new JPanel(new GridLayout(5,1,-5,-5));
        sales.add(foodSales);
        sales.add(beverageSales);
        sales.add(tobaccoSales);
        sales.add(miscSales);
        sales.add(totalSales);

        add(combo,BorderLayout.NORTH);
        add(chart,BorderLayout.CENTER);
        add(sales,BorderLayout.EAST);
    }

}
