package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Balance;

public interface AccountDAO {

    Balance getBalance(int userId);

    int getAccountIdByUserId(Integer userId);

    String getUsernameFromAccountId(int accountId);


}


