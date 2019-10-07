package com.sudhirt.practice.rest.accountservice.repository;

import java.util.List;
import com.sudhirt.practice.rest.accountservice.entity.Account;
import com.sudhirt.practice.rest.accountservice.entity.Transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	List<Transaction> findByAccount(Account account);

}