import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { HttpEventType, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BaseCommon } from '../base-common';
import { ServerService, UploadCallback } from '../server.service';

@Component({
    selector: 'app-reorder-component',
    templateUrl: './reorder-component.component.html',
    styleUrls: ['./reorder-component.component.css']
})
export class ReorderComponentComponent implements OnInit {

    prog_value: number = 0;
    num_uploaded: number = 0;
    current_name: string = '';
    message: string = '';
    prog_value_txt: string = '';
    prog_class: string = '';

    constructor(private service: ServerService) { }

    ngOnInit(): void {
    }

    setStatusGui(status: boolean) {
        BaseCommon.setStatusToolbarGui(status);
        if (status) {
            document.getElementById("btn_reorder").removeAttribute("disabled");
        } else {
            document.getElementById("btn_reorder").setAttribute("disabled", "disabled");
        }
    }

    reorderAllData() {

        this.prog_value = 0;
        this.num_uploaded = 0;

        this.setStatusGui(false);
        this.setProgressData("", "", "progress-bar bg-success", 0, "");

        try {
            this.service.requestGet(BaseCommon.config.upload_url_ping, () => {
                this.reorderAll();
            }, (error) => {
                console.error("ERROR Ping 1: ", error);
                this.setProgressData("", "Error Cannot Connect To Server",
                    "progress-bar bg-danger", 100, "");
                this.setStatusGui(true);
            });
        } catch (err) {
            console.error(err);
        }
    }

    setProgressData(n: string = "", m: string = "",
        c: string = "progress-bar bg-success",
        i: number = 0, t: string = "") {

        document.getElementById("reorder_name_file_reorder").innerHTML = n;
        document.getElementById("prog_reorder").setAttribute("class", c);
        document.getElementById("prog_reorder").setAttribute("style", "width: " + i + "%");
        document.getElementById("prog_reorder").setAttribute("aria-valuenow", i.toString());
        document.getElementById("prog_reorder").innerHTML = t;
        document.getElementById("message_reorder").innerHTML = m;
    }

    reorderAll() {

        this.current_name = '';
        this.message = '';

        let current_event = null;
        let first_message = null;
        let num = 0;
        let total_file = 0;

        this.service.eventSource(BaseCommon.config.reorder_data_url, (event, source) => {
            console.log(BaseCommon.time() + " EVENT SOURCE: ", event);
            if (current_event === null) {
                current_event = event;
                first_message = JSON.parse(current_event.data);
                num = total_file = first_message.value;
                this.prog_value = 0;
                this.setProgressData("", "", "progress-bar bg-warning", 0, "reording...");
            } else {
                num--;
                this.prog_value = Math.round(100 * (total_file - num) / total_file);
                this.setProgressData("", "", "progress-bar bg-warning", this.prog_value, "reording...");
                if (num === 0) {
                    source.close();
                    this.setStatusGui(true);
                    this.setProgressData("", "", "progress-bar bg-success", 100, "done");
                }
            }
        }, (error, source) => {
            source.close();
            console.log("REORDER ERROR:", error);
            this.setStatusGui(true);
            this.setProgressData("", "Error in redorder data.", "progress-bar bg-danger", 100, "");
        });
    }
}
