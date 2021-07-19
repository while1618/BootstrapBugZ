import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
import { AngularUiAuthModule } from '@bootstrapbugz/angular-ui/auth';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';
// eslint-disable-next-line import/no-extraneous-dependencies
import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import { environment } from '../environments/environment';
import { AppComponent } from './app.component';

@NgModule({
  declarations: [AppComponent],
  imports: [
    BrowserModule,
    HttpClientModule,
    AngularUiAuthModule,
    RouterModule.forRoot([], { initialNavigation: 'enabled' }),
    StoreModule.forRoot({}),
    EffectsModule.forRoot(),
    StoreDevtoolsModule.instrument({
      maxAge: 25,
      logOnly: environment.production,
    }),
  ],
  providers: [],
  bootstrap: [AppComponent],
})
export class AppModule {}
