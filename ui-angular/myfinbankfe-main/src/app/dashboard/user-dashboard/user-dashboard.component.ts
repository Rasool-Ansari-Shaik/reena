import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AccountService } from '../../service/account.service';
import { ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../service/auth.sevice';
import { Transaction } from '../../model/transaction';
import { Account } from '../../model/account';

@Component({
  selector: 'app-user-dashboard',
  templateUrl: './user-dashboard.component.html',
  styleUrls: ['./user-dashboard.component.css'],
  standalone: true,
  imports: [ReactiveFormsModule,CommonModule]
})
export class UserDashboardComponent {
  activeTab: string = 'withdraw';

  emiResult: number | null = null;

  withdrawForm: FormGroup;
  depositForm: FormGroup;
  transferForm: FormGroup;
  investmentForm: FormGroup;
  loanForm: FormGroup;
  accounts: Account[] = [];

  transactions: any[] = [];
  firstAccountId: string | undefined = undefined;
  constructor(private fb: FormBuilder, private accountService: AccountService, private authService: AuthService) {
    
    const user = authService.getUser();
    this.getAccounts(user.id);
    this.withdrawForm = this.fb.group({
      amount: [null, [Validators.required, Validators.min(1)]]
    });

    this.depositForm = this.fb.group({
      amount: [null, [Validators.required, Validators.min(1)]]
    });

    this.transferForm = this.fb.group({
      amount: [null, [Validators.required, Validators.min(1)]],
      toAccount: ['', Validators.required]
    });

    this.investmentForm = this.fb.group({
      amount: [null, [Validators.required, Validators.min(1)]],
      type: ['recurring', Validators.required]
    });

    this.loanForm = this.fb.group({
      amount: [null, [Validators.required, Validators.min(1)]],
      interestRate: [null, [Validators.required, Validators.min(0)]],
      months: [null, [Validators.required, Validators.min(1)]]
    });

    
  }



  getAccounts(userId: number): void {
    this.accountService.getAccountsByUserId(userId).subscribe({
      next: (data) => {
        this.accounts = data;
        console.log('accounts', this.accounts);
        if (this.accounts.length > 0) {
          this.firstAccountId = this.accounts[0].accountNumber; //this.accounts[0]?this.accounts[0].accountNumber:'0';// Access the first account's ID
          console.log('first id accounts', this.firstAccountId);
          this.loadTransactions();
        }
      },
      error: (err) => {
        console.error('Error fetching accounts', err);
      }
    });
  }

  logout() {
    this.authService.logout();  
  }
  onWithdraw() {
    if (this.withdrawForm.valid) {
      const amount = this.withdrawForm.value.amount;
      // this.accountService.withdraw(this.firstAccountId?this.firstAccountId:'0',amount).subscribe(
      //   response => console.log('Withdraw successful', response),
      //   error => console.error('Withdraw failed', error)
      // );
      this.accountService.withdraw(this.firstAccountId?this.firstAccountId:'0',amount).subscribe({
        next: (data) => {
          
          console.log('Withdraw successful', data);
          
        },
        error: (err) => {
          console.error('Withdraw failed', err);
        }
        
    });
    }
  }

  selectTab(tab: string) {
    this.activeTab = tab;
    if (tab === 'transaction-list') {
      this.loadTransactions();
    }
  }

  loadTransactions() {
    console.log('transaction first account', this.firstAccountId);
    this.accountService.getTransaction(this.firstAccountId?this.firstAccountId:'0').subscribe(data => {
      this.transactions = data;
    });
  }

  onDeposit() {
    if (this.depositForm.valid) {
      const amount = this.depositForm.value.amount;
      this.accountService.deposit(this.firstAccountId?this.firstAccountId:'0',amount).subscribe(
        response => console.log('Deposit successful', response),
        error => console.error('Deposit failed', error)
      );
    }
  }

  onTransfer() {
    if (this.transferForm.valid) {
      const { amount, toAccount } = this.transferForm.value;
      this.accountService.transfer(this.firstAccountId?this.firstAccountId:'0',toAccount, amount).subscribe(
        response => console.log('Transfer successful', response),
        error => console.error('Transfer failed', error)
      );
    }
  }

  onInvest() {
    if (this.investmentForm.valid) {
      const { amount, type } = this.investmentForm.value;
      this.accountService.invest(this.firstAccountId?this.firstAccountId:'0',amount, type).subscribe(
        response => console.log('Investment successful', response),
        error => console.error('Investment failed', error)
      );
    }
  }

  calculateLoanEMI(): void {
    const amount = this.loanForm.get('amount')?.value;
    const interestRate = this.loanForm.get('interestRate')?.value;
    const months = this.loanForm.get('months')?.value;

    const monthlyRate = interestRate / (12 * 100);
    const emi = (amount * monthlyRate * Math.pow(1 + monthlyRate, months)) / 
                (Math.pow(1 + monthlyRate, months) - 1);

    this.emiResult = emi;
  }
}
