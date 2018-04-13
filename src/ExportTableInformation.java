import java.sql.*;
import java.util.ArrayList;
import java.util.Random;

public class ExportTableInformation {
    public static void main(String[] args) {
        try {
            // Connecting to database
            Class.forName("com.mysql.jdbc.Driver");
            DriverManager.setLoginTimeout(10);
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://mfis-db-instance.ch7fymzvlb8l.us-east-1.rds.amazonaws.com:3306/MFIS_DB",
                    "root", "password");
            Statement statement = conn.createStatement();

            ResultSet productUPC = statement.executeQuery("select upc from products");
            ArrayList<String> UPC = new ArrayList<>();

            while (productUPC.next()) {
                UPC.add(productUPC.getString(1));
            }

            String query = "insert into inv_levels (upc, current_level)"
                    + " values (?, ?)";

            for (String upc_indivual : UPC) {
                int randNumber = new Random().nextInt(50) + 1;

                System.out.println(upc_indivual);
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1, upc_indivual);
                ps.setInt(2, randNumber);

                ps.execute();
            }

            conn.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
