package com.ecommerce.order.order;

import com.ecommerce.order.order.model.Order;
import com.ecommerce.order.order.model.OrderFactory;
import com.ecommerce.order.order.model.OrderItem;
import com.ecommerce.order.sdk.command.order.CreateOrderCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Slf4j
@Component
@Transactional
public class OrderApplicationService {
    private final OrderFactory orderFactory;
    private final OrderRepository orderRepository;

    public OrderApplicationService( OrderFactory orderFactory, OrderRepository orderRepository ) {
        this.orderFactory = orderFactory;
        this.orderRepository = orderRepository;
    }

    public String createOrder( CreateOrderCommand command ) {
        Order order = orderFactory.create( command.getItems( ).stream( )
                .map( cmd -> new OrderItem( cmd.getProductId( ), cmd.getCount( ), cmd.getItemPrice( ) ) )
                .collect( Collectors.toList( ) ), command.getAddress( ) );
        orderRepository.doSave( order );
        log.info( "Created order[{}].", order );
        return order.getId( );
    }
}
