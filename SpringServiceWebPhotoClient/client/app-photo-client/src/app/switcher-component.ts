import {
    Component,
    OnInit,
    ViewChild,
    ViewContainerRef,
    ComponentFactoryResolver,
    ComponentRef,
    ComponentFactory, 
    Type
} from '@angular/core';

import { MainContainerComponent } from './main-container/main-container.component';

export class SwitcherComponent {
    
    private static resolver: ComponentFactoryResolver;
    private static entry: ViewContainerRef;
    private static current_component: ComponentRef<any>;
    private static action: string;

    static setResolver(value: ComponentFactoryResolver) {
        this.resolver = value;
    } 

    static setEntry(value: ViewContainerRef) {
        this.entry = value;
    } 

    static getResolver(): ComponentFactoryResolver {
        return this.resolver;
    } 

    static getEntry(): ViewContainerRef { 
        return this.entry;
    } 

    static setAction(value: string) {
        this.action = value;
    }

    static getAction(): string {
        return this.action;
    }

    static switchTo(value: Type<any>): void {
        this.entry.clear();
        this.current_component = this.entry.createComponent(this.resolver.resolveComponentFactory(value));
    } 

    static updateTo(value: string): void {
        this.setAction(value);
        this.switchTo(MainContainerComponent);
    }

    static destroyComponent() {
        this.current_component.destroy();
    }
}