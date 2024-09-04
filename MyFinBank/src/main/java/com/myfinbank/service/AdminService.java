package com.myfinbank.service;

import com.myfinbank.entity.Account;
import com.myfinbank.entity.Loan;
import com.myfinbank.entity.User;
import com.myfinbank.model.UserAccount;
import com.myfinbank.repository.AccountRepository;
import com.myfinbank.repository.LoanRepository;
import com.myfinbank.repository.UserRepository;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;

@Service
public class AdminService {

    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);
    private UserRepository userRepository;
    private AccountRepository accountRepository;
    private LoanRepository loanRepository;

    public AdminService(UserRepository userRepository, AccountRepository accountRepository, LoanRepository loanRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.loanRepository = loanRepository;
    }

    // Create Customer Account
    public String createCustomerAccount(String username) {
        // Check User exists
        Optional<User> findUser = userRepository.findByUsername(username);
        if (! findUser.isPresent()) {
            logger.error("User not found");
            return null;
        }
        // Create Customer Account
        String uuid = java.util.UUID.randomUUID().toString().substring(0, 8);

        Account account = new Account();
        account.setAccountNumber(uuid);
        account.setUsername(findUser.get().getUsername());
        account.setCreatedAt(LocalDateTime.now());
        account.setActive(true);
        account.setBalance(0.0);
        Account savedAccount = accountRepository.save(account);
        if (savedAccount == null) {
            logger.error("Account not created");
            return null;
        } else {
            logger.info("Account created: {}", uuid);
        }

        return uuid;
    }

    // Update Customer Account
    public boolean updateCustomerAccount(Account account) {
        // find customer account
        Optional<Account> findAccount = accountRepository.findByAccountNumber(account.getAccountNumber());
        if (! findAccount.isPresent()) {
            logger.error("Account not found");
            return false;
        } else {
            Account updatedAccount = findAccount.get();
            updatedAccount.setBalance(account.getBalance());
            updatedAccount.setActive(account.isActive());
            updatedAccount.setUpdatedAt(LocalDateTime.now());
            accountRepository.save(updatedAccount);
            logger.info("Account updated: {}", updatedAccount);
            return true;
        }
    }

    // Delete Customer Account
    public boolean deleteCustomerAccount(String accountNumber) {
        // find customer account
        Optional<Account> findAccount = accountRepository.findByAccountNumber(accountNumber);
        if (! findAccount.isPresent()) {
            logger.error("Account not found");
            return false;
        } else {
            accountRepository.delete(findAccount.get());
            logger.info("Account deleted: {}", accountNumber);
            return true;
        }
    }

    // Activate Customer
    public boolean activateCustomer(String username) {
        // find user details
        Optional<User> findUser = userRepository.findByUsername(username);
        if (! findUser.isPresent()) {
            logger.error("User not found");
            return false;
        } else {
            User updatedUser = findUser.get();
            // find user has account number
            Optional<Account> findAccount = accountRepository.findByUsername(username);
            if (! findAccount.isPresent()) {
                logger.error("Account not found");
                return false;
            }
            Account account = findAccount.get();
            if (! account.isActive() || account.getAccountNumber() == null) {
                logger.error("Account not active or account number is not available");
                return false;
            }
            updatedUser.setActive(true);
            userRepository.save(updatedUser);
            logger.info("User activated: {}", updatedUser);
            return true;
        }
    }


    // Deactivate Customer
    public boolean deactivateCustomer(String username) {
        // find user details
        Optional<User> findUser = userRepository.findByUsername(username);
        if (! findUser.isPresent()) {
            logger.error("User not found");
            return false;
        } else {
            User updatedUser = findUser.get();
            updatedUser.setActive(false);
            userRepository.save(updatedUser);
            logger.info("User deactivated: {}", updatedUser);
            return true;
        }
    }

    // approve loan
    public boolean approveLoan(String loanAccountNumber) {
        // find loan account
        Optional<Loan> findLoanAccount = loanRepository.findByLoanAccountNumber(loanAccountNumber);
        if (! findLoanAccount.isPresent()) {
            logger.error("Loan account not found");
            return false;
        } else {
            Loan loan = findLoanAccount.get();

            String accountNumber = loan.getAccountNumber();
            String username = loan.getUsername();

            Optional<Account> accountOptional = accountRepository.findByAccountNumber(accountNumber);
            if (accountOptional.isPresent()) {
                Account account = accountOptional.get();
                double balance = account.getBalance();
                double emi = loan.getEmi();
                if (balance < emi * 2) {
                    logger.error("Insufficient balance to pay EMIs");
                    loan.setLoanStatus(false);
                    loan.setRemarks("Insufficient balance");
                    loanRepository.save(loan);
                    return false;
                } else {
                    loan.setLoanStatus(true);
                    loanRepository.save(loan);
                    logger.info("Loan account approved: {}", loanAccountNumber);
                    return true;
                }
            } else {
                logger.error("Customer Account Number not found");
                loan.setLoanStatus(false);
                loan.setRemarks("Customer Account Number not found");
                loanRepository.save(loan);
                return false;
            }
        }
    }

    // Create User account according to UI
    public String createCustomerAccountUI(UserAccount userAccount) {
        // Check User exists
        Optional<User> findUser = userRepository.findById(userAccount.getUser().getId());
        if (! findUser.isPresent()) {
            logger.error("User not found");
            return null;
        }

        Account account = new Account();
        account.setAccountNumber(userAccount.getAccountNumber());
        account.setUsername(findUser.get().getUsername());
        account.setUserId(findUser.get().getId());
        account.setCreatedAt(LocalDateTime.now());
        account.setActive(true);
        account.setBalance(userAccount.getBalance());
        Account savedAccount = accountRepository.save(account);
        if (savedAccount == null) {
            logger.error("Account not created");
            return null;
        } else {
            logger.info("Account created: {}", userAccount.getAccountNumber());
        }


        return userAccount.getAccountNumber();
    }

    public List<Account> getAccount(Long userId) {
        // get Account by userId
        List<Account> accountList = accountRepository.findByUserId(userId);

        if (accountList != null && accountList.size() > 0) {
            logger.info("User Accounts available");
            return accountList;
        } else {
            logger.error("User Account not available");
            return null;
        }
    }

    public List<User> getUsers() {
       return userRepository.findAll();
    }

    public List<UserAccount> getAccounts() {
        List<Account> accountList = accountRepository.findAll();
        if (accountList != null && accountList.size() > 0) {
            // convert accountList to List<UserAccount>
            List<UserAccount> userAccountList = new ArrayList<>();
            for (Account account : accountList) {
                UserAccount userAccount = new UserAccount();
                userAccount.setAccountNumber(account.getAccountNumber());
                com.myfinbank.model.User user = new com.myfinbank.model.User();
                user.setId(account.getUserId());
                userAccount.setUser(user);
                userAccount.setBalance(account.getBalance());

                userAccountList.add(userAccount);
            }
            return userAccountList;
        }
        return null;
    }


    public boolean activateUser(Long userId) {
        // find user details
        Optional<User> findUser = userRepository.findById(userId);
        if (! findUser.isPresent()) {
            logger.error("User not found");
            return false;
        } else {
            User updatedUser = findUser.get();
            updatedUser.setActive(true);
            userRepository.save(updatedUser);
            logger.info("User activated: {}", updatedUser);
            return true;
        }
    }

    public boolean deactivateUser(Long userId) {
        // find user details
        Optional<User> findUser = userRepository.findById(userId);
        if (! findUser.isPresent()) {
            logger.error("User not found");
            return false;
        } else {
            User updatedUser = findUser.get();
            updatedUser.setActive(false);
            userRepository.save(updatedUser);
            logger.info("User deactivated: {}", updatedUser);
            return true;
        }
    }


}
