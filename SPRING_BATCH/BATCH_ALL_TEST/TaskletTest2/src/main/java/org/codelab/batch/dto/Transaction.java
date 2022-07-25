package org.codelab.batch.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    private String accountNumber;
    private Date transactionDate;
    private Double amount;



}
