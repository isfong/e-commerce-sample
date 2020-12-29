package com.ecommerce.order.order;

import com.ecommerce.order.order.model.Order;
import com.ecommerce.order.sdk.representation.order.OrderSummaryRepresentation;
import com.ecommerce.shared.jackson.DefaultObjectMapper;
import com.ecommerce.shared.utils.PagedResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.ImmutableMap.of;
import static java.util.Collections.emptyMap;


@Slf4j
@Service
@SuppressWarnings( { "SqlDialectInspection", "SqlNoDataSourceInspection" } )
public class OrderRepresentationService {
    private final OrderRepository orderRepository;
    private final DefaultObjectMapper objectMapper;
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public OrderRepresentationService( OrderRepository orderRepository, DefaultObjectMapper objectMapper, NamedParameterJdbcTemplate jdbcTemplate ) {
        this.orderRepository = orderRepository;
        this.objectMapper = objectMapper;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional( readOnly = true )
    public PagedResource< OrderSummaryRepresentation > findPaged( int page, int size ) {
        MapSqlParameterSource sqlParameters = new MapSqlParameterSource( );
        sqlParameters.addValue( "limit", size );
        sqlParameters.addValue( "offset", ( page - 1 ) * size );
        String SELECT_SQL = "SELECT JSON_CONTENT FROM ORDER_SUMMARY LIMIT :limit OFFSET :offset;";
        String COUNT_SQL = "SELECT COUNT(1) FROM ORDER_SUMMARY;";
        List< OrderSummaryRepresentation > representations = this.jdbcTemplate.query( SELECT_SQL,
                sqlParameters,
                ( rs, rowNum ) -> objectMapper.readValue( rs.getString( "JSON_CONTENT" ), OrderSummaryRepresentation.class ) );
        Integer count = this.jdbcTemplate.queryForObject( COUNT_SQL, emptyMap( ), Integer.class );
        return PagedResource.of( count == null ? 0 : count, page, representations );
    }

    @Transactional
    public void sqrsSync( String id ) {
        Order order = this.orderRepository.byId( id );
        OrderSummaryRepresentation summary = order.toSummary( );
        String sql = "INSERT INTO ORDER_SUMMARY (ID, JSON_CONTENT) VALUES (:id, :json) " +
                "ON DUPLICATE KEY UPDATE JSON_CONTENT=:json;";
        Map< String, String > paramMap = of( "id", summary.getId( ), "json", objectMapper.writeValueAsString( summary ) );
        jdbcTemplate.update( sql, paramMap );
        log.info( "Order[{}] summary synced due to CQRS.", id );
    }
}
