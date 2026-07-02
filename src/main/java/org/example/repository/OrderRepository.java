package org.example.repository;

import org.example.exception.InsufficientStockException;
import org.example.model.Order;
import org.example.model.OrderItem;
import org.example.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderRepository {

    // Hàm lưu toàn bộ đơn hàng
    public void saveOrderWithTransaction(Order order) throws Exception {
        String insertOrderSql = "INSERT INTO orders (customer_id, total_amount) VALUES (?, ?)";
        String insertItemSql = "INSERT INTO order_items (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
        // Update kho: Chỉ trừ kho nếu kho hiện tại >= số lượng khách mua
        String updateStockSql = "UPDATE products SET stock_quantity = stock_quantity - ? WHERE id = ? AND stock_quantity >= ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                int orderId;

                // Lưu Order tổng
                try (PreparedStatement pstmtOrder = conn.prepareStatement(insertOrderSql, Statement.RETURN_GENERATED_KEYS)) {
                    pstmtOrder.setInt(1, order.getCustomerId());
                    pstmtOrder.setBigDecimal(2, order.getTotalAmount());
                    pstmtOrder.executeUpdate();

                    try (ResultSet rs = pstmtOrder.getGeneratedKeys()) {
                        if (rs.next()) {
                            orderId = rs.getInt(1);
                            order.setId(orderId);
                        } else {
                            throw new SQLException("Failed to retrieve Order ID.");
                        }
                    }
                }

                // Xử lý từng OrderItem: Trừ kho và Lưu lịch sử món hàng
                try (PreparedStatement pstmtItem = conn.prepareStatement(insertItemSql);
                     PreparedStatement pstmtStock = conn.prepareStatement(updateStockSql)) {

                    for (OrderItem item : order.getItems()) {
                        // Trừ kho trước
                        pstmtStock.setInt(1, item.getQuantity());
                        pstmtStock.setInt(2, item.getProductId());
                        pstmtStock.setInt(3, item.getQuantity());

                        int affectedRows = pstmtStock.executeUpdate();
                        // Nếu update không thành công (do kho < quantity)
                        if (affectedRows == 0) {
                            throw new InsufficientStockException("Product ID " + item.getProductId() + " does not have enough stock!");
                        }

                        pstmtItem.setInt(1, orderId);
                        pstmtItem.setInt(2, item.getProductId());
                        pstmtItem.setInt(3, item.getQuantity());
                        pstmtItem.setBigDecimal(4, item.getPrice());
                        pstmtItem.addBatch();
                    }
                    pstmtItem.executeBatch();
                }

                conn.commit();

            } catch (Exception e) {
                // 5. CÓ LỖI (Vd: Hết hàng) -> HỦY BỎ TẤT CẢ LỆNH ĐÃ CHẠY BÊN TRÊN
                conn.rollback();
                throw e;
            }
        }
    }

    // Lấy tất cả đơn hàng để phục vụ cho báo cáo Thống kê
    public List<Order> findAllOrdersBasic() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Order order = new Order(rs.getInt("id"), rs.getInt("customer_id"), rs.getTimestamp("order_date").toLocalDateTime());
                order.setTotalAmount(rs.getBigDecimal("total_amount"));
                orders.add(order);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching orders: " + e.getMessage(), e);
        }
        return orders;
    }
}