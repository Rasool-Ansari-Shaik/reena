package com.myfinbank.controller;

import com.myfinbank.entity.Account;
import com.myfinbank.entity.AccountTransaction;
import com.myfinbank.entity.Loan;
import com.myfinbank.model.Transact;
import com.myfinbank.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    private CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // Deposit amount
    @PostMapping("/deposit")
    public ResponseEntity<Account> deposit(@RequestParam("accountId") String accountNumber, @RequestParam("amount") double amount) {
        Account depositAccount = customerService.deposit(accountNumber, amount);
        if (depositAccount != null) {
            System.out.println("Rasool.................");
            double newBalance = depositAccount.getBalance();
            return ResponseEntity.ok().body(depositAccount); //"Amount deposited successfully with new balance: " + newBalance);
        } else {
            return ResponseEntity.badRequest().body(null); //"Amount deposit failed");
        }
    }

    // Withdraw amount
    @PostMapping("/withdraw")
    public ResponseEntity<String> withdraw(@RequestParam("accountId") String accountNumber, @RequestParam("amount") double amount) {
        Account withdrawAccount = customerService.withdraw(accountNumber, amount);
        if (withdrawAccount != null) {
            double newBalance = withdrawAccount.getBalance();
            return ResponseEntity.ok().body("Amount withdrawn successfully with new balance: " + newBalance);
        } else {
            return ResponseEntity.badRequest().body("Amount withdraw failed");
        }
    }

    // Transfer amount
    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@RequestParam("fromAccountId") String fromAccountNumber,
                                           @RequestParam("toAccountId") String toAccountNumber, @RequestParam("amount") double amount) {
        Account transferAccount = customerService.transfer(fromAccountNumber, toAccountNumber, amount);
        if (transferAccount != null) {
            double newBalance = transferAccount.getBalance();
            return ResponseEntity.ok().body("Amount transferred successfully with new balance: " + newBalance);
        } else {
            return ResponseEntity.badRequest().body("Amount transfer failed");
        }
    }

    // Apply Loan - Calculate EMI
    @PostMapping("/loan/emiCalculator")
    public ResponseEntity<String> applyLoan(@RequestParam("loanAmount") double loanAmount, @RequestParam("tenure") int tenure) {
        double emi = customerService.calculateEmi(loanAmount, tenure);
        if (emi > 0) {
            return ResponseEntity.ok().body("EMI would be : " + emi);
        } else {
            return ResponseEntity.badRequest().body("EMI cannot be calculated");
        }
    }

    // Apply Loan - Calculate EMI
    @PostMapping("/loan/apply")
    public ResponseEntity<String> applyLoan(@RequestBody Loan loan) {
        double emi = customerService.applyLoan(loan);
        if (emi > 0) {
            return ResponseEntity.ok().body("Loan applied successfully with EMI: " + emi);
        } else {
            return ResponseEntity.badRequest().body("Loan apply failed");
        }
    }

    // To get transactions
    @GetMapping("/transactions")
    public ResponseEntity<List<Transact>> getTransactions(@RequestParam("accountId") String accountNumber) {
        List<Transact> accountTransactionList = customerService.getTransactions(accountNumber);
        if (accountTransactionList != null) {
            return ResponseEntity.ok().body(accountTransactionList);
        } else {
            return ResponseEntity.badRequest().body(null);
        }

    }

    @PostMapping("/investments/invest")
    public ResponseEntity<AccountTransaction> invest(@RequestParam("accountId") String accountNumber,
                                         @RequestParam("investmentType") String investmentType,
                                         @RequestParam("amount") double amount) {
        AccountTransaction accountTransaction = customerService.invest(accountNumber, investmentType, amount);
        if (accountTransaction != null) {
            return ResponseEntity.ok().body(accountTransaction);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }



}
