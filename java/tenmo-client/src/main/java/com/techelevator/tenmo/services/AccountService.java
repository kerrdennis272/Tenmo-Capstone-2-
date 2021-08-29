package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

public class AccountService {

    private static final String API_BASE_URL = "http://localhost:8080/";

    RestTemplate restTemplate;

    public AccountService() {
        restTemplate = new RestTemplate();
    }

    public void viewCurrentBalance(AuthenticatedUser currentUser) {
        BigDecimal balance = restTemplate.exchange(API_BASE_URL + "balance/",
                HttpMethod.GET,
                makeAuthEntity(currentUser),
                BigDecimal.class).getBody();

        System.out.println("Your current account balance is: $" + balance);
    }

    public BigDecimal getCurrentBalance(AuthenticatedUser currentUser) {
        BigDecimal balance = restTemplate.exchange(API_BASE_URL + "balance/",
                HttpMethod.GET,
                makeAuthEntity(currentUser),
                BigDecimal.class).getBody();

        return balance;
    }


    private HttpEntity makeAuthEntity(AuthenticatedUser currentUser) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(currentUser.getToken());
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity(httpHeaders);
        return entity;
    }

    public int getCurrentAccount(AuthenticatedUser currentUser) {
        int accountId = restTemplate.exchange(API_BASE_URL+"/send-from-account",
                HttpMethod.GET,
                makeAuthEntity(currentUser),
                Integer.class).getBody();

        return accountId;
    }

    public int getSendToAccount(AuthenticatedUser currentUser, Integer sendToId) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(currentUser.getToken());
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Integer> entity = new HttpEntity(sendToId, httpHeaders);

        int accountId = restTemplate.exchange(API_BASE_URL+"/send-to-account/" + sendToId,
                HttpMethod.GET,
                entity,
                Integer.class).getBody();

        return accountId;
    }

    public String getUsernameFromAccountId(AuthenticatedUser currentUser, int accountId) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(currentUser.getToken());
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Integer> entity = new HttpEntity(accountId, httpHeaders);

        String username = restTemplate.exchange(API_BASE_URL + "/view-username/" + accountId,
                HttpMethod.GET, entity, String.class).getBody();

        return username;
    }


}
