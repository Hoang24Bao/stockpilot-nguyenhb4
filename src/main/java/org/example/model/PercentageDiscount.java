package org.example.model;

import java.math.BigDecimal;
import org.example.exception.InvalidInputException;

public class PercentageDiscount implements DiscountPolicy {
    private final double percent;

    public PercentageDiscount(double percent) {
        if (percent < 0 || percent > 1) {
            throw new InvalidInputException("Discount percentage must be between 0.0 and 1.0!");
        }
        this.percent = percent;
    }

    @Override
    public BigDecimal applyDiscount(BigDecimal originalTotal) {
        BigDecimal discountAmount = originalTotal.multiply(BigDecimal.valueOf(percent));
        return originalTotal.subtract(discountAmount);
    }
}