package main;

import dao.OrderProcessor;
import entity.*;
import exception.*;
import java.util.*;

public class MainModule {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        OrderProcessor orderProcessor = new OrderProcessor();

        while (true) {
            System.out.println("\n===== Order Management System =====");
            System.out.println("1. Create User");
            System.out.println("2. Create Product (Admin only)");
            System.out.println("3. Place Order");
            System.out.println("4. Cancel Order");
            System.out.println("5. View All Products");
            System.out.println("6. View User's Order History");
            System.out.println("7. Exit");
            System.out.print("Select an option: ");
            int choice = scanner.nextInt();

            try {
                switch (choice) {
                    case 1:
                        System.out.print("Enter user ID: ");
                        int userId = scanner.nextInt();
                        scanner.nextLine(); 
                        System.out.print("Enter username: ");
                        String username = scanner.nextLine();
                        System.out.print("Enter password: ");
                        String password = scanner.nextLine();
                        System.out.print("Enter role (Admin/User): ");
                        String role = scanner.nextLine();

                        User user = new User(userId, username, password, role);
                        orderProcessor.createUser(user);
                        break;

                    case 2:
                        System.out.print("Enter admin user ID: ");
                        int adminId = scanner.nextInt();
                        scanner.nextLine();
                        System.out.print("Enter username: ");
                        String adminUsername = scanner.nextLine();
                        System.out.print("Enter password: ");
                        String adminPassword = scanner.nextLine();
                        User admin = new User(adminId, adminUsername, adminPassword, "Admin");

                        System.out.print("Enter product ID: ");
                        int productId = scanner.nextInt();
                        scanner.nextLine();
                        System.out.print("Enter product name: ");
                        String productName = scanner.nextLine();
                        System.out.print("Enter description: ");
                        String description = scanner.nextLine();
                        System.out.print("Enter price: ");
                        double price = scanner.nextDouble();
                        System.out.print("Enter quantity: ");
                        int quantity = scanner.nextInt();
                        scanner.nextLine();
                        System.out.print("Enter type (Electronics/Clothing): ");
                        String type = scanner.nextLine();

                        Product product;
                        if (type.equalsIgnoreCase("Electronics")) {
                            System.out.print("Enter brand: ");
                            String brand = scanner.nextLine();
                            System.out.print("Enter warranty period: ");
                            int warranty = scanner.nextInt();
                            product = new Electronics(productId, productName, description, price, quantity, type, brand, warranty);
                        } else {
                            System.out.print("Enter size: ");
                            String size = scanner.nextLine();
                            System.out.print("Enter color: ");
                            String color = scanner.nextLine();
                            product = new Clothing(productId, productName, description, price, quantity, type, size, color);
                        }

                        orderProcessor.createProduct(admin, product);
                        break;

                    case 3:
                        System.out.print("Enter user ID: ");
                        int buyerId = scanner.nextInt();
                        scanner.nextLine();
                        System.out.print("Enter username: ");
                        String buyerUsername = scanner.nextLine();
                        System.out.print("Enter password: ");
                        String buyerPassword = scanner.nextLine();
                        User buyer = new User(buyerId, buyerUsername, buyerPassword, "User");

                        List<Product> selectedProducts = new ArrayList<>();
                        System.out.print("How many products do you want to order? ");
                        int count = scanner.nextInt();

                        for (int i = 0; i < count; i++) {
                            System.out.print("Enter product ID to order: ");
                            int pid = scanner.nextInt();
                            Product found = null;
                            for (Product p : orderProcessor.getAllProducts()) {
                                if (p.getProductId() == pid) {
                                    found = p;
                                    break;
                                }
                            }
                            if (found != null) {
                                selectedProducts.add(found);
                            } else {
                                System.out.println("Product with ID " + pid + " not found.");
                            }
                        }

                        orderProcessor.createOrder(buyer, selectedProducts);
                        break;

                    case 4:
                        System.out.print("Enter user ID: ");
                        int cancelUserId = scanner.nextInt();
                        System.out.print("Enter order index to cancel (0-based): ");
                        int cancelIndex = scanner.nextInt();
                        orderProcessor.cancelOrder(cancelUserId, cancelIndex);
                        break;

                    case 5:
                        List<Product> products = orderProcessor.getAllProducts();
                        for (Product p : products) {
                            System.out.println(p.getProductId() + " - " + p.getProductName() + " | Price: " + p.getPrice());
                        }
                        break;

                    case 6:
                        System.out.print("Enter user ID: ");
                        int histId = scanner.nextInt();
                        scanner.nextLine();
                        System.out.print("Enter username: ");
                        String histUser = scanner.nextLine();
                        System.out.print("Enter password: ");
                        String histPass = scanner.nextLine();
                        User hist = new User(histId, histUser, histPass, "User");

                        List<Product> orders = orderProcessor.getOrderByUser(hist);
                        if (orders.isEmpty()) {
                            System.out.println("No orders found.");
                        } else {
                            for (int i = 0; i < orders.size(); i++) {
                                Product o = orders.get(i);
                                System.out.println(i + ": " + o.getProductName() + " - " + o.getPrice());
                            }
                        }
                        break;

                    case 7:
                        System.out.println("Exiting program.");
                        System.exit(0);

                    default:
                        System.out.println("Invalid choice.");
                }

            } catch (UserNotFoundException | OrderNotFoundException e) {
                System.out.println("Error: " + e.getMessage());
            }

        }
    }
}
