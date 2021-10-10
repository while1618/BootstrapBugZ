import { UniqueEmail } from './unique-email';
import { AuthService } from '../services/auth.service';
import { TestBed } from '@angular/core/testing';

describe('UniqueEmail', () => {
  let authService: AuthService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    authService = TestBed.inject(AuthService);
  });

  it('should create an instance', () => {
    expect(new UniqueEmail(authService)).toBeTruthy();
  });
});
