package org.example.model;

import java.math.BigDecimal;

@FunctionalInterface
public interface DiscountPolicy {
    BigDecimal applyDiscount(BigDecimal originalTotal);
}