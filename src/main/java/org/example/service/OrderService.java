package org.example.service;

import org.example.model.Order;
import org.example.repository.OrderRepository;
import java.util.List;

public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // Hàm xử lý đặt hàng
    public void placeOrder(Order order) throws Exception {
        if (order.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cannot place an empty order!");
        }

        // Gọi xuống DB để lưu giao dịch
        orderRepository.saveOrderWithTransaction(order);
        System.out.println("Order placed successfully! Order ID: " + order.getId());
    }

    // Lấy danh sách toàn bộ đơn hàng để phục vụ cho báo cáo
    public List<Order> getAllOrders() {
        return orderRepository.findAllOrdersBasic();
    }
}