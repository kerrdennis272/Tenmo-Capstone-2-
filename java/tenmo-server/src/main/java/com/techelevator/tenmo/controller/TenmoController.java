package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDAO;
import com.techelevator.tenmo.dao.TransferDAO;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Balance;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Null;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")

public class TenmoController {

    @Autowired
    AccountDAO accountDao;
    @Autowired
    UserDao userDao;
    @Autowired
    TransferDAO transferDAO;

    @RequestMapping(path = "/balance", method = RequestMethod.GET)
    public BigDecimal getBalance (Principal principal) {
        Balance balance = accountDao.getBalance(userDao.findIdByUsername(principal.getName()));
        return balance.getBalance();
    }

    @RequestMapping(path = "/users", method = RequestMethod.GET)
    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    @RequestMapping(path = "/send-from-account", method = RequestMethod.GET)
    public int getCurrentUserId(Principal principal) {
        int userId = userDao.findIdByUsername(principal.getName());
        int accountId = accountDao.getAccountIdByUserId(userId);
        return accountId;
    }

    @RequestMapping(path = "/send-to-account/{id}", method = RequestMethod.GET)
    public int getSendToUserId(@PathVariable Integer id) {
        Integer accountId = accountDao.getAccountIdByUserId(id);
        return accountId;
    }

    @RequestMapping(path = "/transfer-list", method = RequestMethod.GET)
    public List<Transfer> getAllTransfers(Principal principal) {
        int userId = userDao.findIdByUsername(principal.getName());
        return transferDAO.getAllTransfers(accountDao.getAccountIdByUserId(userId));
    }

    @RequestMapping(path = "/add-transfer", method = RequestMethod.POST)
    public void createNewTransfer(@RequestBody @Valid Transfer transfer, Principal principal) {
        transferDAO.newTransfer(transfer, userDao.findIdByUsername(principal.getName()));
    }

    @RequestMapping(path = "/update-accounts", method = RequestMethod.PUT)
    public void updateAccounts(Principal principal) {
        transferDAO.updateFromAccount(userDao.findIdByUsername(principal.getName()));
        transferDAO.updateToAccount();
    }

    @RequestMapping(path = "view-username/{id}", method = RequestMethod.GET)
    public String getUsernameFromAccountId(@PathVariable int id) {
        return accountDao.getUsernameFromAccountId(id);
    }


}
