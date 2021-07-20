import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { AngularUiAuthModule } from '@bootstrapbugz/angular-ui/auth';
import { AngularUiSharedModule } from '@bootstrapbugz/angular-ui/shared';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';
// eslint-disable-next-line import/no-extraneous-dependencies
import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import { environment } from '../environments/environment';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LandingPageComponent } from './landing-page/landing-page.component';

@NgModule({
  declarations: [AppComponent, LandingPageComponent],
  imports: [
    BrowserModule,
    HttpClientModule,
    AngularUiAuthModule,
    AngularUiSharedModule,
    StoreModule.forRoot({}),
    EffectsModule.forRoot(),
    BrowserAnimationsModule,
    AppRoutingModule,
    StoreDevtoolsModule.instrument({
      maxAge: 25,
      logOnly: environment.production,
    }),
  ],
  providers: [],
  bootstrap: [AppComponent],
})
export class AppModule {}
