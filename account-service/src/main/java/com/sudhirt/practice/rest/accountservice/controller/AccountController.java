package com.sudhirt.practice.rest.accountservice.controller;

import java.util.List;
import com.sudhirt.practice.rest.accountservice.entity.Account;
import com.sudhirt.practice.rest.accountservice.entity.Transaction;
import com.sudhirt.practice.rest.accountservice.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
public class AccountController {

	private AccountService accountService;

	@Autowired
	AccountController(AccountService accountService) {
		this.accountService = accountService;
	}

	@GetMapping
	List<Account> getAll() {
		List<Account> accounts = accountService.getAll();
		return accounts;
	}

	@GetMapping("/{accountNumber}")
	Account get(@PathVariable String accountNumber) {
		return accountService.get(accountNumber);
	}

	@GetMapping("/{accountNumber}/transactions")
	List<Transaction> getTransactions(@PathVariable String accountNumber) {
		return accountService.getTransactions(accountNumber);
	}

	@PostMapping("/{accountNumber}/transactions")
	ResponseEntity<?> addTransaction(@PathVariable String accountNumber, @RequestBody Transaction transaction) {
		transaction.setAccount(Account.builder().accountNumber(accountNumber).build());
		accountService.addTransaction(transaction);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

}