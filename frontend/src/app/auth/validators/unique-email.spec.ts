import { TestBed } from '@angular/core/testing';
import { AuthService } from '../services/auth.service';
import { UniqueEmail } from './unique-email';

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
