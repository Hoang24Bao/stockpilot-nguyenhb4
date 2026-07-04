package service;

import org.example.exception.InvalidInputException;
import org.example.model.Customer;
import org.example.model.Order;
import org.example.model.PercentageDiscount;
import org.example.model.Product;
import org.example.repository.OrderRepository;
import org.example.service.OrderService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class OrderServiceTest {

    @Test
    void testPlaceEmptyOrderShouldThrowException() {
        // Arrange (Chuẩn bị)
        OrderRepository repo = new OrderRepository();
        OrderService service = new OrderService(repo);
        Order emptyOrder = new Order(1, 101, null);

        // Test khả năng ngăn chặn đặt hàng lỗi.
        assertThrows(IllegalArgumentException.class, () -> {
            service.placeOrder(emptyOrder);
        }, "Should throw exception when placing an empty order");
    }

    @Test
    void testAddProductWithInvalidPriceShouldThrowException() {
        assertThrows(InvalidInputException.class, () -> {
            // Cố tình tạo sản phẩm có giá trị là số âm (-10)
            new Product(1, "SKU-1234", "Laptop", "Electronics", new BigDecimal("-10"), 5);
        }, "Should throw InvalidInputException when price is negative");
    }

    @Test
    void testGetExistingProductById() {
        // Test tìm đúng sản phẩm thì phải trả về đối tượng Product
    }

    @Test
    void testGetNonExistentProductById() {
        // Test tìm sản phẩm không có ID trong DB thì phải trả về Optional.empty()
    }

    @Test
    void testPercentageDiscountValid() {
        // Test tính toán logic giảm giá đúng.
        PercentageDiscount discount = new PercentageDiscount(0.1); // 10%
        BigDecimal original = new BigDecimal("100");
        assertEquals(new BigDecimal("90.0"), discount.applyDiscount(original));
    }

    @Test
    void testPercentageDiscountInvalidPercent() {
        // Kiểm tra chặn chính sách giảm giá vô lý
        assertThrows(InvalidInputException.class, () -> new PercentageDiscount(1.5));
    }

    @Test
    void testInvalidEmailFormat() {
        // Kiểm tra tính năng xác thực Email (Validation) qua Regex.
        Customer customer = new Customer(1, "Nguyen", "valid@email.com", "0123456789");
        assertThrows(InvalidInputException.class, () -> customer.setEmail("invalid-email"));
    }

    @Test
    void testInvalidPhoneFormat() {
        // Kiểm tra tính năng xác thực số điện thoại qua Regex.
        assertThrows(InvalidInputException.class, () -> {
            Customer customer = new Customer(1, "Nguyen", "a@b.com", "123"); // Quá ngắn
        });
    }
}