package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import jdk.swing.interop.SwingInterOpUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

public class TransferService {

    private static final String API_BASE_URL = "http://localhost:8080/";

    RestTemplate restTemplate;
    AccountService accountService;

    public TransferService() {
        this.restTemplate = new RestTemplate();
        this.accountService = new AccountService();
    }

    public void listUsers(AuthenticatedUser currentUser) {
        User[] userList = restTemplate.exchange(API_BASE_URL + "/users", HttpMethod.GET, makeAuthEntity(currentUser), User[].class).getBody();
        System.out.println("-------------------------------------------");
        System.out.println("Users");
        System.out.println("ID          Name");
        System.out.println("-------------------------------------------");
        for (User user : userList) {
            System.out.println(user.getId() + "         " + user.getUsername());
        }
    }

    public Transfer[] listTransfers(AuthenticatedUser currentUser) {
        int accountId = accountService.getCurrentAccount(currentUser);
        Transfer[] transferList = restTemplate.exchange(API_BASE_URL + "/transfer-list", HttpMethod.GET,
                makeAuthEntity(currentUser), Transfer[].class).getBody();
        System.out.println("-------------------------------------------");
        System.out.println("Transfers");
        System.out.println("ID          From/To                 Amount");
        System.out.println("-------------------------------------------");
        for (Transfer transfer : transferList) {
            if (transfer.getAccountFromId() == accountId) {
                System.out.println(transfer.getTransferId() + "           From: " + currentUser.getUser().getUsername() +
                        "         $" + transfer.getAmount());
            }
            if (transfer.getAccountToId() == accountId) {
                System.out.println(transfer.getTransferId() + "           To: " + currentUser.getUser().getUsername() +
                        "           $" + transfer.getAmount());
            }
        }
        return transferList;
    }

    public void getTransferDetails(Transfer[] transfers, Long transferId, AuthenticatedUser currentUser) {
        Transfer transfer = new Transfer();
        for (int i = 0; i < transfers.length; i++) {
            if (transfers[i].getTransferId().equals(transferId)) {
                transfer = transfers[i];
            }
        }
        String type = "";
        String status = "";
        if (transfer.getTransferTypeId() == 1) {
            type = "Request";
        } else {
            type = "Send";
        }
        if (transfer.getTransferStatusId() == 1) {
            status = "Pending";
        } else if (transfer.getTransferStatusId() == 2) {
            status = "Approved";
        } else {
            status = "Rejected";
        }
        System.out.println("--------------------------------------------");
        System.out.println("Transfer Details");
        System.out.println("--------------------------------------------");
        System.out.println("Id: " + transfer.getTransferId());
        System.out.println("From: " + accountService.getUsernameFromAccountId(currentUser, transfer.getAccountFromId()));
        System.out.println("To: " + accountService.getUsernameFromAccountId(currentUser, transfer.getAccountToId()));
        System.out.println("Type: " + type);
        System.out.println("Status: " + status);
        System.out.println("Amount: " + transfer.getAmount());

    }



    private HttpEntity makeAuthEntity(AuthenticatedUser currentUser) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(currentUser.getToken());
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity(httpHeaders);
        return entity;
    }

    public Transfer addNewTransfer(AuthenticatedUser currentUser, Transfer transfer){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(currentUser.getToken());
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Transfer> entity = new HttpEntity(transfer, httpHeaders);

        restTemplate.exchange(API_BASE_URL+"/add-transfer",
                HttpMethod.POST,
                entity,
                Integer.class).getBody();

        return transfer;
    }

    public void updateAccount(AuthenticatedUser currentUser) {
        restTemplate.exchange(API_BASE_URL+ "/update-accounts", HttpMethod.PUT, makeAuthEntity(currentUser), AuthenticatedUser.class).getBody();

    }

}
