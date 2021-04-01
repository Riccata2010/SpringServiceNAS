import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { ActionsComponentComponent } from './actions-component/actions-component.component';
import { UploadComponentComponent } from './upload-component/upload-component.component';
import { ViewComponentComponent } from './view-component/view-component.component';
import { ReorderComponentComponent } from './reorder-component/reorder-component.component';

@NgModule({
    declarations: [
        AppComponent,
        ActionsComponentComponent,
        UploadComponentComponent,
        ViewComponentComponent,
        ReorderComponentComponent
    ],
    imports: [
        CommonModule,
        BrowserModule,
        AppRoutingModule,
        FontAwesomeModule,
        HttpClientModule
    ],
    providers: [ 
	    { provide: Window, useValue: window }
    ],
    bootstrap: [AppComponent]
})
export class AppModule { }
