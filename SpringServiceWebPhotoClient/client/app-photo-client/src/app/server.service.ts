import { Injectable } from '@angular/core';
import { HttpClient, HttpRequest, HttpEvent, HttpEventType, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BaseCommon } from './base-common';

@Injectable({
    providedIn: 'root'
})
export class ServerService {

    constructor(private http: HttpClient) { }

    uploadFile(file: File, url: string): Observable<HttpEvent<any>> {

        const formData: FormData = new FormData();

        formData.append('file', file);
        
        console.log("UPLOADING: " + file.name);

        const req = new HttpRequest('POST', `${url}`, formData, {
            reportProgress: true,
            responseType: 'text'
        });

        return this.http.request(req);
    }

    requestGet(url: string, onOk: Function, onError: Function) {
        try {
            let request = new XMLHttpRequest();
            request.timeout = 5000;
            request.open('GET', url);
            request.onreadystatechange = function () {
                if (this.readyState == 4) {
                    if (this.status == 200) {
                        onOk();
                    } else {
                        onError(this);
                    }
                }
            }
            request.send();
        } catch (err) {
            console.error(err);
            onError(err);
        }
    }

    eventSource(url: string, onMessage: Function, onError: Function) {
        let eventSource = new EventSource(url);
        eventSource.onmessage = (event) => {
            onMessage(event, eventSource);
        }
        eventSource.onerror = (err) => {
            onError(err, eventSource)
        }
    }

    uploadListDefault(list: Array<File>, call_back: UploadCallback, skip: boolean): void {
        this.uploadList(BaseCommon.config.upload_url1, list, call_back, skip);
    }

    uploadList(url: string, list: Array<File>, call_back: UploadCallback, skip: boolean): void {

        if (list && list.length > 0) {

            let file = list.shift();

            call_back.updateName(file.name);

            let upload = () => {
                this.uploadFile(file, url).subscribe(
                    event => {
                        if (event.type === HttpEventType.Response) {
                            call_back.updateProgress(file.name);
                            this.uploadList(url, list, call_back, skip);
                        }
                    },
                    err => {
                        console.error("ERROR: ", err);
                        call_back.error(err);
                        call_back.updateProgress(file.name);
                        this.uploadList(url, list, call_back, skip);
                    });
            };

            if (!skip) {
                upload();
            } else {
                fetch(BaseCommon.config.info_file_url + "/" + file.name)
                    .then(response => response.json())
                    .then(json => {
                        console.log(json);
                        if (json["exists"] === "false") {
                            upload();
                        } else {
                            console.log("FILE ALREADY EXISTS!");
                            call_back.updateProgress(file.name);
                            this.uploadList(url, list, call_back, skip);
                        }
                    });
            }
        } else {
            call_back.end();
        }
    }

    createWorkers(list1: Array<File>, list2: Array<File>, call_back: UploadCallback, skip: boolean) {

        console.log("Use split list");

        if (typeof Worker !== 'undefined') {

            const worker1 = new Worker('./worker.worker', { type: 'module' });
            worker1.onmessage = ({ data }) => {
                this.uploadList(BaseCommon.config.upload_url1, data, call_back, skip);
            };
            worker1.postMessage(list1);

            const worker2 = new Worker('./worker.worker', { type: 'module' });
            worker2.onmessage = ({ data }) => {
                this.uploadList(BaseCommon.config.upload_url2, data, call_back, skip);
            };
            worker2.postMessage(list2);

        } else {
            console.log("Can't use typeof Worker");
            setTimeout(() => {
                this.uploadList(BaseCommon.config.upload_url1, list1, call_back, skip);
            }, 100);
            setTimeout(() => {
                this.uploadList(BaseCommon.config.upload_url2, list2, call_back, skip);
            }, 100);
        }
    }
}

export interface UploadCallback {
    updateName(name: string): void;
    updateProgress(name: string): void;
    error(err: any): void;
    end(): void;
}
