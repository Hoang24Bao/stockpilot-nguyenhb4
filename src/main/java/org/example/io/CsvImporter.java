package org.example.io;

import org.example.model.Product;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CsvImporter {

    public static List<Product> importProducts(String filePath) throws IOException {
        List<Product> productList = new ArrayList<>();

        // Tự động đóng file
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Tách dữ liệu bằng dấu phẩy
                String[] data = line.split(",");

                // Cấu trúc: SKU, Name, Category, Price, Stock
                String sku = data[0].trim();
                String name = data[1].trim();
                String category = data[2].trim();
                BigDecimal price = new BigDecimal(data[3].trim());
                int stock = Integer.parseInt(data[4].trim());

                productList.add(new Product(null, sku, name, category, price, stock));
            }
        }
        return productList;
    }
}