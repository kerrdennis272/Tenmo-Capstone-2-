package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Balance;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class TransferJdbcDAO implements TransferDAO {

    private JdbcTemplate jdbcTemplate;
    @Autowired
    private UserDao userDao;
    @Autowired
    private AccountDAO accountDao;

    public TransferJdbcDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void newTransfer(Transfer transfer, int userId) {

        String sqlStr = "INSERT INTO transfers (transfer_type_id, transfer_status_id, account_from, " +
                "account_to, amount) " +
                "VALUES (?, ?, ?, ?, ?)";
            jdbcTemplate.update(sqlStr, transfer.getTransferTypeId(), transfer.getTransferStatusId(), transfer.getAccountFromId(), transfer.getAccountToId(), transfer.getAmount());

    }

    @Override
    public void updateFromAccount(int userId) {
        Transfer transfer = null;
        String sqlString = "SELECT * FROM transfers ORDER BY transfer_id DESC LIMIT 1";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlString);
        if (results.next()) {
            transfer = mapRowToTransfer(results);
        }

        if (transfer != null) {
            BigDecimal updatedBalance = accountDao.getBalance(userId).getBalance().subtract(transfer.getAmount());
            String sqlStr = "UPDATE accounts SET balance = ? WHERE account_id = ?";
            jdbcTemplate.update(sqlStr, updatedBalance, transfer.getAccountFromId());
        }
    }

    @Override
    public void updateToAccount() {
        Transfer transfer = null;
        String sqlString = "SELECT * FROM transfers ORDER BY transfer_id DESC LIMIT 1";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlString);
        if (results.next()) {
            transfer = mapRowToTransfer(results);
        }
        String selectUserId = "SELECT user_id FROM accounts WHERE account_id = ?";
        SqlRowSet recipientResults = jdbcTemplate.queryForRowSet(selectUserId, transfer.getAccountToId());
        int recipientId = 0;

        if (recipientResults.next()) {
            recipientId = recipientResults.getInt("user_id");
        }
        if (transfer != null) {
            BigDecimal updatedBalance = accountDao.getBalance(recipientId).getBalance().add(transfer.getAmount());
            String sqlStr = "UPDATE accounts SET balance = ? WHERE account_id = ?";
            jdbcTemplate.update(sqlStr, updatedBalance, transfer.getAccountToId());
        }
    }

    @Override
    public List<Transfer> getAllTransfers(int accountId) {
        List<Transfer> transfers = new ArrayList<Transfer>();
        String sqlStr = "SELECT * FROM transfers WHERE account_from = ? OR account_to = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlStr, accountId, accountId);
        while (results.next()) {
            transfers.add(mapRowToTransfer(results));
        }
        return transfers;
    }

    public Transfer mapRowToTransfer(SqlRowSet sqlRowSet) {
        Transfer transfer = new Transfer();
        transfer.setTransferId(sqlRowSet.getLong("transfer_id"));
        transfer.setTransferTypeId(sqlRowSet.getInt("transfer_type_id"));
        transfer.setTransferStatusId(sqlRowSet.getInt("transfer_status_id"));
        transfer.setAccountFromId(sqlRowSet.getInt("account_from"));
        transfer.setAccountToId(sqlRowSet.getInt("account_to"));
        transfer.setAmount(sqlRowSet.getBigDecimal("amount"));

        return transfer;
    }

}
