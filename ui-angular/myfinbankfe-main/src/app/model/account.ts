import { User } from "./user";
export interface Account {
    id?: number;
    accountNumber?: string;
    balance: number;
    user: User;
  }