package com.sudhirt.practice.rest.accountservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import java.time.LocalDate;
import javax.transaction.Transactional;
import com.sudhirt.practice.rest.accountservice.constants.TransactionType;
import com.sudhirt.practice.rest.accountservice.entity.Account;
import com.sudhirt.practice.rest.accountservice.entity.Transaction;
import com.sudhirt.practice.rest.accountservice.exception.AccountNotFoundException;
import com.sudhirt.practice.rest.accountservice.exception.InsufficientBalanceException;
import com.sudhirt.practice.rest.accountservice.repository.AccountRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class AccountServiceTests {

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private AccountService accountService;

	private Account createAccount(Double balance) {
		return createAccount("123456", balance);
	}

	private Account createAccount(String accountNumber, Double balance) {
		Account account = Account.builder().accountNumber(accountNumber).balance(balance).build();
		return accountRepository.save(account);
	}

	@Test
	public void getAll_should_return_empty_array_when_no_accounts_are_available() {
		assertThat(accountService.getAll()).hasSize(0);
	}

	@Test
	public void getAll_should_return_all_available_accounts() {
		createAccount("1234567", 0d);
		createAccount("1234568", 100d);
		createAccount("1234569", 1000d);
		assertThat(accountService.getAll()).hasSize(3);
	}

	@Test
	public void account_balance_should_be_increased_when_credit_transaction_is_added() {
		createAccount(0d);
		Transaction transaction = Transaction.builder().account(Account.builder().accountNumber("123456").build())
				.transactionType(TransactionType.CREDIT).transactionAmount(100d).transactionDate(LocalDate.now())
				.build();
		accountService.addTransaction(transaction);
		Account account = accountRepository.getOne("123456");
		assertThat(account.getBalance()).isEqualTo(100d);
	}

	@Test
	public void account_balance_should_be_decreased_when_credit_transaction_is_added() {
		createAccount(100d);
		Transaction transaction = Transaction.builder().account(Account.builder().accountNumber("123456").build())
				.transactionType(TransactionType.DEBIT).transactionAmount(100d).transactionDate(LocalDate.now())
				.build();
		accountService.addTransaction(transaction);
		Account account = accountRepository.getOne("123456");
		assertThat(account.getBalance()).isEqualTo(0d);
	}

	@Test
	public void should_throw_InsufficientBalanceException_when_debit_amount_is_more_than_balance() {
		createAccount(100d);
		Transaction transaction = Transaction.builder().account(Account.builder().accountNumber("123456").build())
				.transactionType(TransactionType.DEBIT).transactionAmount(101d).transactionDate(LocalDate.now())
				.build();
		assertThatExceptionOfType(InsufficientBalanceException.class)
				.isThrownBy(() -> accountService.addTransaction(transaction)).withMessage("Insufficient Balance");
	}

	@Test
	public void should_throw_AccountNotFoundException_when_account_does_not_exist() {
		Transaction transaction = Transaction.builder().account(Account.builder().accountNumber("234567").build())
				.transactionType(TransactionType.DEBIT).transactionAmount(101d).transactionDate(LocalDate.now())
				.build();
		assertThatExceptionOfType(AccountNotFoundException.class)
				.isThrownBy(() -> accountService.addTransaction(transaction)).withMessage("Account not found");
	}

	@Test
	public void should_throw_AccountNotFoundException_when_retrieving_transactions_for_non_existing_account() {
		assertThatExceptionOfType(AccountNotFoundException.class)
				.isThrownBy(() -> accountService.getTransactions("1234567")).withMessage("Account not found");
	}

	@Test
	public void should_return_transactions_of_given_account_when_only_one_account_exists() {
		Account account = createAccount(100d);
		accountService.addTransaction(Transaction.builder().account(account).transactionAmount(100d)
				.transactionType(TransactionType.CREDIT).transactionDate(LocalDate.of(2019, 6, 15)).build());
		accountService.addTransaction(Transaction.builder().account(account).transactionAmount(200d)
				.transactionType(TransactionType.CREDIT).transactionDate(LocalDate.of(2019, 7, 15)).build());
		accountService.addTransaction(Transaction.builder().account(account).transactionAmount(250d)
				.transactionType(TransactionType.DEBIT).transactionDate(LocalDate.of(2019, 8, 1)).build());
		assertThat(accountService.getTransactions("123456").size()).isEqualTo(3);
	}

	@Test
	public void should_return_transactions_of_given_account_even_when_multiple_accounts_exists() {
		Account account1 = createAccount(100d);
		Account account2 = createAccount("1234567", 100d);
		accountService.addTransaction(Transaction.builder().account(account1).transactionAmount(100d)
				.transactionType(TransactionType.CREDIT).transactionDate(LocalDate.of(2019, 6, 15)).build());
		accountService.addTransaction(Transaction.builder().account(account2).transactionAmount(200d)
				.transactionType(TransactionType.CREDIT).transactionDate(LocalDate.of(2019, 7, 15)).build());
		accountService.addTransaction(Transaction.builder().account(account1).transactionAmount(150d)
				.transactionType(TransactionType.DEBIT).transactionDate(LocalDate.of(2019, 8, 1)).build());
		assertThat(accountService.getTransactions("123456").size()).isEqualTo(2);
		assertThat(accountService.getTransactions("1234567").size()).isEqualTo(1);
	}

}