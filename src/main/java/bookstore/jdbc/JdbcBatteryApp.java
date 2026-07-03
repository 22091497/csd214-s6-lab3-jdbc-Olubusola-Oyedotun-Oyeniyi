package bookstore.jdbc;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

class Battery {
    private int id;
    private String productId; 
    private String manufacturer; 
    private double price; 
    private int capacityAh; 
    private int voltage; 

    public Battery(String productId, String manufacturer, double price, int capacityAh, int voltage) {
        this.productId = productId;
        this.manufacturer = manufacturer;
        this.price = price;
        this.capacityAh = capacityAh;
        this.voltage = voltage;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getProductId() { return productId; }
    public String getManufacturer() { return manufacturer; }
    public double getPrice() { return price; }
    public int getCapacityAh() { return capacityAh; }
    public int getVoltage() { return voltage; }

    @Override
    public String toString() {
        return "Battery [id=" + id + ", product_id=" + productId + ", manufacturer=" + manufacturer + 
               ", price=$" + price + ", capacity=" + capacityAh + "Ah, voltage=" + voltage + "V]";
    }
}

public class JdbcBatteryApp {
    private static final String DB_URL = "jdbc:mysql://localhost:3333/bookstore";
    private static final String USER = "root";
    private static final String PASS = "password";

    public static void main(String[] args) {
        createTable();

        // RUN 1: This seeds the database with initial values
        insertBattery(new Battery("BATT-999", "Bosch", 219.99, 80, 12));

        System.out.println("--- Reading Persistent Batteries From DB ---");
        listItems();
    }

    public static void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS batteries (" +
                     "id INT AUTO_INCREMENT PRIMARY KEY, " +
                     "product_id VARCHAR(50) NOT NULL, " +
                     "manufacturer VARCHAR(100) NOT NULL, " +
                     "price DECIMAL(10, 2) NOT NULL, " +
                     "capacity_ah INT NOT NULL, " +
                     "voltage INT NOT NULL" +
                     ");";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertBattery(Battery b) {
        String sql = "INSERT INTO batteries (product_id, manufacturer, price, capacity_ah, voltage) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, b.getProductId());
            pstmt.setString(2, b.getManufacturer());
            pstmt.setDouble(3, b.getPrice());
            pstmt.setInt(4, b.getCapacityAh());
            pstmt.setInt(5, b.getVoltage());
            pstmt.executeUpdate();
            System.out.println("Inserted Battery: " + b.getProductId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Battery> listItems() {
        List<Battery> batteries = new ArrayList<>();
        String sql = "SELECT * FROM batteries";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Battery b = new Battery(
                    rs.getString("product_id"),
                    rs.getString("manufacturer"),
                    rs.getDouble("price"),
                    rs.getInt("capacity_ah"),
                    rs.getInt("voltage")
                );
                b.setId(rs.getInt("id"));
                batteries.add(b);
                System.out.println(b);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return batteries;
    }

    public static void updatePrice(String manufacturer, double newPrice) {
        String sql = "UPDATE batteries SET price = ? WHERE manufacturer = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, newPrice);
            pstmt.setString(2, manufacturer);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteItem(String manufacturer) {
        String sql = "DELETE FROM batteries WHERE manufacturer = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, manufacturer);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
