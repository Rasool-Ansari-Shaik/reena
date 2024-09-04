package com.myfinbank.controller;

import com.myfinbank.entity.Account;
import com.myfinbank.entity.User;
import com.myfinbank.model.UserAccount;
import com.myfinbank.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@CrossOrigin("*")
public class AdminController {

    private AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // Activate Customer
    @PostMapping("/customer/activate")
    public ResponseEntity<String> activateCustomer(@RequestParam("username") String username) {
        // Activate Customer
        boolean isActive = adminService.activateCustomer(username);
        if (isActive) {
            return ResponseEntity.ok().body("Customer activated successfully");
        } else {
            return ResponseEntity.badRequest().body("Customer activation failed");
        }

    }

    // Deactivate Customer
    @PostMapping("/customer/deactivate")
    public ResponseEntity<String> deactivateCustomer(@RequestParam("username") String username) {
        // deactivate Customer
        boolean isDeactive = adminService.deactivateCustomer(username);
        if (isDeactive) {
            return ResponseEntity.ok().body("Customer deactivated successfully");
        } else {
            return ResponseEntity.badRequest().body("Customer deactivation failed");
        }
    }

    // Update Customer

    // Create Customer Account
    @PostMapping("/customer/account")
    public ResponseEntity<String> createCustomerAccount(@RequestParam("username") String username) {
        // Create Customer Account
        String customerAccount = adminService.createCustomerAccount(username);
        if (customerAccount == null) {
            return ResponseEntity.badRequest().body("User not found");
        } else {
            return ResponseEntity.ok().body("Customer account created: " + customerAccount);
        }
    }



    // Update Customer Account
    @PutMapping("/customer/account")
    public ResponseEntity<String> updateCustomerAccount(@RequestBody Account account) {
        // Update Customer Account
        boolean isUpdated = adminService.updateCustomerAccount(account);
        if (isUpdated) {
            return ResponseEntity.ok().body("Customer account updated successfully");
        } else {
            return ResponseEntity.badRequest().body("Customer account update failed");
        }
    }

    // Delete Customer Account
    @DeleteMapping("/customer/account")
    public ResponseEntity<String> deleteCustomerAccount(@RequestParam("accountNumber") String accountNumber) {
        // Delete Customer Account
        boolean isDeleted = adminService.deleteCustomerAccount(accountNumber);
        if (isDeleted) {
            return ResponseEntity.ok().body("Customer account deleted successfully");
        } else {
            return ResponseEntity.badRequest().body("Customer account delete failed");
        }
    }

    // Loan approval
    @PostMapping("/loan/approve")
    public ResponseEntity<String> approveLoan(@RequestParam("loanAccountNumber") String loanAccountNumber) {
        // Approve Loan
        boolean isApproved = adminService.approveLoan(loanAccountNumber);
        if (isApproved) {
            return ResponseEntity.ok().body("Loan approved successfully");
        } else {
            return ResponseEntity.badRequest().body("Loan approval failed");
        }
    }

    // Create Customer Account - with UI
    @PostMapping("/accounts/create")
    public ResponseEntity<String> createCustomerAccount(@RequestBody UserAccount userAccount) {
        // Create Customer Account
        String customerAccount = adminService.createCustomerAccountUI(userAccount);
        if (customerAccount == null) {
            return ResponseEntity.badRequest().body("User not found");
        } else {
            return ResponseEntity.ok().body("Customer account created: " + customerAccount);
        }
    }

    // Loan approval
    @GetMapping("/user")
    public ResponseEntity<List<Account>> getAccount(@RequestParam("userId") Long userId) {
        // Get Account using userId
        List<Account> accountList = adminService.getAccount(userId);
        if (accountList != null) {
            return ResponseEntity.ok().body(accountList);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers() {
        // Get All Users
        List<User> userList = adminService.getUsers();
        if (userList != null) {
            return ResponseEntity.ok().body(userList);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/accounts")
    public ResponseEntity<List<UserAccount>> getAccounts() {
        // Get All Accounts
        List<UserAccount> accountList = adminService.getAccounts();
        if (accountList != null) {

            return ResponseEntity.ok().body(accountList);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // activate corresponding to UI
    @PostMapping("/users/activate")
    public ResponseEntity<String> activateUser(@RequestParam("userId") Long userId) {
        // Activate Customer
        boolean isActive = adminService.activateUser(userId);
        if (isActive) {
            return ResponseEntity.ok().body("Customer activated successfully");
        } else {
            return ResponseEntity.badRequest().body("Customer activation failed");
        }

    }

    @PostMapping("/users/deactivate")
    public ResponseEntity<String> deactivateUser(@RequestParam("userId") Long userId) {
        // Activate Customer
        boolean isDeactive = adminService.deactivateUser(userId);
        if (isDeactive) {
            return ResponseEntity.ok().body("Customer deactivated successfully");
        } else {
            return ResponseEntity.badRequest().body("Customer deactivation failed");
        }

    }

    @PutMapping("/accounts/update")
    public ResponseEntity<UserAccount> updateAccount(@RequestBody UserAccount userAccount) {
        return ResponseEntity.ok().body(userAccount);
    }

    @DeleteMapping("/accounts/delete")
    public ResponseEntity<String> deleteAccount(@RequestParam("accountId") String accountNumber) {
        // Activate Customer
        boolean isDelete= adminService.deleteCustomerAccount(accountNumber);
        if (isDelete) {
            return ResponseEntity.ok().body("Customer deleted successfully");
        } else {
            return ResponseEntity.badRequest().body("Customer delete failed");
        }
    }

}
