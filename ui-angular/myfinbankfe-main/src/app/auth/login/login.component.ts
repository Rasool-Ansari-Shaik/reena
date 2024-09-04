import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../service/auth.sevice'; 
// Importing as standalone
import { ReactiveFormsModule } from '@angular/forms';
import { provideHttpClient } from '@angular/common/http'; // New import
import { Router } from '@angular/router';


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  standalone: true,
  imports: [ReactiveFormsModule],
  styleUrls: ['./login.component.css']// New configuration for HttpClient
})
export class LoginComponent {
  loginForm: FormGroup;

  constructor(private fb: FormBuilder, private authService: AuthService, private router: Router) {
    this.loginForm = this.fb.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required]],
    });
  }


  onSubmit() {

    const { username, password } = this.loginForm.value;
    if (this.loginForm.valid) {
      this.authService.login(username, password).subscribe({
        next: (response) => {
          console.log('Login successful', response);
          
          // Assuming the response contains the role of the user
          const userRole = response.role; // Adjust this based on your actual response structure
          this.authService.setUser(response);
          // Redirect based on the role
          if (userRole === 'ADMIN') {
            this.router.navigate(['/admin-dashboard']);
          } else if (userRole === 'USER') {
            this.router.navigate(['/user-dashboard']);
          } else {
            console.error('Unknown role:', userRole);
            // Handle unknown role
          }
        },
        error: (error) => {
          console.error('Login failed', error);
          // Handle login error
        }
      });
    }
  }
}
