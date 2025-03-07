package com.group.libraryapp.repository.assignment;

import com.group.libraryapp.dto.assignment.request.FruitInformationRequest;
import com.group.libraryapp.dto.assignment.request.FruitUpdateRequest;
import com.group.libraryapp.dto.assignment.response.FruitSellMoney;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class FruitJdbcRepository {

    private JdbcTemplate jdbcTemplate;

    public FruitJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public void fruitSave (FruitInformationRequest request) {

        String sql = "INSERT INTO fruit (name, warehousingDate, price) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, request.getName() , request.getWarehousingDate(), request.getPrice());

    }

    public void fruitUpdate (FruitUpdateRequest request) {
        String sql = "UPDATE fruit SET sell = 1 WHERE id = ?";
        jdbcTemplate.update(sql,request.getId());
    }

    public FruitSellMoney sellMoney(String name){

        String sql1 = "SELECT SUM(price) FROM fruit WHERE sell = 1 and name = ?";
        long salesAmount = jdbcTemplate.queryForObject(sql1, Long.class, name);
        String sql2 = "SELECT SUM(price) FROM fruit WHERE sell = 0 and name = ?";
        long notSalesAmount = jdbcTemplate.queryForObject(sql2, Long.class, name);

        return new FruitSellMoney(salesAmount, notSalesAmount);

    }
}
