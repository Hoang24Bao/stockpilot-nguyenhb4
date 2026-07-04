package org.example;

import org.example.model.Order;
import org.example.model.OrderItem;
import org.example.model.Product;
import org.example.repository.OrderRepository;
import org.example.repository.ProductRepository;
import org.example.service.OrderService;
import org.example.service.ProductService;
import org.example.service.ReportService;
import org.example.util.DatabaseConnection;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // 1. Khởi tạo Cơ sở dữ liệu
        DatabaseConnection.initializeDatabase();

        try {
            org.h2.tools.Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082").start();
            System.out.println("H2 Console is running at: http://localhost:8082");
        } catch (Exception e) {
            System.err.println("Failed to start H2 Console: " + e.getMessage());
        }

        ProductRepository productRepo = new ProductRepository();
        ProductService productService = new ProductService(productRepo);

        OrderRepository orderRepo = new OrderRepository();
        OrderService orderService = new OrderService(orderRepo);

        ReportService reportService = new ReportService();

        Scanner scanner = new Scanner(System.in);
        boolean isRunning = true;

        System.out.println("==========================================");
        System.out.println("      WELCOME TO STOCKPILOT SYSTEM        ");
        System.out.println("==========================================");

        // 2. Vòng lặp Menu (CLI Loop)
        while (isRunning) {
            System.out.println("\n--- MAIN MENU ---");
            System.out.println("1. Add New Product");
            System.out.println("2. View All Products & Low Stock Alert");
            System.out.println("3. Place a New Order");
            System.out.println("4. Import products from CSV");
            System.out.println("0. Exit System");
            System.out.print("Please select an option (0-3): ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    try {
                        System.out.print("Enter SKU (Format: ABC-1234): ");
                        String sku = scanner.nextLine();
                        System.out.print("Enter Product Name: ");
                        String name = scanner.nextLine();
                        System.out.print("Enter Category: ");
                        String category = scanner.nextLine();
                        System.out.print("Enter Price: ");
                        BigDecimal price = new BigDecimal(scanner.nextLine());
                        System.out.print("Enter Initial Stock Quantity: ");
                        int stock = Integer.parseInt(scanner.nextLine());

                        Product product = new Product(null, sku, name, category, price, stock);
                        productService.addProduct(product);
                    } catch (Exception e) {
                        System.err.println("Error adding product: " + e.getMessage());
                    }
                    break;

                case "2":
                    System.out.println("\n--- PRODUCT LIST ---");
                    List<Product> allProducts = productService.getAllProducts();
                    if (allProducts.isEmpty()) {
                        System.out.println("No products found in the database.");
                    } else {
                        for (Product p : allProducts) {
                            System.out.println(p);
                        }

                        // Cảnh báo sản phẩm sắp hết hàng (tồn kho < 10)
                        System.out.println("\n[!] LOW STOCK ALERT (Under 10 items):");
                        List<Product> lowStockProducts = reportService.getLowStockProducts(allProducts, 10);
                        if (lowStockProducts.isEmpty()) {
                            System.out.println("All products have sufficient stock.");
                        } else {
                            lowStockProducts.forEach(p -> System.out.println("- " + p.getName() + " (Only " + p.getStockQuantity() + " left)"));
                        }
                    }
                    break;

                case "3":
                    try {
                        System.out.print("Enter Customer ID: ");
                        int customerId = Integer.parseInt(scanner.nextLine());

                        // Tạo đơn hàng mới
                        Order order = new Order(null, customerId, null);

                        boolean ordering = true;
                        while (ordering) {
                            System.out.print("Enter Product ID to buy (or type 0 to finish placing order): ");
                            int productId = Integer.parseInt(scanner.nextLine());
                            if (productId == 0) {
                                ordering = false;
                                break;
                            }

                            System.out.print("Enter Quantity: ");
                            int qty = Integer.parseInt(scanner.nextLine());

                            // Kiểm tra sản phẩm có tồn tại không
                            Product p = productService.getProductById(productId)
                                    .orElseThrow(() -> new RuntimeException("Product not found!"));

                            OrderItem item = new OrderItem(null, null, productId, qty, p.getPrice());
                            order.addItem(item);
                            System.out.println("Added to cart! Current Subtotal: $" + order.getTotalAmount());
                        }

                        // Tiến hành lưu đơn hàng (Test DB Transaction & Rollback)
                        if (!order.getItems().isEmpty()) {
                            orderService.placeOrder(order);
                        } else {
                            System.out.println("Order cancelled because the cart is empty.");
                        }

                    } catch (Exception e) {
                        System.err.println("Error placing order: " + e.getMessage());
                    }
                    break;

                case "4":
                    try {
                        System.out.print("Enter CSV file path (e.g., products.csv): ");
                        String path = scanner.nextLine();
                        List<Product> importedProducts = org.example.io.CsvImporter.importProducts(path);

                        for (Product p : importedProducts) {
                            productService.addProduct(p);
                        }
                        System.out.println("Imported " + importedProducts.size() + " products successfully!");
                    } catch (Exception e) {
                        System.err.println("Error importing CSV: " + e.getMessage());
                    }
                    break;

                case "0":
                    isRunning = false;
                    System.out.println("Thank you for using StockPilot! Goodbye.");
                    break;

                default:
                    System.out.println("Invalid option! Please try again.");
            }
        }

        scanner.close();
    }
}