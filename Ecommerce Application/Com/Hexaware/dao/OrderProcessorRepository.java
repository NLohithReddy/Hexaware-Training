package dao;

import java.util.List;
import java.util.Map;

import entity.Customer;
import entity.Product;
import exceptions.CustomerNotFoundException;
import exceptions.ProductNotFoundException;
import exceptions.OrderNotFoundException;

public interface OrderProcessorRepository {

    boolean createProduct(Product product);
    boolean createCustomer(Customer customer);
    boolean deleteProduct(int productId) throws ProductNotFoundException;
    boolean deleteCustomer(int customerId) throws CustomerNotFoundException;

    boolean addToCart(Customer customer, Product product, int quantity);
    boolean removeFromCart(Customer customer, Product product);

    List<Product> getAllFromCart(Customer customer);

    boolean placeOrder(Customer customer, List<Map<Product, Integer>> productList, String shippingAddress);

    List<Map<Product, Integer>> getOrdersByCustomer(int customerId) throws CustomerNotFoundException;
}
