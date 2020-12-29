package com.ecommerce.order.order;

import com.ecommerce.order.sdk.event.order.OrderEvent;
import org.springframework.stereotype.Component;

@Component
public class OrderEventHandler {
    private final OrderRepresentationService orderRepresentationService;

    public OrderEventHandler( OrderRepresentationService orderRepresentationService ) {
        this.orderRepresentationService = orderRepresentationService;
    }

    public void cqrsSync( OrderEvent event ) {
        this.orderRepresentationService.sqrsSync( event.getOrderId( ) );
    }
}
