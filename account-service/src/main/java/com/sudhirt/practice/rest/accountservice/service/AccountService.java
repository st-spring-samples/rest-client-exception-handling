package com.sudhirt.practice.rest.accountservice.service;

import java.util.List;
import javax.transaction.Transactional;
import com.sudhirt.practice.rest.accountservice.constants.TransactionType;
import com.sudhirt.practice.rest.accountservice.entity.Account;
import com.sudhirt.practice.rest.accountservice.entity.Transaction;
import com.sudhirt.practice.rest.accountservice.exception.AccountNotFoundException;
import com.sudhirt.practice.rest.accountservice.exception.InsufficientBalanceException;
import com.sudhirt.practice.rest.accountservice.repository.AccountRepository;
import com.sudhirt.practice.rest.accountservice.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AccountService {

	private AccountRepository accountRepository;

	private TransactionRepository transactionRepository;

	@Autowired
	AccountService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
		this.accountRepository = accountRepository;
		this.transactionRepository = transactionRepository;
	}

	public List<Account> getAll() {
		return accountRepository.findAll();
	}

	public Account get(String accountNumber) {
		return accountRepository.findById(accountNumber).orElseThrow(AccountNotFoundException::new);
	}

	public List<Transaction> getTransactions(String accountNumber) {
		Account account = accountRepository.findById(accountNumber).orElseThrow(AccountNotFoundException::new);
		return transactionRepository.findByAccount(account);
	}

	@Transactional
	public void addTransaction(Transaction transaction) {
		Account account = get(transaction.getAccount().getAccountNumber());
		transactionRepository.save(transaction);
		if (transaction.getTransactionType() == TransactionType.CREDIT) {
			credit(account, transaction.getTransactionAmount());
		}
		else if (transaction.getTransactionType() == TransactionType.DEBIT) {
			debit(account, transaction.getTransactionAmount());
		}
		account.setLastTransactionDate(transaction.getTransactionDate());
		accountRepository.save(account);
	}

	private void credit(Account account, Double creditAmount) {
		account.setBalance(account.getBalance() + creditAmount);
	}

	private void debit(Account account, Double debitAmount) {
		if (account.getBalance() >= debitAmount) {
			account.setBalance(account.getBalance() - debitAmount);
		}
		else {
			throw new InsufficientBalanceException("Insufficient Balance");
		}
	}

}