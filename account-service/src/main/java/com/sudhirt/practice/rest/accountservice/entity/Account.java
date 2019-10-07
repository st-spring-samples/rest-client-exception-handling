package com.sudhirt.practice.rest.accountservice.entity;

import java.time.LocalDate;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "accountNumber")
public class Account {

	@Id
	private String accountNumber;

	@Column(nullable = false)
	private Double balance;

	@Column(nullable = true)
	private LocalDate lastTransactionDate;

	@OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
	private List<Transaction> transactions;

}