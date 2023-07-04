package com.programmers.voucher.stream.customer;

import com.programmers.voucher.domain.customer.Customer;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class JdbcCustomerStream implements CustomerStream {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public JdbcCustomerStream(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public String save(Customer customer) {
        SqlParameterSource param = new BeanPropertySqlParameterSource(customer);
        jdbcTemplate.update("INSERT INTO customers(customer_id, name, email) VALUES(:customerId, :name, :email)",
                param);
        return customer.getCustomerId();
    }

    @Override
    public Optional<Customer> findById(String customerId) {
        try {
            Customer customer = jdbcTemplate.queryForObject("SELECT * FROM customers WHERE customer_id = :customerId",
                    Map.of("customerId", customerId),
                    (rs, rowNum) -> customerRowMapper(rs, rowNum)
            );
            return Optional.of(customer);
        } catch (Exception e) {
            return Optional.empty();
        }
    }


    @Override
    public List<Customer> findAll() {
        List<Customer> customerList = jdbcTemplate.query("SELECT * FROM customers", ((rs, rowNum) -> customerRowMapper(rs, rowNum)));
        return customerList;

    }


    @Override
    public String update(String customerId, String name) {
        jdbcTemplate.update("UPDATE customers SET name = :name WHERE customer_id = :customerId",
                Map.of("name", name, "customerId", customerId));
        return customerId;
    }

    @Override
    public void deleteById(String customerId) {
        jdbcTemplate.update("DELETE FROM customers WHERE customer_id = :customerId",
                Map.of("customerId", customerId));
    }
    @Override
    public void deleteAll() {
        jdbcTemplate.update("DELETE FROM customers",
                Map.of());
    }

    private Customer customerRowMapper(ResultSet rs, int rowNum) throws SQLException {
        return new Customer(rs.getString("customer_id"), rs.getString("name"), rs.getString("email"));
    }
}
