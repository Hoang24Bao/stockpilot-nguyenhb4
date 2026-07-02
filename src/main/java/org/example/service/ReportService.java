package org.example.service;

import org.example.model.Order;
import org.example.model.OrderItem;
import org.example.model.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportService {

    // 1. Tính tổng doanh thu của tất cả đơn hàng
    public BigDecimal calculateTotalRevenue(List<Order> orders) {
        return orders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // 2. Lọc danh sách sản phẩm sắp hết hàng (Tồn kho < ngưỡng threshold)
    public List<Product> getLowStockProducts(List<Product> products, int threshold) {
        return products.stream()
                .filter(p -> p.getStockQuantity() < threshold)
                .sorted((p1, p2) -> Integer.compare(p1.getStockQuantity(), p2.getStockQuantity()))
                .collect(Collectors.toList());
    }

    // 3. Tìm Top N sản phẩm bán chạy nhất
    public List<Map.Entry<Integer, Integer>> getTopSellingProducts(List<Order> orders, int limitN) {
        return orders.stream()
                .flatMap(order -> order.getItems().stream())

                .collect(Collectors.groupingBy(
                        OrderItem::getProductId,
                        Collectors.summingInt(OrderItem::getQuantity)
                ))

                .entrySet().stream()

                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))

                .limit(limitN)

                .collect(Collectors.toList());
    }
}