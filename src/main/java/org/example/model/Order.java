package org.example.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private Integer id;
    private Integer customerId;
    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    private List<OrderItem> items;
    private DiscountPolicy discountPolicy;

    public Order(Integer id, Integer customerId, LocalDateTime orderDate) {
        this.id = id;
        this.customerId = customerId;
        this.orderDate = orderDate != null ? orderDate : LocalDateTime.now();
        this.totalAmount = BigDecimal.ZERO;
        this.items = new ArrayList<>();

        this.discountPolicy = new NoDiscount();
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public List<OrderItem> getItems() { return items; }

    public DiscountPolicy getDiscountPolicy() { return discountPolicy; }

    public void setDiscountPolicy(DiscountPolicy discountPolicy) {
        this.discountPolicy = discountPolicy;
        recalculateTotal();
    }

    // Thêm một mặt hàng vào đơn
    public void addItem(OrderItem item) {
        this.items.add(item);
        recalculateTotal();
    }

    private void recalculateTotal() {
        BigDecimal subTotal = BigDecimal.ZERO;

        // 1. Tính tổng tiền các món hàng
        for (OrderItem item : items) {
            subTotal = subTotal.add(item.getTotalPrice());
        }

        // 2. Áp dụng chính sách giảm giá hiện tại
        this.totalAmount = discountPolicy.applyDiscount(subTotal);
    }

    @Override
    public String toString() {
        return String.format("Order[ID=%d, CustomerID=%d, Date=%s, Total=%s, Items=%d]",
                id, customerId, orderDate, totalAmount, items.size());
    }
}