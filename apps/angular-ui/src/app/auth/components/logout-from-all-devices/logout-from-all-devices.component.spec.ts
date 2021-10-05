import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LogoutFromAllDevicesComponent } from './logout-from-all-devices.component';

describe('LogoutFromAllDevicesComponent', () => {
  let component: LogoutFromAllDevicesComponent;
  let fixture: ComponentFixture<LogoutFromAllDevicesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [LogoutFromAllDevicesComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LogoutFromAllDevicesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
