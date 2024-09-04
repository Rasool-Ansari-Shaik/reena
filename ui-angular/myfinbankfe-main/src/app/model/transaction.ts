import { Account } from "./account";

export interface Transaction {
  id: number;
  transactionId: string;
  amount: number;
  type: 'DEPOSIT' | 'WITHDRAWAL' | 'TRANSFER';
  date: Date;
  //account: Account;
}
