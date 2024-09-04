
  import { bootstrapApplication } from '@angular/platform-browser';
  import { provideHttpClient } from '@angular/common/http'; // Import provideHttpClient
  import { provideRouter, Route } from '@angular/router';
  import { AppComponent } from './app/app.component';
  import { routes } from './app/app.routes';
  
  bootstrapApplication(AppComponent, {
    providers: [
      provideHttpClient(), // Add provideHttpClient to providers
      provideRouter(routes), // Add provideRouter with your routes
    ],
  })
  .catch(err => console.error(err));
  