package com.ecommerce.order.order;

import com.ecommerce.order.order.exception.OrderNotFoundException;
import com.ecommerce.order.order.model.Order;
import com.ecommerce.shared.jackson.DefaultObjectMapper;
import com.ecommerce.shared.model.BaseRepository;
import com.google.common.collect.ImmutableBiMap;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
@SuppressWarnings( { "SqlDialectInspection", "SqlNoDataSourceInspection" } )
public class OrderRepository extends BaseRepository< Order > {
    private final DefaultObjectMapper objectMapper;
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public OrderRepository( DefaultObjectMapper objectMapper, NamedParameterJdbcTemplate jdbcTemplate ) {
        this.objectMapper = objectMapper;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    protected void doSave( Order order ) {
        String sql = "INSERT INTO ORDER (ID, JSON_CONTENT) VALUES (:id, :json)" +
                "ON DUPLICATE KEY UPDATE JSON_CONTENT=:json;";
        ImmutableBiMap< String, String > ofParameter = ImmutableBiMap.of( "id", order.getId( ), "json", objectMapper.writeValueAsString( order ) );
        this.jdbcTemplate.update( sql, ofParameter );
    }

    public Order byId( String id ) {
        try {
            String sql = "SELECT JSON_CONTENT FROM ORDERS WHERE ID=:id;";
            return this.jdbcTemplate.queryForObject( sql, ImmutableBiMap.of( "id", id ), mapper( ) );
        } catch ( EmptyResultDataAccessException e ) {
            throw new OrderNotFoundException( id );
        }
    }

    private RowMapper< Order > mapper( ) {
        return ( rs, rowNum ) -> objectMapper.readValue( rs.getString( "JSON_CONTENT" ), Order.class );
    }
}
