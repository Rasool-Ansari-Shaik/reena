import { Injectable } from '@angular/core';
import { HttpClient,HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environment/environment';

@Injectable({
  providedIn: 'root'
})
export class CustomerService {
  private apiUrl = `${environment.apiUrl}/admin`;

  constructor(private http: HttpClient) { }

  getCustomers(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/users`);
  }

  activateCustomer(userId: number): Observable<any> {
    const params = new HttpParams()
      .set('userId', userId)
    return this.http.post(`${this.apiUrl}/users/activate`, null, {params:params});
  }

  deactivateCustomer(userId: number): Observable<any> {
    const params = new HttpParams()
      .set('userId', userId)
    return this.http.post(`${this.apiUrl}/users/deactivate`, null, {params:params});
  }

  // Additional CRUD methods for customers can be added here
}
