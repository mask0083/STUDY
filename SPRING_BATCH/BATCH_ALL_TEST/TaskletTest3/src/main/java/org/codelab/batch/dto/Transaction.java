package org.codelab.batch.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Transaction {
    private String accountNumber;
    private Date transactionDate;
    private Double amount;

}
