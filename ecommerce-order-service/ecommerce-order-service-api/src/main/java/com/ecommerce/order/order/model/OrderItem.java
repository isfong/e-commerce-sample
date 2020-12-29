package com.ecommerce.order.order.model;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class OrderItem {
    private String productId;
    private int count;
    private BigDecimal itemPrice;

    public OrderItem( String productId, int count, BigDecimal itemPrice ) {
        this.productId = productId;
        this.count = count;
        this.itemPrice = itemPrice;
    }
}
