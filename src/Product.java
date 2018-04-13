public class Product {
    String upc;
    String name;
    int pkgSize;
    double price;
    String prodType;
    boolean isRequired;
    int inventoryLevel;

    public Product() {
        upc = name = prodType = null;
        pkgSize = 0;
        price = 0.00;
        isRequired = false;
    }

    public String getUpc() {
        return upc;
    }

    public void setUpc(String upc) {
        this.upc = upc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPkgSize() {
        return pkgSize;
    }

    public void setPkgSize(int pkgSize) {
        this.pkgSize = pkgSize;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getProdType() {
        return prodType;
    }

    public void setProdType(String prodType) {
        this.prodType = prodType;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public void setRequired(boolean required) {
        isRequired = required;
    }

    public void setInventoryLevel(int inventoryLevel) {
        this.inventoryLevel = inventoryLevel;
    }

    public int getInventoryLevel() {
        return inventoryLevel;
    }
}
