import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppComponent } from './app.component'; // Importing as standalone
import { ReactiveFormsModule } from '@angular/forms';
import { LoginComponent } from './auth/login/login.component';
import { HttpClientModule } from '@angular/common/http';
import { AuthService } from './service/auth.sevice';
import { provideHttpClient } from '@angular/common/http'; // New import
import { RegisterComponent } from './auth/register/register.component';
import { UserDashboardComponent } from './dashboard/user-dashboard/user-dashboard.component';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { CommonModule } from '@angular/common';

import { MatTabsModule } from '@angular/material/tabs'; 

@NgModule({
  declarations: [
  ],
  imports: [
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    ReactiveFormsModule,
    BrowserModule,
    AppComponent,
    CommonModule,
    MatTabsModule,// Import the standalone component
  ], // Provide AuthService here


  providers: [
    provideHttpClient() ,AuthService],
})
export class AppModule { }
