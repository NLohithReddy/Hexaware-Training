package dao;

import entity.*;
import exception.UserNotFoundException;
import exception.OrderNotFoundException;
import util.DBConnUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderProcessor implements IOrderManagementRepository {

    private final String dbProps = "src/db.properties";

    @Override
    public void createUser(User user) {
        String query = "INSERT INTO User (username, password, role) VALUES (?, ?, ?)";

        try (Connection conn = DBConnUtil.getConnection(dbProps);
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getRole());

            int rows = ps.executeUpdate();
            System.out.println(rows > 0 ? "User created!" : "Failed to create user.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createProduct(User user, Product product) throws UserNotFoundException {
        if (!isAdmin(user.getUsername())) throw new UserNotFoundException("User not authorized");

        String baseQuery = "INSERT INTO Product (productName, description, price, quantityInStock, type) VALUES (?, ?, ?, ?, ?)";
        String typeQuery = product instanceof Electronics
                ? "INSERT INTO Electronics (productId, brand, warrantyPeriod) VALUES (?, ?, ?)"
                : "INSERT INTO Clothing (productId, size, color) VALUES (?, ?, ?)";

        try (Connection conn = DBConnUtil.getConnection(dbProps);
             PreparedStatement ps = conn.prepareStatement(baseQuery, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, product.getProductName());
            ps.setString(2, product.getDescription());
            ps.setDouble(3, product.getPrice());
            ps.setInt(4, product.getQuantityInStock());
            ps.setString(5, product.getType());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int productId = rs.getInt(1);

                    PreparedStatement typeStmt = conn.prepareStatement(typeQuery);
                    typeStmt.setInt(1, productId);

                    if (product instanceof Electronics) {
                        Electronics e = (Electronics) product;
                        typeStmt.setString(2, e.getBrand());
                        typeStmt.setInt(3, e.getWarrantyPeriod());
                    } else {
                        Clothing c = (Clothing) product;
                        typeStmt.setString(2, c.getSize());
                        typeStmt.setString(3, c.getColor());
                    }

                    typeStmt.executeUpdate();
                    System.out.println("Product added with ID: " + productId);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createOrder(User user, List<Product> products) throws UserNotFoundException {
        Integer userId = getUserId(user.getUsername());
        if (userId == null) throw new UserNotFoundException("User does not exist");

        try (Connection conn = DBConnUtil.getConnection(dbProps)) {
            conn.setAutoCommit(false);

            String insertOrder = "INSERT INTO Orders (userId) VALUES (?)";
            PreparedStatement orderStmt = conn.prepareStatement(insertOrder, Statement.RETURN_GENERATED_KEYS);
            orderStmt.setInt(1, userId);
            orderStmt.executeUpdate();

            ResultSet rs = orderStmt.getGeneratedKeys();
            if (rs.next()) {
                int orderId = rs.getInt(1);

                String orderDetails = "INSERT INTO Order_Details (orderId, productId, quantity) VALUES (?, ?, ?)";
                PreparedStatement detailStmt = conn.prepareStatement(orderDetails);

                for (Product p : products) {
                    detailStmt.setInt(1, orderId);
                    detailStmt.setInt(2, p.getProductId());
                    detailStmt.setInt(3, 1); // default quantity = 1, can be dynamic
                    detailStmt.addBatch();
                }

                detailStmt.executeBatch();
                conn.commit();
                System.out.println("Order placed successfully! Order ID: " + orderId);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cancelOrder(int userId, int orderId) throws UserNotFoundException, OrderNotFoundException {
        try (Connection conn = DBConnUtil.getConnection(dbProps)) {
            // Check if user exists
            if (!userExists(userId)) throw new UserNotFoundException("Invalid user.");

            // Check if order exists
            String check = "SELECT * FROM Orders WHERE orderId = ? AND userId = ?";
            PreparedStatement ps = conn.prepareStatement(check);
            ps.setInt(1, orderId);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) throw new OrderNotFoundException("Order not found for user.");

            // Cancel (delete)
            String delete = "DELETE FROM Orders WHERE orderId = ?";
            PreparedStatement delStmt = conn.prepareStatement(delete);
            delStmt.setInt(1, orderId);
            int rows = delStmt.executeUpdate();

            System.out.println(rows > 0 ? "Order canceled." : "Failed to cancel.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        String query = "SELECT * FROM Product";

        try (Connection conn = DBConnUtil.getConnection(dbProps);
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Product p = new Product();
                p.setProductId(rs.getInt("productId"));
                p.setProductName(rs.getString("productName"));
                p.setDescription(rs.getString("description"));
                p.setPrice(rs.getDouble("price"));
                p.setQuantityInStock(rs.getInt("quantityInStock"));
                p.setType(rs.getString("type"));
                list.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Product> getOrderByUser(User user) throws UserNotFoundException {
        List<Product> list = new ArrayList<>();
        Integer userId = getUserId(user.getUsername());
        if (userId == null) throw new UserNotFoundException("User not found");

        String query = """
            SELECT p.productId, p.productName, p.description, p.price, p.quantityInStock, p.type
            FROM Product p
            JOIN Order_Details od ON p.productId = od.productId
            JOIN Orders o ON od.orderId = o.orderId
            WHERE o.userId = ?
        """;

        try (Connection conn = DBConnUtil.getConnection(dbProps);
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Product p = new Product();
                p.setProductId(rs.getInt("productId"));
                p.setProductName(rs.getString("productName"));
                p.setDescription(rs.getString("description"));
                p.setPrice(rs.getDouble("price"));
                p.setQuantityInStock(rs.getInt("quantityInStock"));
                p.setType(rs.getString("type"));
                list.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // Helper methods

    private Integer getUserId(String username) {
        String query = "SELECT userId FROM User WHERE username = ?";
        try (Connection conn = DBConnUtil.getConnection(dbProps);
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("userId");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean isAdmin(String username) {
        String query = "SELECT role FROM User WHERE username = ?";
        try (Connection conn = DBConnUtil.getConnection(dbProps);
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getString("role").equalsIgnoreCase("Admin");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean userExists(int userId) {
        String query = "SELECT userId FROM User WHERE userId = ?";
        try (Connection conn = DBConnUtil.getConnection(dbProps);
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
