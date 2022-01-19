import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SignOutFromAllDevicesComponent } from './sign-out-from-all-devices.component';

describe('SignOutFromAllDevicesComponent', () => {
  let component: SignOutFromAllDevicesComponent;
  let fixture: ComponentFixture<SignOutFromAllDevicesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SignOutFromAllDevicesComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SignOutFromAllDevicesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
