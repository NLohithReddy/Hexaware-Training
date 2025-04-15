package dao;

import java.sql.*;
import java.util.*;

import entity.*;
import exception.*;
import util.DBConnUtil;

public class OrderProcessor implements IOrderManagementRepository {

    @Override
    public void createUser(User user) {
        try (Connection conn = DBConnUtil.getConnection("db.properties")) {
            String query = "INSERT INTO User (userId, username, password, role) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, user.getUserId());
            ps.setString(2, user.getUsername());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getRole());
            ps.executeUpdate();
            System.out.println("User created: " + user.getUsername());
        } catch (SQLException e) {
            System.out.println("Error creating user: " + e.getMessage());
        }
    }

    @Override
    public void createProduct(User user, Product product) throws UserNotFoundException {
        if (!userExists(user)) {
            throw new UserNotFoundException("User not found: " + user.getUsername());
        }

        try (Connection conn = DBConnUtil.getConnection("db.properties")) {
            String productQuery = "INSERT INTO Product (productName, description, price, quantityInStock, type) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(productQuery, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, product.getProductName());
            ps.setString(2, product.getDescription());
            ps.setDouble(3, product.getPrice());
            ps.setInt(4, product.getQuantityInStock());
            ps.setString(5, product.getType());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            int productId = 0;
            if (rs.next()) {
                productId = rs.getInt(1);
            }

            if (product instanceof Electronics) {
                Electronics e = (Electronics) product;
                String electronicsQuery = "INSERT INTO Electronics (productId, brand, warrantyPeriod) VALUES (?, ?, ?)";
                PreparedStatement eps = conn.prepareStatement(electronicsQuery);
                eps.setInt(1, productId);
                eps.setString(2, e.getBrand());
                eps.setInt(3, e.getWarrantyPeriod());
                eps.executeUpdate();
            } else if (product instanceof Clothing) {
                Clothing c = (Clothing) product;
                String clothingQuery = "INSERT INTO Clothing (productId, size, color) VALUES (?, ?, ?)";
                PreparedStatement cps = conn.prepareStatement(clothingQuery);
                cps.setInt(1, productId);
                cps.setString(2, c.getSize());
                cps.setString(3, c.getColor());
                cps.executeUpdate();
            }

            System.out.println("Product created: " + product.getProductName());
        } catch (SQLException e) {
            System.out.println("Error creating product: " + e.getMessage());
        }
    }

    @Override
    public void createOrder(User user, List<Product> products) throws UserNotFoundException {
        if (!userExists(user)) {
            throw new UserNotFoundException("User not found: " + user.getUsername());
        }

        try (Connection conn = DBConnUtil.getConnection("db.properties")) {
            conn.setAutoCommit(false);

            String orderQuery = "INSERT INTO Orders (userId) VALUES (?)";
            PreparedStatement orderStmt = conn.prepareStatement(orderQuery, Statement.RETURN_GENERATED_KEYS);
            orderStmt.setInt(1, user.getUserId());
            orderStmt.executeUpdate();

            ResultSet rs = orderStmt.getGeneratedKeys();
            int orderId = 0;
            if (rs.next()) {
                orderId = rs.getInt(1);
            }

            String itemQuery = "INSERT INTO Order_Details (orderId, productId, quantity) VALUES (?, ?, ?)";
            PreparedStatement itemStmt = conn.prepareStatement(itemQuery);
            for (Product p : products) {
                itemStmt.setInt(1, orderId);
                itemStmt.setInt(2, p.getProductId());
                itemStmt.setInt(3, 1); // Assuming quantity = 1
                itemStmt.addBatch();
            }

            itemStmt.executeBatch();
            conn.commit();
            System.out.println("Order created successfully for user: " + user.getUsername());

        } catch (SQLException e) {
            System.out.println("Error creating order: " + e.getMessage());
        }
    }

    @Override
    public void cancelOrder(int userId, int orderId) throws UserNotFoundException, OrderNotFoundException {
        if (!userExistsById(userId)) {
            throw new UserNotFoundException("User not found: " + userId);
        }

        try (Connection conn = DBConnUtil.getConnection("db.properties")) {
            String check = "SELECT orderId FROM Orders WHERE orderId = ? AND userId = ?";
            PreparedStatement checkStmt = conn.prepareStatement(check);
            checkStmt.setInt(1, orderId);
            checkStmt.setInt(2, userId);
            ResultSet rs = checkStmt.executeQuery();
            if (!rs.next()) {
                throw new OrderNotFoundException("Order not found: " + orderId);
            }

            PreparedStatement delItems = conn.prepareStatement("DELETE FROM Order_Details WHERE orderId = ?");
            delItems.setInt(1, orderId);
            delItems.executeUpdate();

            PreparedStatement delOrder = conn.prepareStatement("DELETE FROM Orders WHERE orderId = ?");
            delOrder.setInt(1, orderId);
            delOrder.executeUpdate();

            System.out.println("Order cancelled: " + orderId);

        } catch (SQLException e) {
            System.out.println("Error cancelling order: " + e.getMessage());
        }
    }

    @Override
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();

        try (Connection conn = DBConnUtil.getConnection("db.properties")) {
            String query = "SELECT * FROM Product";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int productId = rs.getInt("productId");
                String type = rs.getString("type");
                Product product = null;

                if ("Electronics".equalsIgnoreCase(type)) {
                    PreparedStatement eps = conn.prepareStatement("SELECT * FROM Electronics WHERE productId = ?");
                    eps.setInt(1, productId);
                    ResultSet ers = eps.executeQuery();
                    if (ers.next()) {
                        product = new Electronics(productId, rs.getString("productName"), rs.getString("description"),
                                rs.getDouble("price"), rs.getInt("quantityInStock"), type,
                                ers.getString("brand"), ers.getInt("warrantyPeriod"));
                    }
                } else if ("Clothing".equalsIgnoreCase(type)) {
                    PreparedStatement cps = conn.prepareStatement("SELECT * FROM Clothing WHERE productId = ?");
                    cps.setInt(1, productId);
                    ResultSet crs = cps.executeQuery();
                    if (crs.next()) {
                        product = new Clothing(productId, rs.getString("productName"), rs.getString("description"),
                                rs.getDouble("price"), rs.getInt("quantityInStock"), type,
                                crs.getString("size"), crs.getString("color"));
                    }
                }

                if (product != null) {
                    products.add(product);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving products: " + e.getMessage());
        }

        return products;
    }

    @Override
    public List<Product> getOrderByUser(User user) throws UserNotFoundException {
        if (!userExists(user)) {
            throw new UserNotFoundException("User not found: " + user.getUsername());
        }

        List<Product> orders = new ArrayList<>();

        try (Connection conn = DBConnUtil.getConnection("db.properties")) {
            String query = """
                SELECT p.*
                FROM Orders o
                JOIN Order_Details od ON o.orderId = od.orderId
                JOIN Product p ON od.productId = p.productId
                WHERE o.userId = ?
            """;
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, user.getUserId());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String type = rs.getString("type");
                Product p = null;
                int productId = rs.getInt("productId");

                if ("Electronics".equalsIgnoreCase(type)) {
                    PreparedStatement eps = conn.prepareStatement("SELECT * FROM Electronics WHERE productId = ?");
                    eps.setInt(1, productId);
                    ResultSet ers = eps.executeQuery();
                    if (ers.next()) {
                        p = new Electronics(productId, rs.getString("productName"), rs.getString("description"),
                                rs.getDouble("price"), rs.getInt("quantityInStock"), type,
                                ers.getString("brand"), ers.getInt("warrantyPeriod"));
                    }
                } else if ("Clothing".equalsIgnoreCase(type)) {
                    PreparedStatement cps = conn.prepareStatement("SELECT * FROM Clothing WHERE productId = ?");
                    cps.setInt(1, productId);
                    ResultSet crs = cps.executeQuery();
                    if (crs.next()) {
                        p = new Clothing(productId, rs.getString("productName"), rs.getString("description"),
                                rs.getDouble("price"), rs.getInt("quantityInStock"), type,
                                crs.getString("size"), crs.getString("color"));
                    }
                }

                if (p != null) {
                    orders.add(p);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving orders: " + e.getMessage());
        }

        return orders;
    }

    private boolean userExists(User user) {
        return userExistsById(user.getUserId());
    }

    private boolean userExistsById(int userId) {
        try (Connection conn = DBConnUtil.getConnection("db.properties")) {
            String query = "SELECT * FROM User WHERE userId = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Error checking user existence: " + e.getMessage());
            return false;
        }
    }
}
