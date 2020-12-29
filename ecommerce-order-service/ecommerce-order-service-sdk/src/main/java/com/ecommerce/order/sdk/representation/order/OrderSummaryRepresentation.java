package com.ecommerce.order.sdk.representation.order;

import com.ecommerce.shared.model.Address;
import lombok.Value;

import java.math.BigDecimal;
import java.time.Instant;

@Value
public class OrderSummaryRepresentation {
    String id;
    BigDecimal totalPrice;
    String status;
    Instant createdAt;
    Address address;
}
