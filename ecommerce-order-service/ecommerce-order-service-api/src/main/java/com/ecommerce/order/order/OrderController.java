package com.ecommerce.order.order;

import com.ecommerce.order.sdk.command.order.CreateOrderCommand;
import com.ecommerce.order.sdk.representation.order.OrderSummaryRepresentation;
import com.ecommerce.shared.utils.PagedResource;
import com.google.common.collect.ImmutableBiMap;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping( "/orders" )
public class OrderController {

    private final OrderApplicationService orderApplicationService;
    private final OrderRepresentationService orderRepresentationService;

    public OrderController( OrderApplicationService orderApplicationService, OrderRepresentationService orderRepresentationService ) {
        this.orderApplicationService = orderApplicationService;
        this.orderRepresentationService = orderRepresentationService;
    }

    @PostMapping
    public ResponseEntity< Map< String, String > > createOrder( @RequestBody @Valid CreateOrderCommand command ) {
        String id = this.orderApplicationService.createOrder( command );
        return ResponseEntity.created( ServletUriComponentsBuilder//
                .fromCurrentRequest( )
                .path( "/{id}" )
                .build( )
                .toUri( ) )
                .body( ImmutableBiMap.of( "id", id ) );
    }

    @GetMapping
    public ResponseEntity< PagedResource< OrderSummaryRepresentation > > pagedProducts( @RequestParam( required = false, defaultValue = "1" ) int page,
                                                                                        @RequestParam( required = false, defaultValue = "10" ) int size ) {
        return ResponseEntity
                .ok( this.orderRepresentationService.findPaged( page, size ) );
    }
}
