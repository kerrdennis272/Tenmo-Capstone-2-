package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.util.List;

public interface TransferDAO {

    void newTransfer(Transfer transfer, int userId);
    void updateFromAccount(int userId);
    void updateToAccount();
    List<Transfer> getAllTransfers(int accountId);
}
