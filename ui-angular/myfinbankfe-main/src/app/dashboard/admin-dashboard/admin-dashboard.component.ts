import { Component } from '@angular/core';
import { CustomerService } from '../../service/customer.service';
import { AccountService } from '../../service/account.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Account } from '../../model/account';
import { AuthService } from '../../service/auth.sevice';

@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css'],
  standalone: true,
  imports: [ReactiveFormsModule,CommonModule]
})
export class AdminDashboardComponent {
  customers: any[] = [];
  accounts: any[] = [];
  activeTab: string = 'customers';
  createAccountForm: FormGroup;
  editAccountForm: FormGroup;
  editingAccountStatus: any = null;
  updateAccountValue!: Account;

  constructor(private customerService: CustomerService, private accountService: AccountService, private fb: FormBuilder, private authService: AuthService) {
    //hafija1
    this.loadCustomers();
    this.loadAccounts();
    this.createAccountForm = this.fb.group({
      accountNumber: ['', [Validators.required]],
      customerId: ['', [Validators.required]],
      initialDeposit: ['', [Validators.required, Validators.min(0)]],
    });
    this.editAccountForm = this.fb.group({
      accountNumber: [{ value: '', disabled: true }],
      customerId: ['', [Validators.required]],
      balance: ['', [Validators.required, Validators.min(0)]]
    });
  }

  loadCustomers() {
    this.customerService.getCustomers().subscribe(data => {
      this.customers = data;
    });
  }

  loadAccounts() {
    this.accountService.getAccounts().subscribe(data => {
      this.accounts = data;
    });
  }

  activateCustomer(customerId: number) {
    this.customerService.activateCustomer(customerId).subscribe(() => {
      this.loadCustomers();
    });
    
  }

  logout() {
    this.authService.logout();  
  }
  deactivateCustomer(customerId: number) {
    this.customerService.deactivateCustomer(customerId).subscribe(() => {
      this.loadCustomers();
    });
  }

  onCreateAccount() {
    if (this.createAccountForm.valid) {
      const {accountNumber,customerId,initialDeposit} = this.createAccountForm.value;
      this.accountService.createAccount(accountNumber,customerId,initialDeposit).subscribe({
        next: (response) => {
          console.log('Account created successfully', response);
          // Handle successful account creation, e.g., refresh the list of accounts
        },
        error: (error) => {
          console.error('Failed to create account', error);
          // Handle error
        }
      });
    }
  }
  editAccount(account: Account) {
    this.editingAccountStatus = account;
    this.updateAccountValue = account;
    this.editAccountForm.patchValue({
      accountNumber: account.accountNumber,
      balance: account.balance
    });
  }

  deleteAccount(accountNumber: string) {
    if (confirm(`Are you sure you want to delete account number ${accountNumber}?`)) {
      this.accountService.deleteAccount(accountNumber).subscribe(
        () => {
          this.loadAccounts(); // Refresh the accounts list
        },
        (error) => {
          console.error('Error deleting account', error);
        }
      );
    }
  }
  updateAccount() {
    if (this.editAccountForm.valid) {

      const {customerId,balance} = this.editAccountForm.value;
      const validNumber = this.updateAccountValue.id !== undefined ? this.updateAccountValue.id : 0;
      this.accountService.updateAccount(customerId,balance,validNumber).subscribe(
        () => {
          this.editAccountForm.reset();
          this.loadAccounts(); // Refresh the accounts list
        },
        (error) => {
          console.error('Error updating account', error);
        }
      );

    this.editingAccountStatus = null;
    }
  }

  cancelEdit() {
    this.editAccountForm.reset();
    this.editingAccountStatus = null;
  }


  // Similar CRUD methods for accounts can be added here
}
