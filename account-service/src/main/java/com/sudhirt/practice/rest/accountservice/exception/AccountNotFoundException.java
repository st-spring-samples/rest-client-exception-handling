package com.sudhirt.practice.rest.accountservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class AccountNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -254360863550452659L;

	public AccountNotFoundException() {
		super("Account not found");
	}

}