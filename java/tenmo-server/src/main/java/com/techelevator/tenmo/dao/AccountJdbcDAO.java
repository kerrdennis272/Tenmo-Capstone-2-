package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Balance;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.math.BigDecimal;

@Component
public class AccountJdbcDAO implements AccountDAO{

    private JdbcTemplate jdbcTemplate;

    public AccountJdbcDAO(DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
    }

    @Override

    public Balance getBalance(int userId) {
        Balance balance = new Balance();

        String sqlStr = "SELECT * FROM accounts WHERE user_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlStr, userId);
        if (results.next()) {
            String stringBalance = results.getString("balance");
            balance.setBalance(new BigDecimal(stringBalance));
        }
        return balance;
    }

    @Override
    public int getAccountIdByUserId(Integer userId) {
        int accountId = 0;

        String sqlStr = "Select * FROM accounts WHERE user_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlStr, userId);
        if (results.next()) {
            accountId=results.getInt("account_id");
        }
        return accountId;
    }

    @Override
    public String getUsernameFromAccountId(int accountId) {
        String username = "";
        String sqlStr = "SELECT username FROM accounts JOIN users ON accounts.user_id = users.user_id WHERE account_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlStr, accountId);
        if (results.next()) {
            username = results.getString("username");
        }
        return username;
    }




}
