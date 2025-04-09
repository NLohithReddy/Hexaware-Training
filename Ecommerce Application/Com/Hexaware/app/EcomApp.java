package app;

import dao.OrderProcessorRepository;
import dao.OrderProcessorRepositoryImpl;
import entity.Customer;
import entity.Product;
import myexceptions.CustomerNotFoundException;
import myexceptions.ProductNotFoundException;

import java.util.*;

public class EcomApp {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        OrderProcessorRepository repo = new OrderProcessorRepositoryImpl();

        while (true) {
            System.out.println("\n===== E-Commerce Menu =====");
            System.out.println("1. Register Customer");
            System.out.println("2. Create Product");
            System.out.println("3. Delete Product");
            System.out.println("4. Add to Cart");
            System.out.println("5. View Cart");
            System.out.println("6. Place Order");
            System.out.println("7. View Customer Orders");
            System.out.println("8. Exit");
            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();
            sc.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter name: ");
                    String name = sc.nextLine();
                    System.out.print("Enter email: ");
                    String email = sc.nextLine();
                    System.out.print("Enter password: ");
                    String pass = sc.nextLine();

                    Customer c = new Customer();
                    c.setName(name);
                    c.setEmail(email);
                    c.setPassword(pass);

                    if (repo.createCustomer(c))
                        System.out.println("✅ Customer registered.");
                    else
                        System.out.println("❌ Failed to register.");
                    break;

                case 2:
                    System.out.print("Enter product name: ");
                    String pname = sc.nextLine();
                    System.out.print("Enter price: ");
                    double price = sc.nextDouble();
                    sc.nextLine(); // consume newline
                    System.out.print("Enter description: ");
                    String desc = sc.nextLine();
                    System.out.print("Enter stock quantity: ");
                    int qty = sc.nextInt();

                    Product p = new Product();
                    p.setName(pname);
                    p.setPrice(price);
                    p.setDescription(desc);
                    p.setStockQuantity(qty);

                    if (repo.createProduct(p))
                        System.out.println("✅ Product created.");
                    else
                        System.out.println("❌ Failed to create product.");
                    break;

                case 3:
                    System.out.print("Enter product ID to delete: ");
                    int pid = sc.nextInt();
                    try {
                        if (repo.deleteProduct(pid))
                            System.out.println("✅ Product deleted.");
                    } catch (ProductNotFoundException e) {
                        System.out.println("❌ " + e.getMessage());
                    }
                    break;

                case 4:
                    System.out.print("Enter customer ID: ");
                    int custId = sc.nextInt();
                    System.out.print("Enter product ID: ");
                    int prodId = sc.nextInt();
                    System.out.print("Enter quantity: ");
                    int q = sc.nextInt();

                    Customer cust = new Customer();
                    cust.setCustomerId(custId);
                    Product prod = new Product();
                    prod.setProductId(prodId);

                    if (repo.addToCart(cust, prod, q))
                        System.out.println("✅ Product added to cart.");
                    else
                        System.out.println("❌ Failed to add to cart.");
                    break;

                case 5:
                    System.out.print("Enter customer ID: ");
                    int cid = sc.nextInt();
                    Customer cust2 = new Customer();
                    cust2.setCustomerId(cid);
                    List<Product> cartItems = repo.getAllFromCart(cust2);
                    System.out.println("🛒 Cart Items:");
                    for (Product prodItem : cartItems) {
                        System.out.println("-> " + prodItem.getName() + " - ₹" + prodItem.getPrice());
                    }
                    break;

                case 6:
                    System.out.print("Enter customer ID: ");
                    int ocid = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Enter shipping address: ");
                    String address = sc.nextLine();

                    Customer orderCust = new Customer();
                    orderCust.setCustomerId(ocid);

                    List<Product> cartList = repo.getAllFromCart(orderCust);
                    List<Map<Product, Integer>> orderList = new ArrayList<>();

                    for (Product item : cartList) {
                        System.out.print("Enter quantity for " + item.getName() + ": ");
                        int itemQty = sc.nextInt();
                        Map<Product, Integer> map = new HashMap<>();
                        map.put(item, itemQty);
                        orderList.add(map);
                    }

                    if (repo.placeOrder(orderCust, orderList, address))
                        System.out.println("✅ Order placed!");
                    else
                        System.out.println("❌ Order failed.");
                    break;

                case 7:
                    System.out.print("Enter customer ID: ");
                    int viewId = sc.nextInt();
                    try {
                        List<Map<Product, Integer>> orders = repo.getOrdersByCustomer(viewId);
                        System.out.println("📦 Orders:");
                        for (Map<Product, Integer> order : orders) {
                            for (Map.Entry<Product, Integer> entry : order.entrySet()) {
                                System.out.println("-> " + entry.getKey().getName() + " x" + entry.getValue());
                            }
                        }
                    } catch (CustomerNotFoundException e) {
                        System.out.println("❌ " + e.getMessage());
                    }
                    break;

                case 8:
                    System.out.println("👋 Exiting... Thank you!");
                    sc.close();
                    System.exit(0);
                    break;

                default:
                    System.out.println("❌ Invalid option. Try again.");
            }
        }
    }
}
