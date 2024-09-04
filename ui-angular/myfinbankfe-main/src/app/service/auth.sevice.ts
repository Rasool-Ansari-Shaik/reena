import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { User } from '../model/user';
import { Observable, BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private userSubject = new BehaviorSubject<any>(null); // Observable for user info
  user$ = this.userSubject.asObservable(); 

  constructor(private http: HttpClient, private router: Router) {
    const user = this.getUser();
    this.userSubject.next(user);
   }

  login(username: string, password: string): Observable<any> {
    return this.http.post(`http://localhost:8085/user/login`, { username, password });
  }

  register(user: User): Observable<any> {
    return this.http.post(`http://localhost:8085/user/register`,  user);
  }

  

 
  setUser(user: any): void {
    this.userSubject.next(user);
    localStorage.setItem('user', JSON.stringify(user)); // Store user info
  }

  getUser(): any {
    return JSON.parse(localStorage.getItem('user') || '{}');
  }


   // Method to store user details and role in local storage
   storeUserDetails(user: any): void {
    localStorage.setItem('user', JSON.stringify(user));
    localStorage.setItem('role', user.role); // Store user role
  }

  // Method to get the stored user role
  getRole(): string | null {
    return localStorage.getItem('role');
  }

  // Method to clear user details from local storage
  logout(): void {
     // Assuming this clears user data in the service
     this.router.navigate(['/login']); 
    localStorage.removeItem('user');
    localStorage.removeItem('role');
  }
}
