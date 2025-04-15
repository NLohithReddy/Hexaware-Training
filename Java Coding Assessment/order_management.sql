-- Create Database
CREATE DATABASE order_management;
USE order_management;

-- Create Product Table
CREATE TABLE Product (
    productId INT PRIMARY KEY AUTO_INCREMENT,
    productName VARCHAR(100) NOT NULL,
    description TEXT,
    price DOUBLE NOT NULL CHECK (price >= 0),
    quantityInStock INT NOT NULL CHECK (quantityInStock >= 0),
    type VARCHAR(20) NOT NULL CHECK (type IN ('Electronics', 'Clothing'))
);

-- Create Electronics Table
CREATE TABLE Electronics (
    productId INT PRIMARY KEY,
    brand VARCHAR(50) NOT NULL,
    warrantyPeriod INT NOT NULL CHECK (warrantyPeriod >= 0),
    FOREIGN KEY (productId) REFERENCES Product(productId) ON DELETE CASCADE
);

-- Create Clothing Table
CREATE TABLE Clothing (
    productId INT PRIMARY KEY,
    size VARCHAR(10) NOT NULL,
    color VARCHAR(30) NOT NULL,
    FOREIGN KEY (productId) REFERENCES Product(productId) ON DELETE CASCADE
);

-- Create User Table
CREATE TABLE User (
    userId INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('Admin', 'User'))
);

-- Create Order Table
CREATE TABLE Orders (
    orderId INT PRIMARY KEY AUTO_INCREMENT,
    userId INT NOT NULL,
    orderDate DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (userId) REFERENCES User(userId) ON DELETE CASCADE
);

-- Create Order_Details Table
CREATE TABLE Order_Details (
    orderId INT,
    productId INT,
    quantity INT NOT NULL CHECK (quantity > 0),
    PRIMARY KEY (orderId, productId),
    FOREIGN KEY (orderId) REFERENCES Orders(orderId) ON DELETE CASCADE,
    FOREIGN KEY (productId) REFERENCES Product(productId) ON DELETE CASCADE
);