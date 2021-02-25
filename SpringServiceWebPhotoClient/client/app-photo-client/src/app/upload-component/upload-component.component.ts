import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { HttpEventType, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BaseCommon } from '../base-common';
import { SwitcherComponent } from '../switcher-component';
import { ServerService, UploadCallback } from '../server.service';

@Component({
    selector: 'app-upload-component',
    templateUrl: './upload-component.component.html',
    styleUrls: ['./upload-component.component.css']
})
export class UploadComponentComponent implements OnInit, UploadCallback {

    max_split: number = 10;
    total_file: number = 0;
    prog_value: number = 0;
    num_uploaded: number = 0;
    selected_files: FileList;
    current_name: string = '';
    message: string = '';
    prog_value_txt: string = '';
    prog_class: string = '';

    constructor(private service: ServerService) { }

    ngOnInit(): void {
    }

    updateName(name: string): void {
        this.current_name = name;
    }

    updateProgress(name: string): void {

        console.log(BaseCommon.time() + " - name: " + name);

        this.num_uploaded++;
        this.prog_value = Math.round(100 * this.num_uploaded / this.total_file);
        this.prog_value_txt = this.prog_value + "%";
        document.getElementById("prog").innerHTML = this.prog_value_txt;

        if (this.num_uploaded === this.total_file) {
            this.reorder();
        }
    }

    error(err: any): void {
        console.error(err);
        this.message = err.message;
    }

    end(): void {
    }

    setStatusGui(status: boolean) {
        BaseCommon.setStatusToolbarGui(status);
        if (status) {
            document.getElementById("send_difference").removeAttribute("disabled");
            document.getElementById("selector_multiple").removeAttribute("disabled");
            document.getElementById("btn_upload").removeAttribute("disabled");
        } else {
            document.getElementById("send_difference").setAttribute("disabled", "disabled");
            document.getElementById("selector_multiple").setAttribute("disabled", "disabled");
            document.getElementById("btn_upload").setAttribute("disabled", "disabled");
        }
    }

    selectFiles(event) {
        this.selected_files = event.target.files;
    }

    skipIfExists(): boolean {
        try {
            return (<HTMLInputElement>document.getElementById("send_difference")).checked;
        } catch (error) { console.error(error); return true; }
    }

    uploadFiles() {

        this.prog_value = 0;
        this.num_uploaded = 0;

        this.setStatusGui(false);
        this.setProgressData("", "", "progress-bar bg-success", 0, "");

        try {

            if (this.selected_files) {

                let skip = this.skipIfExists();
                console.log("SKIP: ", skip);

                let list = BaseCommon.toArray(this.selected_files);
                this.total_file = list.length;

                this.service.requestGet(BaseCommon.config.upload_url_ping1, () => {
                    if (list.length > this.max_split) {
                        this.service.requestGet(BaseCommon.config.upload_url_ping2, () => {
                            let half = Math.ceil(list.length / 2);
                            let s1 = list.splice(0, half);
                            let s2 = list.splice(-half);
                            this.service.createWorkers(s1, s2, this, skip);
                        }, (error) => {
                            this.service.uploadListDefault(list, this, skip);
                        });
                    } else {
                        this.service.uploadListDefault(list, this, skip);
                    }
                }, (error) => {
                    console.error("ERROR Ping 1: ", error);
                    this.setProgressData("", "Error Cannot Connect To Server",
                        "progress-bar bg-danger", 100, "");
                    this.setStatusGui(true);
                });
            }
        } catch (err) {
            console.error(err);
        }
    }

    setProgressData(n: string = "", m: string = "",
        c: string = "progress-bar bg-success",
        i: number = 0, t: string = "") {

        document.getElementById("upload_name_file").innerHTML = n;
        document.getElementById("prog").setAttribute("class", c);
        document.getElementById("prog").setAttribute("style", "width: " + i + "%");
        document.getElementById("prog").setAttribute("aria-valuenow", i.toString());
        document.getElementById("prog").innerHTML = t;
        document.getElementById("message").innerHTML = m;
    }

    reorder() {

        this.current_name = '';
        this.message = '';

        let current_event = null;
        let first_message = null;
        let remains = 0;
        let num_tot = 0;

        this.service.eventSource(BaseCommon.config.reorder_url, (event, source) => {
            console.log(BaseCommon.time() + " EVENT SOURCE: ", event);
            if (current_event === null) {
                current_event = event;
                first_message = JSON.parse(current_event.data);
                num_tot = remains = parseInt(first_message.value);
                this.prog_value = 0;
                if (remains === 0) {
                    source.close();
                    this.setStatusGui(true);
                    this.setProgressData("", "", "progress-bar bg-success", 100, "done");
                } else {
                    this.setProgressData("", "", "progress-bar bg-warning", 0, "reording...");
                }
            } else {
                remains--;
                this.prog_value = Math.round(100 * (num_tot - remains) / num_tot);
                this.setProgressData("", "", "progress-bar bg-warning", this.prog_value, "reording...");
                if (remains === 0) {
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
