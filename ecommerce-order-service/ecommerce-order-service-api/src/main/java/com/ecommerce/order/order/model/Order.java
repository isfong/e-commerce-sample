package com.ecommerce.order.order.model;

import com.ecommerce.order.sdk.event.order.OrderCreatedEvent;
import com.ecommerce.order.sdk.representation.order.OrderSummaryRepresentation;
import com.ecommerce.shared.model.Address;
import com.ecommerce.shared.model.BaseAggregate;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class Order extends BaseAggregate {
    private String id;
    private List< OrderItem > items;
    private BigDecimal totalPrice;
    private OrderStatus status;
    private Address address;
    private Instant createdAt;

    public static Order create( String id, List< OrderItem > items, Address address ) {
        Order order = Order.builder( )
                .id( id )
                .items( items )
                .totalPrice( calculateTotalPrice( items ) )
                .status( OrderStatus.CREATED )
                .address( address )
                .createdAt( Instant.now( ) )
                .build( );
        order.raiseCreatedEvent( id, items, address );
        return order;
    }

    private static BigDecimal calculateTotalPrice( List< OrderItem > items ) {
        return items.stream( )
                .map( OrderItem::getItemPrice )
                .reduce( BigDecimal.ZERO, BigDecimal::add );
    }

    private void raiseCreatedEvent( String id, List< OrderItem > items, Address address ) {
        List< com.ecommerce.order.sdk.event.order.OrderItem > orderItems = items.stream( )
                .map( orderItem -> new com.ecommerce.order.sdk.event.order.OrderItem( orderItem.getProductId( ),
                        orderItem.getCount( ) ) )
                .collect( Collectors.toList( ) );
        raiseEvent( new OrderCreatedEvent( id, totalPrice, address, orderItems, createdAt ) );
    }

    public OrderSummaryRepresentation toSummary( ) {
        return new OrderSummaryRepresentation( this.getId( ),
                this.getTotalPrice( ),
                this.getStatus( ).name( ),
                this.getCreatedAt( ),
                this.getAddress( ) );
    }
}
