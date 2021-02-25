import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { MainContainerComponent } from './main-container/main-container.component';
import { ActionsComponentComponent } from './actions-component/actions-component.component';
import { CenterComponentComponent } from './center-component/center-component.component';
import { UploadComponentComponent } from './upload-component/upload-component.component';
import { ViewComponentComponent } from './view-component/view-component.component';
import { ReorderComponentComponent } from './reorder-component/reorder-component.component';

@NgModule({
    declarations: [
        AppComponent,
        MainContainerComponent,
        ActionsComponentComponent,
        CenterComponentComponent,
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
    providers: [],
    bootstrap: [AppComponent]
})
export class AppModule { }
