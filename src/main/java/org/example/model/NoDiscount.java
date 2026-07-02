package org.example.model;

import java.math.BigDecimal;

public class NoDiscount implements DiscountPolicy {
    @Override
    public BigDecimal applyDiscount(BigDecimal originalTotal) {
        return originalTotal;
    }
}