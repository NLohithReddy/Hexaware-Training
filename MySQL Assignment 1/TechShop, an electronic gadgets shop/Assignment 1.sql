CREATE DATABASE TechShop;
USE TechShop;


CREATE TABLE Customers (
    CustomerID INT AUTO_INCREMENT PRIMARY KEY,
    FirstName VARCHAR(50) NOT NULL,
    LastName VARCHAR(50) NOT NULL,
    Email VARCHAR(100) UNIQUE NOT NULL,
    Phone VARCHAR(15) UNIQUE NOT NULL,
    Address TEXT
);


CREATE TABLE Products (
    ProductID INT AUTO_INCREMENT PRIMARY KEY,
    ProductName VARCHAR(100) NOT NULL,
    Description TEXT,
    Price DECIMAL(10,2) NOT NULL
);


CREATE TABLE Orders (
    OrderID INT AUTO_INCREMENT PRIMARY KEY,
    CustomerID INT NOT NULL,
    OrderDate DATE NOT NULL,
    TotalAmount DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (CustomerID) REFERENCES Customers(CustomerID)
);

CREATE TABLE OrderDetails (
    OrderDetailID INT AUTO_INCREMENT PRIMARY KEY,
    OrderID INT NOT NULL,
    ProductID INT NOT NULL,
    Quantity INT NOT NULL CHECK (Quantity > 0),
    FOREIGN KEY (OrderID) REFERENCES Orders(OrderID) ,
    FOREIGN KEY (ProductID) REFERENCES Products(ProductID) 
);


CREATE TABLE Inventory (
    InventoryID INT AUTO_INCREMENT PRIMARY KEY,
    ProductID INT NOT NULL,
    QuantityInStock INT NOT NULL CHECK (QuantityInStock >= 0),
    LastStockUpdate DATE NOT NULL,
    FOREIGN KEY (ProductID) REFERENCES Products(ProductID) 
);


INSERT INTO Customers (FirstName, LastName, Email, Phone, Address) VALUES
('Ram', 'Kumar', 'ram.kumar@example.com', '9876543210', '123 Elm St, NY'),
('Anitha', 'Sharma', 'anitha.sharma@example.com', '9876543211', '456 Maple St, CA'),
('Raghu', 'Varma', 'raghu.varma@example.com', '9876543212', '789 Oak St, TX'),
('Divya', 'Reddy', 'divya.reddy@example.com', '9876543213', '101 Pine St, FL'),
('Ajay', 'Naidu', 'ajay.naidu@example.com', '9876543214', '202 Cedar St, WA'),
('Soumya', 'Choudhary', 'soumi.choudhary@example.com', '9876543215', '303 Birch St, NV'),
('Venu', 'Prasad', 'venu.prasad@example.com', '9876543216', '404 Spruce St, CO'),
('Madhu', 'Krishna', 'madhu.krishna@example.com', '9876543217', '505 Redwood St, MA'),
('Swathi', 'Gupta', 'swathi.gupta@example.com', '9876543218', '606 Fir St, OR'),
('Vikram', 'Singh', 'vikram.singh@example.com', '9876543219', '707 Palm St, AZ');


INSERT INTO Products (ProductName, Description, Price) VALUES
('Smartphone', 'Latest Android smartphone', 699.99),
('Laptop', 'High-performance gaming laptop', 1299.99),
('Tablet', '10-inch screen tablet', 399.99),
('Smartwatch', 'Fitness tracking smartwatch', 199.99),
('Headphones', 'Noise-cancelling headphones', 149.99),
('Bluetooth Speaker', 'Portable wireless speaker', 89.99),
('Gaming Mouse', 'Ergonomic gaming mouse', 49.99),
('Mechanical Keyboard', 'RGB mechanical keyboard', 99.99),
('Monitor', '27-inch 4K UHD monitor', 349.99),
('External Hard Drive', '1TB external storage', 79.99);



INSERT INTO Orders (CustomerID, OrderDate, TotalAmount) VALUES
(1, '2025-03-01', 849.98),
(2, '2025-03-02', 1499.98),
(3, '2025-03-03', 449.98),
(4, '2025-03-04', 199.99),
(5, '2025-03-05', 699.99),
(6, '2025-03-06', 79.99),
(7, '2025-03-07', 1299.99),
(8, '2025-03-08', 349.99),
(9, '2025-03-09', 99.99),
(10, '2025-03-10', 49.99);



INSERT INTO OrderDetails (OrderID, ProductID, Quantity) VALUES
(1, 1, 1),
(1, 5, 1),
(2, 2, 1),
(2, 8, 1),
(3, 3, 1),
(3, 4, 1),
(4, 4, 1),
(5, 1, 1),
(6, 10, 1),
(7, 2, 1),
(8, 9, 1),
(9, 8, 1),
(10, 7, 1);



INSERT INTO Inventory (ProductID, QuantityInStock, LastStockUpdate) VALUES
(1, 50, '2025-03-01'),
(2, 30, '2025-03-02'),
(3, 40, '2025-03-03'),
(4, 25, '2025-03-04'),
(5, 35, '2025-03-05'),
(6, 60, '2025-03-06'),
(7, 45, '2025-03-07'),
(8, 20, '2025-03-08'),
(9, 15, '2025-03-09'),
(10, 55, '2025-03-10');
