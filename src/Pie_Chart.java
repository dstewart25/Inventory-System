import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ScrollPane;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Pie_Chart extends JPanel{
    private static ObservableList<PieChart.Data> details = FXCollections.observableArrayList();
    private static PieChart pieChart;

    public Pie_Chart(String viewBy,String daTe){
        setLayout(new GridLayout(2,3,10,10));

        JFXPanel dataPanel = new JFXPanel();

        setSalesData(viewBy,daTe);

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
    private void setSalesData(String view,String date){
        switch (view){
            case "Day":
                setChart("Day",date);
                break;
            case "Week":
                setChart("Week",date);
                break;
            case "Month":
                setChart("Month",date);
                break;
            case "Year":
                setChart("Year",date);
                break;
            case "Specific":
                setChart("Specific",date);
                break;
        }
    }

    private void setChart(String param,String Date){
        //Date format: Mon Apr 16 19:25:54 EDT 2018

        //percentage of total sales
        double foodPiece=25;
        double beveragePiece=25;
        double tobaccoPiece=25;
        double miscPiece=25;
        String temp=param;
        String temp2=Date;

        /**Grab the data from the DB and reassign the chart piece values above
         **param and Date will be passed as search parameters
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

        details.addAll(new PieChart.Data("Food", foodPiece),
                new PieChart.Data("Beverages", beveragePiece),
                new PieChart.Data("Tobacco", tobaccoPiece),
                new PieChart.Data("Miscellaneous", miscPiece));
    }

}