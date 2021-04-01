import {
    Component,
    OnInit,
    ViewChild,
    ViewContainerRef,
    ComponentFactoryResolver,
    ComponentRef,
    ComponentFactory,
    Injectable
} from '@angular/core';

import { HttpClient } from '@angular/common/http';
import {
    faFile,
    faUpload,
    faFileImage,
    faFileInvoice,
    faFileUpload,
    faRedo,
    faCameraRetro,
    faClipboardList,
    faSync,
    faSortAmountDownAlt
} from '@fortawesome/free-solid-svg-icons';
import { BaseCommon } from './base-common';

@Injectable({
    providedIn: 'root',
})
@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {

    title = 'app-photo-client';
    camera_retro = faCameraRetro;
    isShown: boolean = false;
    icon_upload = faFileUpload;
    icon_reorder = faSortAmountDownAlt; 
    icon_sync = faSync;
    icon_config = faClipboardList;

    @ViewChild('main', { read: ViewContainerRef, static: true }) entry: ViewContainerRef;
    constructor(private resolver: ComponentFactoryResolver, private http: HttpClient) {

        http.get(BaseCommon.assets_common_config).subscribe(data => {
            BaseCommon.config = data;
        });
    }

    ngOnInit() {
    }
}
