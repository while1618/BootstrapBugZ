import { Injectable } from '@angular/core';
import { AbstractControl, AsyncValidator, ValidationErrors } from '@angular/forms';
import { catchError, map, Observable, of } from 'rxjs';
import { AuthService } from '../services/auth.service';

@Injectable({ providedIn: 'root' })
export class UniqueUsername implements AsyncValidator {
  constructor(private authService: AuthService) {}

  validate = (
    control: AbstractControl
  ): Promise<ValidationErrors> | Observable<ValidationErrors> => {
    return this.authService.usernameAvailability(control.value).pipe(
      map((value) => (value ? null : { nonUniqueUsername: true })),
      catchError((error) => of({ nonUniqueUsername: true }))
    );
  };
}
