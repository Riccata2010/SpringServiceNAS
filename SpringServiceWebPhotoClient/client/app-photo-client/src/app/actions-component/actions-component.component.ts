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

    constructor() { }

    ngOnInit(): void {
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
