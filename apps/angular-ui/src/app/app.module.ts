import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';
// eslint-disable-next-line import/no-extraneous-dependencies
import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import { environment } from '../environments/environment';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { ErrorInterceptor } from './auth/interceptors/error.interceptor';
import { TokenInterceptor } from './auth/interceptors/token.interceptor';
import { HomeComponent } from './home/home.component';
import { LandingPageComponent } from './landing-page/landing-page.component';
import { SharedModule } from './shared/shared.module';

@NgModule({
  declarations: [AppComponent, LandingPageComponent, HomeComponent],
  imports: [
    BrowserModule,
    HttpClientModule,
    SharedModule,
    StoreModule.forRoot({}),
    EffectsModule.forRoot(),
    BrowserAnimationsModule,
    AppRoutingModule,
    StoreDevtoolsModule.instrument({
      maxAge: 25,
      logOnly: environment.production,
    }),
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: TokenInterceptor,
      multi: true,
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: ErrorInterceptor,
      multi: true,
    },
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
