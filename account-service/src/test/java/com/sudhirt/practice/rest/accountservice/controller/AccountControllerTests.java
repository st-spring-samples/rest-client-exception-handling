package com.sudhirt.practice.rest.accountservice.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.time.LocalDate;
import javax.transaction.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sudhirt.practice.rest.accountservice.constants.TransactionType;
import com.sudhirt.practice.rest.accountservice.entity.Account;
import com.sudhirt.practice.rest.accountservice.entity.Transaction;
import com.sudhirt.practice.rest.accountservice.repository.AccountRepository;
import com.sudhirt.practice.rest.accountservice.service.AccountService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AccountControllerTests {

	private static ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private AccountService accountService;

	public AccountControllerTests() {
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
	}

	private Account createAccount(Double balance) {
		return createAccount("123456", balance);
	}

	private Account createAccount(String accountNumber, Double balance) {
		Account account = Account.builder().accountNumber(accountNumber).balance(balance).build();
		return accountRepository.save(account);
	}

	@Test
	public void get_should_throw_404_when_account_number_does_not_exist() throws Exception {
		mockMvc.perform(get("/accounts/1234567")).andExpect(status().isNotFound());
	}

	@Test
	public void get_all_accounts_should_return_empty_array_when_accounts_does_not_exist() throws Exception {
		mockMvc.perform(get("/accounts")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));
	}

	@Test
	public void get_transactions_should_throw_404_when_account_number_does_not_exist() throws Exception {
		mockMvc.perform(get("/accounts/1234567/transactions")).andExpect(status().isNotFound());
	}

	@Test
	public void should_get_account_successfully_when_only_one_account_exist() throws Exception {
		createAccount(1000d);
		mockMvc.perform(get("/accounts/123456")).andExpect(status().isOk())
				.andExpect(jsonPath("$.balance").value(1000d));
	}

	@Test
	public void get_account_should_not_include_transaction_details() throws Exception {
		Account account = createAccount(1000d);
		Transaction transaction = Transaction.builder().transactionAmount(200d).transactionType(TransactionType.CREDIT)
				.transactionDate(LocalDate.of(2019, 9, 1)).account(account).build();
		accountService.addTransaction(transaction);
		mockMvc.perform(get("/accounts/123456")).andExpect(status().isOk())
				.andExpect(jsonPath("$.balance").value(1200d)).andExpect(jsonPath("$.transactions").doesNotExist());
	}

	@Test
	public void get_transactions_should_return_empty_list_when_transactions_does_not_exist() throws Exception {
		createAccount(1000d);
		mockMvc.perform(get("/accounts/123456/transactions")).andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(0)));
	}

	@Test
	public void get_transactions_should_return_non_empty_list_when_transactions_exist() throws Exception {
		Account account = createAccount(1000d);
		Transaction transaction = Transaction.builder().transactionAmount(200d).transactionType(TransactionType.CREDIT)
				.transactionDate(LocalDate.of(2019, 9, 1)).account(account).build();
		accountService.addTransaction(transaction);
		mockMvc.perform(get("/accounts/123456/transactions")).andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)));
	}

	@Test
	public void add_transaction_should_throw_404_when_account_number_does_not_exist() throws Exception {
		Transaction transaction = Transaction.builder().transactionAmount(200d).transactionType(TransactionType.CREDIT)
				.transactionDate(LocalDate.of(2019, 9, 1)).build();
		String requestBody = objectMapper.writeValueAsString(transaction);
		mockMvc.perform(
				post("/accounts/1234567/transactions").contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isNotFound());
	}

	@Test
	public void add_transaction_should_return_201_when_transaction_is_created_successfully() throws Exception {
		createAccount(1000d);
		Transaction transaction = Transaction.builder().transactionAmount(200d).transactionType(TransactionType.CREDIT)
				.transactionDate(LocalDate.of(2019, 9, 1)).build();
		String requestBody = objectMapper.writeValueAsString(transaction);
		mockMvc.perform(
				post("/accounts/123456/transactions").contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isCreated());
	}

}