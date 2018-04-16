import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

public class Order {
    int orderNumber;
    String address;
    String companyName;
    HashMap<Product, Integer> products;
    private Timestamp time;
    boolean confirmed;
    boolean delivered;

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public HashMap<Product, Integer> getProducts() {
        return products;
    }

    public void setProducts(HashMap<Product, Integer> products) {
        this.products = products;
    }

    public Timestamp getTime() {
        return time;
    }

    public String getFormattedTime() {
        String year = new SimpleDateFormat("MMMMMMMMMM dd, yyyy").format(time);
        String time = new SimpleDateFormat("HH:MM").format(this.time);
        String convertedTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm")).format(DateTimeFormatter.ofPattern("hh:mm a"));
        if (convertedTime.startsWith("0"))
            convertedTime = convertedTime.substring(1);
        return year + " at " + convertedTime;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public boolean isDelivered() {
        return delivered;
    }

    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }
}
