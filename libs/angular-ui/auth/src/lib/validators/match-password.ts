import { Injectable } from '@angular/core';
import { AbstractControl, ValidationErrors, Validator } from '@angular/forms';

@Injectable({ providedIn: 'root' })
export class MatchPassword implements Validator {
  validate(control: AbstractControl): ValidationErrors {
    const { password, confirmPassword } = control.value;
    return password === confirmPassword ? null : { passwordsDoNotMatch: true };
  }
}
