import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ScrollPane;

import javax.swing.*;
import java.awt.*;

public class Pie_Chart extends JPanel{
    private ObservableList<PieChart.Data> details =   FXCollections.observableArrayList();
    private PieChart pieChart;

    public Pie_Chart(String viewBy){
        setLayout( new GridLayout(2,3,10,10));

        JFXPanel dataPanel = new JFXPanel();

        setSalesData(viewBy);

        pieChart = new PieChart();
        pieChart.setData(details);
        pieChart.setTitle("Product Sales:");
        pieChart.setLegendSide(Side.TOP);
        pieChart.setLabelsVisible(true);

        ScrollPane sp = new ScrollPane(pieChart);
        sp.setContent(pieChart);

        Scene scene = new Scene(sp, 600, 500);
        dataPanel.setScene(scene);

        add(dataPanel);
    }

    //Sets sales data based upon the view parameter(Day,Week,Month,Year,Specific)
    private void setSalesData(String view){
        switch (view){
            case "Day":
                setChart(25,25,25,25);
                break;
            case "Week":
                setChart(15,35,25,25);
                break;
            case "Month":
                setChart(25,25,45,5);
                break;
            case "Year":
                setChart(15,25,35,25);
                break;
            case "Specific":
                setChart(25,25,25,25);
                break;
        }
    }

    private void setChart(double f, double b, double t, double m){
        details.addAll(new PieChart.Data("Food", f),
                new PieChart.Data("Beverages", b),
                new PieChart.Data("Tobacco", t),
                new PieChart.Data("Miscellaneous", m));
    }
}