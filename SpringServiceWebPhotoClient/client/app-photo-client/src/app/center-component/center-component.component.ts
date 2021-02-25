import {
    Component,
    OnInit,
    ViewChild,
    ViewContainerRef,
    ComponentFactoryResolver,
    ComponentRef,
    ComponentFactory
} from '@angular/core';

import { UploadComponentComponent } from '../upload-component/upload-component.component';

import { BaseCommon } from '../base-common';
import { SwitcherComponent } from '../switcher-component';

@Component({
    selector: 'app-center-component',
    templateUrl: './center-component.component.html',
    styleUrls: ['./center-component.component.css']
})
export class CenterComponentComponent implements OnInit {

    action: string;

    @ViewChild('center_component', { read: ViewContainerRef, static: true }) entry: ViewContainerRef;
    constructor(private resolver: ComponentFactoryResolver) {
    }

    ngOnInit(): void {
        this.action = SwitcherComponent.getAction();
    }
}
