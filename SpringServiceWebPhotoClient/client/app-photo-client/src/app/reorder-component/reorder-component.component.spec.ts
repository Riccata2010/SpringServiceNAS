import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReorderComponentComponent } from './reorder-component.component';

describe('ReorderComponentComponent', () => {
  let component: ReorderComponentComponent;
  let fixture: ComponentFixture<ReorderComponentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ReorderComponentComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ReorderComponentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
