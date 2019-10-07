package com.sudhirt.practice.rest.accountservice.repository;

import com.sudhirt.practice.rest.accountservice.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, String> {

}