package bookstore.jdbc;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

class Tire {
    private int id;
    private String productId; 
    private String manufacturer;
    private double price;
    private int size;
    private String tireType;

    public Tire(String productId, String manufacturer, double price, int size, String tireType) {
        this.productId = productId;
        this.manufacturer = manufacturer;
        this.price = price;
        this.size = size;
        this.tireType = tireType;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getProductId() { return productId; }
    public String getManufacturer() { return manufacturer; }
    public double getPrice() { return price; }
    public int getSize() { return size; }
    public String getTireType() { return tireType; }

    @Override
    public String toString() {
        return "Tire [id=" + id + ", product_id=" + productId + ", manufacturer=" + manufacturer + 
               ", price=$" + price + ", size=" + size + " inches, type=" + tireType + "]";
    }
}

public class JdbcTireApp {
    private static final String DB_URL = "jdbc:mysql://localhost:3333/bookstore";
    private static final String USER = "root";
    private static final String PASS = "password";

    public static void main(String[] args) {
        createTable();

        // RUN 1: Uncomment the line below to seed the database the first time you run it
        // insertTire(new Tire("TIRE-101", "Michelin", 189.99, 17, "All-Season"));

        // RUN 2: Comment the insertion line back out, run again, and screenshot this console output for Phase 4!
        System.out.println("--- Reading Persistent Tires From DB ---");
        listItems();
    }

    public static void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS tires (" +
                     "id INT AUTO_INCREMENT PRIMARY KEY, " +
                     "product_id VARCHAR(50) NOT NULL, " +
                     "manufacturer VARCHAR(100) NOT NULL, " +
                     "price DECIMAL(10, 2) NOT NULL, " +
                     "size INT NOT NULL, " +
                     "tire_type VARCHAR(50) NOT NULL" +
                     ");";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertTire(Tire t) {
        String sql = "INSERT INTO tires (product_id, manufacturer, price, size, tire_type) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, t.getProductId());
            pstmt.setString(2, t.getManufacturer());
            pstmt.setDouble(3, t.getPrice());
            pstmt.setInt(4, t.getSize());
            pstmt.setString(5, t.getTireType());
            pstmt.executeUpdate();
            System.out.println("Inserted Tire: " + t.getProductId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Tire> listItems() {
        List<Tire> tires = new ArrayList<>();
        String sql = "SELECT * FROM tires";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Tire t = new Tire(
                    rs.getString("product_id"),
                    rs.getString("manufacturer"),
                    rs.getDouble("price"),
                    rs.getInt("size"),
                    rs.getString("tire_type")
                );
                t.setId(rs.getInt("id"));
                tires.add(t);
                System.out.println(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tires;
    }

    public static void updatePrice(String manufacturer, double newPrice) {
        String sql = "UPDATE tires SET price = ? WHERE manufacturer = ?";
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
        String sql = "DELETE FROM tires WHERE manufacturer = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, manufacturer);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}