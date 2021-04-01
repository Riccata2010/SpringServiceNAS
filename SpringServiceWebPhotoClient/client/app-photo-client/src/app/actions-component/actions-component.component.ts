import { Component, OnInit } from '@angular/core';
import {
    faFile,
    faUpload,
    faFileImage,
    faFileInvoice,
    faFileUpload,
    faRedo,
    faCameraRetro,
    faClipboardList,
    faSync
} from '@fortawesome/free-solid-svg-icons';

import { BaseCommon } from '../base-common';
import { ServerService, UploadCallback } from '../server.service';

@Component({
    selector: 'app-actions-component',
    templateUrl: './actions-component.component.html',
    styleUrls: ['./actions-component.component.css']
})
export class ActionsComponentComponent implements OnInit {

    isShown: boolean = false;
    icon_upload = faFileUpload;
    icon_reorder = faSync;
    icon_view = faFileImage;
    icon_config = faClipboardList;
    camera_retro = faCameraRetro;
    connection_status: string = "...";

    constructor(private service: ServerService) { }

    ngOnInit(): void {
    
    	setTimeout(() => {
            this.service.requestGet(BaseCommon.config.upload_url_ping, () => {
                    this.connection_status = "ok server reachable";
                }, (error) => {
                    this.connection_status = "error server not reachable!";
                });
        }, 1000);
    
    }

    onClickUpload(): void {
        console.log("onClickUpload");
    }

    onClickReorder(): void {
        console.log("onClickReorder");
    }

    onClickView(): void {
        console.log("onClickView");
    }
}
