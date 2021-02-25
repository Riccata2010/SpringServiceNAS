import { Component, OnInit, Injector } from '@angular/core';
import { BaseCommon } from '../base-common';
import { SwitcherComponent } from '../switcher-component';

@Component({
    selector: 'app-main-container',
    templateUrl: './main-container.component.html',
    styleUrls: ['./main-container.component.css']
})
export class MainContainerComponent implements OnInit {

    constructor(injector: Injector) {
        
    }

    ngOnInit(): void {
       
    }
}
