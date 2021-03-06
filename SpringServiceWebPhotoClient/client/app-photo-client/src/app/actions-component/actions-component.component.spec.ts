import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ActionsComponentComponent } from './actions-component.component';

describe('ActionsComponentComponent', () => {
  let component: ActionsComponentComponent;
  let fixture: ComponentFixture<ActionsComponentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ActionsComponentComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ActionsComponentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
