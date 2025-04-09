package dao;

import java.sql.*;
import java.util.*;

import entity.Customer;
import entity.Product;
import myexceptions.CustomerNotFoundException;
import myexceptions.ProductNotFoundException;
import myexceptions.OrderNotFoundException;
import util.DBConnUtil;

public class OrderProcessorRepositoryImpl implements OrderProcessorRepository {

    private Connection conn;

    public OrderProcessorRepositoryImpl() {
        conn = DBConnUtil.getConnection("src/util/db.properties"); // adjust path if needed
    }

    @Override
    public boolean createProduct(Product product) {
        String sql = "INSERT INTO products (name, price, description, stockQuantity) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, product.getName());
            stmt.setDouble(2, product.getPrice());
            stmt.setString(3, product.getDescription());
            stmt.setInt(4, product.getStockQuantity());
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Error in createProduct: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean createCustomer(Customer customer) {
        String sql = "INSERT INTO customers (name, email, password) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getEmail());
            stmt.setString(3, customer.getPassword());

            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Error in createCustomer: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteProduct(int productId) throws ProductNotFoundException {
        String sql = "DELETE FROM products WHERE product_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new ProductNotFoundException("Product ID not found: " + productId);
            }
            return true;
        } catch (SQLException e) {
            System.out.println("Error in deleteProduct: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteCustomer(int customerId) throws CustomerNotFoundException {
        String sql = "DELETE FROM customers WHERE customer_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new CustomerNotFoundException("Customer ID not found: " + customerId);
            }
            return true;
        } catch (SQLException e) {
            System.out.println("Error in deleteCustomer: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean addToCart(Customer customer, Product product, int quantity) {
        String sql = "INSERT INTO cart (customer_id, product_id, quantity) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customer.getCustomerId());
            stmt.setInt(2, product.getProductId());
            stmt.setInt(3, quantity);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Error in addToCart: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean removeFromCart(Customer customer, Product product) {
        String sql = "DELETE FROM cart WHERE customer_id = ? AND product_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customer.getCustomerId());
            stmt.setInt(2, product.getProductId());
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Error in removeFromCart: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Product> getAllFromCart(Customer customer) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.product_id, p.name, p.price, p.description, p.stockQuantity " +
                     "FROM cart c JOIN products p ON c.product_id = p.product_id " +
                     "WHERE c.customer_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customer.getCustomerId());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Product p = new Product(
                    rs.getInt("product_id"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    rs.getString("description"),
                    rs.getInt("stockQuantity")
                );
                products.add(p);
            }
        } catch (SQLException e) {
            System.out.println("Error in getAllFromCart: " + e.getMessage());
        }
        return products;
    }

    @Override
    public boolean placeOrder(Customer customer, List<Map<Product, Integer>> productList, String shippingAddress) {
        String orderSql = "INSERT INTO orders (customer_id, order_date, total_price, shipping_address) VALUES (?, ?, ?, ?)";
        String itemSql = "INSERT INTO order_items (order_id, product_id, quantity) VALUES (?, ?, ?)";
        String clearCartSql = "DELETE FROM cart WHERE customer_id = ?";
        try {
            conn.setAutoCommit(false);

            double total = 0;
            for (Map<Product, Integer> map : productList) {
                for (Map.Entry<Product, Integer> entry : map.entrySet()) {
                    total += entry.getKey().getPrice() * entry.getValue();
                }
            }

            PreparedStatement orderStmt = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
            orderStmt.setInt(1, customer.getCustomerId());
            orderStmt.setDate(2, new java.sql.Date(System.currentTimeMillis()));
            orderStmt.setDouble(3, total);
            orderStmt.setString(4, shippingAddress);
            orderStmt.executeUpdate();

            ResultSet generatedKeys = orderStmt.getGeneratedKeys();
            int orderId = 0;
            if (generatedKeys.next()) {
                orderId = generatedKeys.getInt(1);
            }

            PreparedStatement itemStmt = conn.prepareStatement(itemSql);
            for (Map<Product, Integer> map : productList) {
                for (Map.Entry<Product, Integer> entry : map.entrySet()) {
                    itemStmt.setInt(1, orderId);
                    itemStmt.setInt(2, entry.getKey().getProductId());
                    itemStmt.setInt(3, entry.getValue());
                    itemStmt.addBatch();
                }
            }
            itemStmt.executeBatch();

            PreparedStatement clearCartStmt = conn.prepareStatement(clearCartSql);
            clearCartStmt.setInt(1, customer.getCustomerId());
            clearCartStmt.executeUpdate();

            conn.commit();
            return true;
        } catch (SQLException e) {
            System.out.println("Error in placeOrder: " + e.getMessage());
            try {
                conn.rollback();
            } catch (SQLException ex) {
                System.out.println("Rollback failed: " + ex.getMessage());
            }
            return false;
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println("Failed to reset auto-commit: " + e.getMessage());
            }
        }
    }

    @Override
    public List<Map<Product, Integer>> getOrdersByCustomer(int customerId) throws CustomerNotFoundException {
        List<Map<Product, Integer>> orders = new ArrayList<>();
        String sql = "SELECT o.order_id, p.product_id, p.name, p.price, p.description, p.stockQuantity, oi.quantity " +
                     "FROM orders o " +
                     "JOIN order_items oi ON o.order_id = oi.order_id " +
                     "JOIN products p ON oi.product_id = p.product_id " +
                     "WHERE o.customer_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Product product = new Product(
                    rs.getInt("product_id"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    rs.getString("description"),
                    rs.getInt("stockQuantity")
                );
                int quantity = rs.getInt("quantity");

                Map<Product, Integer> map = new HashMap<>();
                map.put(product, quantity);
                orders.add(map);
            }
        } catch (SQLException e) {
            System.out.println("Error in getOrdersByCustomer: " + e.getMessage());
        }
        return orders;
    }
}
