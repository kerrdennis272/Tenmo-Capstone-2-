package com.techelevator.tenmo;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.tenmo.services.AccountService;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.AuthenticationServiceException;
import com.techelevator.tenmo.services.TransferService;
import com.techelevator.view.ConsoleService;
import org.apiguardian.api.API;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.math.BigDecimal;


public class App {

private static final String API_BASE_URL = "http://localhost:8080/";
    
    private static final String MENU_OPTION_EXIT = "Exit";
    private static final String LOGIN_MENU_OPTION_REGISTER = "Register";
	private static final String LOGIN_MENU_OPTION_LOGIN = "Login";
	private static final String[] LOGIN_MENU_OPTIONS = { LOGIN_MENU_OPTION_REGISTER, LOGIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };
	private static final String MAIN_MENU_OPTION_VIEW_BALANCE = "View your current balance";
	private static final String MAIN_MENU_OPTION_SEND_BUCKS = "Send TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS = "View your past transfers";
	private static final String MAIN_MENU_OPTION_REQUEST_BUCKS = "Request TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS = "View your pending requests";
	private static final String MAIN_MENU_OPTION_LOGIN = "Login as different user";
	private static final String[] MAIN_MENU_OPTIONS = { MAIN_MENU_OPTION_VIEW_BALANCE, MAIN_MENU_OPTION_SEND_BUCKS, MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS, MAIN_MENU_OPTION_REQUEST_BUCKS, MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS, MAIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };
	private static final String TRANSFER_MENU_OPTION_HISTORY = "View your transfer history";
	private static final String TRANSFER_MENU_OPTION_VIEW_TRANSFER = "Search by transfer ID";
	private static final String[] TRANSFER_MENU_OPTIONS  = {TRANSFER_MENU_OPTION_HISTORY, TRANSFER_MENU_OPTION_VIEW_TRANSFER};

    private AuthenticatedUser currentUser;
    private ConsoleService console;
    private AuthenticationService authenticationService;
    private TransferService transferService;
    private AccountService accountService;

    public static void main(String[] args) {
    	App app = new App(new ConsoleService(System.in, System.out), new AuthenticationService(API_BASE_URL), new TransferService(), new AccountService());
    	app.run();
    }

    public App(ConsoleService console, AuthenticationService authenticationService, TransferService transferService, AccountService accountService) {
		this.console = console;
		this.authenticationService = authenticationService;
		this.transferService = new TransferService();
		this.accountService = new AccountService();
	}

	public void run() {
		System.out.println("*********************");
		System.out.println("* Welcome to TEnmo! *");
		System.out.println("*********************");
		
		registerAndLogin();
		mainMenu();
	}

	private void mainMenu() {
		while(true) {
			String choice = (String)console.getChoiceFromOptions(MAIN_MENU_OPTIONS);
			if(MAIN_MENU_OPTION_VIEW_BALANCE.equals(choice)) {
				viewCurrentBalance();
			} else if(MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS.equals(choice)) {
				viewTransferHistory();
			} else if(MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS.equals(choice)) {
				viewPendingRequests();
			} else if(MAIN_MENU_OPTION_SEND_BUCKS.equals(choice)) {
				sendBucks();
			} else if(MAIN_MENU_OPTION_REQUEST_BUCKS.equals(choice)) {
				requestBucks();
			} else if(MAIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else {
				// the only other option on the main menu is to exit
				exitProgram();
			}
		}
	}

	private void viewCurrentBalance() {
		accountService.viewCurrentBalance(currentUser);
	}


	private void viewTransferHistory() {
    	Transfer[] transfers = transferService.listTransfers(currentUser);
		Long transferId = Long.valueOf(console.getUserInputInteger("\nPlease enter transfer ID to view details"));
		transferService.getTransferDetails(transfers, transferId, currentUser);
	}

	private void viewPendingRequests() {
		// TODO Auto-generated method stub
		
	}

	private void sendBucks() {
    	Transfer transfer = null;
		while (transfer == null) {
			transfer = collectNewTransfer();
		}
		transferService.addNewTransfer(currentUser, transfer);

		transferService.updateAccount(currentUser);

	}

	private Transfer collectNewTransfer(){
		transferService.listUsers(currentUser);
    	//transfer id, sender id
		int currentAccountId = accountService.getCurrentAccount(currentUser);

		Integer sendTo = console.getUserInputInteger("\nChoose the recipient's id");
		int sendToAccountId = accountService.getSendToAccount(currentUser, sendTo);

		String amountAsString = console.getUserInput("Enter the amount to send");
		BigDecimal amount = new BigDecimal(amountAsString);
		BigDecimal balance = accountService.getCurrentBalance(currentUser);

		if (amount.compareTo(balance) <= 0) {
			return new Transfer(currentAccountId, sendToAccountId, amount);
		} else {
			System.out.println("\nAmount to be sent cannot be greater than current account balance.");
			return null;
		}


	}

	private void requestBucks() {
		// TODO Auto-generated method stub
		
	}
	
	private void exitProgram() {
		System.exit(0);
	}

	private void registerAndLogin() {
		while(!isAuthenticated()) {
			String choice = (String)console.getChoiceFromOptions(LOGIN_MENU_OPTIONS);
			if (LOGIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else if (LOGIN_MENU_OPTION_REGISTER.equals(choice)) {
				register();
			} else {
				// the only other option on the login menu is to exit
				exitProgram();
			}
		}
	}

	private boolean isAuthenticated() {
		return currentUser != null;
	}

	private void register() {
		System.out.println("Please register a new user account");
		boolean isRegistered = false;
        while (!isRegistered) //will keep looping until user is registered
        {
            UserCredentials credentials = collectUserCredentials();
            try {
            	authenticationService.register(credentials);
            	isRegistered = true;
            	System.out.println("Registration successful. You can now login.");
            } catch(AuthenticationServiceException e) {
            	System.out.println("REGISTRATION ERROR: "+e.getMessage());
				System.out.println("Please attempt to register again.");
            }
        }
	}

	private void login() {
		System.out.println("Please log in");
		currentUser = null;
		while (currentUser == null) //will keep looping until user is logged in
		{
			UserCredentials credentials = collectUserCredentials();
		    try {
				currentUser = authenticationService.login(credentials);
			} catch (AuthenticationServiceException e) {
				System.out.println("LOGIN ERROR: "+e.getMessage());
				System.out.println("Please attempt to login again.");
			}
		}
	}
	
	private UserCredentials collectUserCredentials() {
		String username = console.getUserInput("Username");
		String password = console.getUserInput("Password");
		return new UserCredentials(username, password);
	}

	private HttpEntity makeAuthEntity() {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setBearerAuth(currentUser.getToken());
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity entity = new HttpEntity(httpHeaders);
		return entity;
	}
}
