package org.example.model;

import org.example.exception.InvalidInputException;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.regex.Pattern;

public class Product {
    private Integer id;
    private String sku;
    private String name;
    private String category;
    private BigDecimal price;
    private int stockQuantity;

    // Pattern Regex check SKU
    private static final Pattern SKU_PATTERN = Pattern.compile("^[A-Z]{3}-\\d{4}$");

    public Product(Integer id, String sku, String name, String category, BigDecimal price, int stockQuantity) {
        this.id = id;
        setSku(sku);
        this.name = name;
        this.category = category;
        setPrice(price);
        setStockQuantity(stockQuantity);
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getSku() { return sku; }

    public void setSku(String sku) {
        if (sku == null || !SKU_PATTERN.matcher(sku).matches()) {
            throw new InvalidInputException("Invalid SKU code! The required format is 3 uppercase letters and 4 digits, for example: ABC-1234");
        }
        this.sku = sku;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public BigDecimal getPrice() { return price; }

    public void setPrice(BigDecimal price) {
        // Kiểm tra giá không được null và không được nhỏ hơn 0
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidInputException("Price cannot be negative or null!");
        }
        this.price = price;
    }

    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) {
        // Kiểm tra số lượng tồn kho không được âm
        if (stockQuantity < 0) {
            throw new InvalidInputException("Stock quantity cannot be negative!");
        }
        this.stockQuantity = stockQuantity;
    }

    @Override
    public String toString() {
        return String.format("Product[ID=%d, SKU=%s, Name='%s', Price=%s, Stock=%d]",
                id, sku, name, price, stockQuantity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id) && Objects.equals(sku, product.sku);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sku);
    }
}