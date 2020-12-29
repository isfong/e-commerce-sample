package com.ecommerce.order.sdk.event.order;

import lombok.Value;

@Value
public class OrderItem {
    String productId;
    int count;
}
