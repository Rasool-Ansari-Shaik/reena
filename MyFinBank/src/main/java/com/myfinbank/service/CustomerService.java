package com.myfinbank.service;

import com.myfinbank.entity.Account;
import com.myfinbank.entity.AccountTransaction;
import com.myfinbank.entity.Loan;
import com.myfinbank.model.Transact;
import com.myfinbank.repository.AccountRepository;
import com.myfinbank.repository.AccountTransactionRepository;
import com.myfinbank.repository.LoanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    private AccountRepository accountRepository;

    private LoanRepository loanRepository;

    private AccountTransactionRepository accountTransactionRepository;

    public CustomerService(AccountRepository accountRepository, LoanRepository loanRepository, AccountTransactionRepository accountTransactionRepository) {
        this.accountRepository = accountRepository;
        this.loanRepository = loanRepository;
        this.accountTransactionRepository = accountTransactionRepository;
    }
    // Deposit amount
    public Account deposit(String accountNumber, double depositAmount) {

        // get account by account number
        Optional<Account> accountOptional = accountRepository.findByAccountNumber(accountNumber);
        if (accountOptional.isPresent()) {
            Account account1 = accountOptional.get();
            double existingBalance = account1.getBalance();
            double newBalance = existingBalance + depositAmount;
            account1.setBalance(newBalance);
            logger.info("Account updated with new balance: "+ newBalance);
            Account updatedAccount = accountRepository.save(account1);

            String transactionId = java.util.UUID.randomUUID().toString().substring(0, 10);

            AccountTransaction accountTransaction = new AccountTransaction();
            accountTransaction.setAccountNumber(account1.getAccountNumber());
            accountTransaction.setUserId(account1.getUserId());
            accountTransaction.setTransactionId(transactionId);
            accountTransaction.setTransactionType("DEPOSIT");
            accountTransaction.setCreatedAt(LocalDateTime.now());
            accountTransaction.setAmount(depositAmount);
            logger.info("Transaction created");
            accountTransactionRepository.save(accountTransaction);

            return updatedAccount;
        }
        logger.error("Account not found");
        return null;
    }

    // Withdraw amount
    public Account withdraw(String accountNumber, double withdrawAmount) {

        // get account by account number
        Optional<Account> accountOptional = accountRepository.findByAccountNumber(accountNumber);
        if (accountOptional.isPresent()) {
            Account account1 = accountOptional.get();
            double existingBalance = account1.getBalance();
            double newBalance = existingBalance - withdrawAmount;
            if (newBalance < 0) {
                logger.error("Insufficient balance");
                return null;
            }
            account1.setBalance(newBalance);
            Account updatedAccount = accountRepository.save(account1);

            String transactionId = java.util.UUID.randomUUID().toString().substring(0, 10);

            AccountTransaction accountTransaction = new AccountTransaction();
            accountTransaction.setAccountNumber(account1.getAccountNumber());
            accountTransaction.setUserId(account1.getUserId());
            accountTransaction.setTransactionId(transactionId);
            accountTransaction.setTransactionType("WITHDRAWAL");
            accountTransaction.setCreatedAt(LocalDateTime.now());
            accountTransaction.setAmount(withdrawAmount);
            logger.info("Transaction created");
            accountTransactionRepository.save(accountTransaction);

            return updatedAccount;
        }
        return null;
    }

    // Transfer amount
    public Account transfer(String fromAccountNumber, String toAccountNumber, double transferAmount) {

        // get account by account number
        Optional<Account> fromAccountOptional = accountRepository.findByAccountNumber(fromAccountNumber);
        Optional<Account> toAccountOptional = accountRepository.findByAccountNumber(toAccountNumber);
        if (fromAccountOptional.isPresent() && toAccountOptional.isPresent()) {
            Account account1 = fromAccountOptional.get();
            double existingBalance = account1.getBalance();
            double newBalance = existingBalance - transferAmount;
            if (newBalance < 0) {
                logger.error("Insufficient balance");
                return null;
            }
            account1.setBalance(newBalance);

            // update toAccount
            Account toAccount1 = toAccountOptional.get();
            double toExistingBalance = toAccount1.getBalance();
            double toNewBalance = toExistingBalance + transferAmount;
            toAccount1.setBalance(toNewBalance);
            accountRepository.save(toAccount1);
            // update from account
            Account updatedAccount = accountRepository.save(account1);

            String transactionId = java.util.UUID.randomUUID().toString().substring(0, 10);

            AccountTransaction accountTransaction = new AccountTransaction();
            accountTransaction.setAccountNumber(account1.getAccountNumber());
            accountTransaction.setUserId(account1.getUserId());
            accountTransaction.setTransactionId(transactionId);
            accountTransaction.setTransactionType("TRANSFER");
            accountTransaction.setCreatedAt(LocalDateTime.now());
            accountTransaction.setAmount(transferAmount);
            logger.info("Transaction created");
            accountTransactionRepository.save(accountTransaction);

            return updatedAccount;
        }
        return null;
    }


    // emi calculator
    public double calculateEmi(double loanAmount, int tenure) {
        double rateOfInterest = 10.0;
        double monthlyInterest = rateOfInterest / 100 / 12;
        int totalMonths = tenure * 12;
        double emi = (loanAmount * monthlyInterest * Math.pow(1 + monthlyInterest, totalMonths)) /
                (Math.pow(1 + monthlyInterest, totalMonths) - 1);
        return emi;
    }

    // apply for loan
    public double applyLoan(Loan loan) {
        // get account number
        String accountNumber = loan.getAccountNumber();
        // get account by account number
        Optional<Account> accountOptional = accountRepository.findByAccountNumber(accountNumber);
        if (accountOptional.isPresent()) {
            Account account1 = accountOptional.get();
            double emi = calculateEmi(loan.getLoanAmount(), loan.getTenure());
            // Create Loan Account Number
            String uuid = java.util.UUID.randomUUID().toString().substring(0, 10);

            Loan loan1 = new Loan();
            loan1.setUsername(account1.getUsername());
            loan1.setAccountNumber(account1.getAccountNumber());
            loan1.setLoanAccountNumber(uuid);
            loan1.setLoanAmount(loan.getLoanAmount());
            loan1.setTenure(loan.getTenure());
            loan1.setRateOfInterest(10.0);
            loan1.setEmi(emi);
            loan1.setLoanStatus(false);
            loan1.setCreatedAt(LocalDateTime.now());

            Loan savedLoan = loanRepository.save(loan);
            if (savedLoan != null) {
                return emi;
            } else {
                logger.error("Loan not applied");
                return 0;
            }
        } else {
            logger.error("Account not found: {}", accountNumber);
            return 0;
        }
    }

    public List<Transact> getTransactions(String accountNumber) {
        List<AccountTransaction> accountTransactionList = accountTransactionRepository.findByAccountNumber(accountNumber);
        List<Transact> transactList = new ArrayList<>();
        if (accountTransactionList != null && accountTransactionList.size() > 0) {
            logger.info("Transaction found : "+ accountTransactionList.size());
            for (AccountTransaction accountTransaction : accountTransactionList) {
                Transact transact = new Transact();
                transact.setAmount(accountTransaction.getAmount());
                transact.setTransactionId(accountTransaction.getTransactionId());
                transact.setId(accountTransaction.getId());
                transact.setDate(accountTransaction.getCreatedAt());
                transact.setType(accountTransaction.getTransactionType());

                transactList.add(transact);
            }
            return transactList;
        } else {
            logger.error("No transaction found");
            return null;
        }

    }


    public AccountTransaction invest(String accountNumber, String investmentType, double amount) {
        Optional<Account> accountOptional = accountRepository.findByAccountNumber(accountNumber);

        if (accountOptional.isPresent()) {
            Account account1 = accountOptional.get();
            String transactionId = java.util.UUID.randomUUID().toString().substring(0, 10);
            AccountTransaction accountTransaction = new AccountTransaction();
            accountTransaction.setAccountNumber(account1.getAccountNumber());
            accountTransaction.setUserId(account1.getUserId());
            accountTransaction.setTransactionId(transactionId);
            accountTransaction.setTransactionType(investmentType);
            accountTransaction.setCreatedAt(LocalDateTime.now());
            accountTransaction.setAmount(amount);
            logger.info("Transaction created");
            return accountTransactionRepository.save(accountTransaction);
        }
        return null;
    }
}
