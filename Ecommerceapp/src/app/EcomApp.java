package app;

import dao.OrderProcessorRepository;
import dao.OrderProcessorRepositoryImpl;
import entity.Customer;
import entity.Product;
import exceptions.CustomerNotFoundException;
import exceptions.ProductNotFoundException;

import java.util.*;

public class EcomApp {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        OrderProcessorRepository repo = new OrderProcessorRepositoryImpl();

        while (true) {
            System.out.println("\n==== E-Commerce Menu ====");
            System.out.println("1. Register Customer");
            System.out.println("2. Add Product");
            System.out.println("3. Delete Product");
            System.out.println("4. Add to Cart");
            System.out.println("5. View Cart");
            System.out.println("6. Place Order");
            System.out.println("7. View Orders");
            System.out.println("8. Exit");
            System.out.print("Enter choice: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Name: ");
                    String name = sc.nextLine();
                    System.out.print("Email: ");
                    String email = sc.nextLine();
                    System.out.print("Password: ");
                    String pass = sc.nextLine();

                    Customer c = new Customer();
                    c.setName(name);
                    c.setEmail(email);
                    c.setPassword(pass);

                    if (repo.createCustomer(c))
                        System.out.println("Customer registered.");
                    else
                        System.out.println("Failed to register.");
                    break;

                case 2:
                    System.out.print("Product Name: ");
                    String pname = sc.nextLine();
                    System.out.print("Price: ");
                    double price = sc.nextDouble();
                    sc.nextLine();
                    System.out.print("Description: ");
                    String desc = sc.nextLine();
                    System.out.print("Stock Quantity: ");
                    int qty = sc.nextInt();

                    Product p = new Product();
                    p.setName(pname);
                    p.setPrice(price);
                    p.setDescription(desc);
                    p.setStockQuantity(qty);

                    if (repo.createProduct(p))
                        System.out.println("Product added.");
                    else
                        System.out.println("Failed to add product.");
                    break;

                case 3:
                    System.out.print("Product ID to delete: ");
                    int pid = sc.nextInt();
                    try {
                        if (repo.deleteProduct(pid))
                            System.out.println("Product deleted.");
                    } catch (ProductNotFoundException e) {
                        System.out.println(e.getMessage());
                    }
                    break;

                case 4:
                    System.out.print("Customer ID: ");
                    int cid = sc.nextInt();
                    System.out.print("Product ID: ");
                    int prid = sc.nextInt();
                    System.out.print("Quantity: ");
                    int q = sc.nextInt();

                    Customer cust = new Customer();
                    cust.setCustomerId(cid);
                    Product prod = new Product();
                    prod.setProductId(prid);

                    if (repo.addToCart(cust, prod, q))
                        System.out.println("Product added to cart.");
                    else
                        System.out.println("Failed to add to cart.");
                    break;

                case 5:
                    System.out.print("Customer ID: ");
                    int viewCartId = sc.nextInt();
                    Customer cartCust = new Customer();
                    cartCust.setCustomerId(viewCartId);

                    List<Product> cart = repo.getAllFromCart(cartCust);
                    System.out.println("Cart Contents:");
                    for (Product item : cart) {
                        System.out.println(item.getName() + " - " + item.getPrice());
                    }
                    break;

                case 6:
                    System.out.print("Customer ID: ");
                    int orderCustId = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Shipping Address: ");
                    String address = sc.nextLine();

                    Customer orderCust = new Customer();
                    orderCust.setCustomerId(orderCustId);

                    List<Product> cartItems = repo.getAllFromCart(orderCust);
                    List<Map<Product, Integer>> productList = new ArrayList<>();

                    for (Product item : cartItems) {
                        System.out.print("Quantity for " + item.getName() + ": ");
                        int itemQty = sc.nextInt();
                        Map<Product, Integer> map = new HashMap<>();
                        map.put(item, itemQty);
                        productList.add(map);
                    }

                    if (repo.placeOrder(orderCust, productList, address))
                        System.out.println("Order placed.");
                    else
                        System.out.println("Failed to place order.");
                    break;

                case 7:
                    System.out.print("Customer ID: ");
                    int custId = sc.nextInt();
                    try {
                        List<Map<Product, Integer>> orders = repo.getOrdersByCustomer(custId);
                        System.out.println("Orders:");
                        for (Map<Product, Integer> order : orders) {
                            for (Map.Entry<Product, Integer> entry : order.entrySet()) {
                                System.out.println(entry.getKey().getName() + " x " + entry.getValue());
                            }
                        }
                    } catch (CustomerNotFoundException e) {
                        System.out.println(e.getMessage());
                    }
                    break;

                case 8:
                    System.out.println("Exiting. Thank you!");
                    sc.close();
                    System.exit(0);
                    break;

                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }
}
