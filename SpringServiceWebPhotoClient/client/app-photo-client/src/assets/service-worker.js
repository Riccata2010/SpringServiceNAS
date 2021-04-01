"use strict"

function uploadFile(file, url) {

    let formData = new FormData();
    formData.append('file', file);
    console.log("UPLOADING: " + file.name); 
    
    return {
    	subscribe: (ok, err) => {
    		fetch(url, {method: "POST", body: formData})
				.then(response => response.text())
				.then(text => {
				    console.log(text);
				    ok();
				});
    	}
    };
}

function uploadList(url, info, list, call_back, skip) {

    if (list && list.length > 0) {

        let file = list.shift();

		console.log("uploadList - file: " + file);
        call_back.postMessage({k:"updateName", v:file.name});

        let upload = () => {
            uploadFile(file, url).subscribe(
                () => {
                	call_back.postMessage({k:"updateProgress", v:file.name});
                    uploadList(url, info, list, call_back, skip);
                },
                () => {
                    console.error("ERROR");
                    call_back.postMessage({k:"error", v:""});
                    call_back.postMessage({k:"updateProgress", v:file.name});
                    uploadList(url, info, list, call_back, skip);
                });
        };

        if (!skip) {
            upload();
        } else {
            fetch(info + "/" + file.name)
                .then(response => response.json())
                .then(json => {
                    console.log(json);
                    if (json["exists"] === "false") {
                        upload();
                    } else {
                        console.log("FILE ALREADY EXISTS!");
                        call_back.postMessage({k:"updateProgress", v:file.name});
                        uploadList(url, info, list, call_back, skip);
                    }
                });
        }
    } else {
        call_back.postMessage({k:"end", v:""});
    }
}

self.addEventListener('install', event => {
  console.log('Service worker installing...');
});

self.addEventListener('activate', event => {
  console.log('Service worker activating...');
});

self.addEventListener('fetch', event => {
  console.log("fetch");
  event.respondWith();
});

self.addEventListener('message', event => {
  if (event && event.data) {
  	switch(event.data.message) {
  		case "upload": {
  			let info_url 	= event.data.info_url;
  			let upload_url 	= event.data.upload_url;
  			let list 		= event.data.value;
  			let skip 		= event.data.skip;
  			console.log("---------------------------");
  			console.log("info_url    : " + info_url);
  			console.log("upload_url  : " + upload_url);
  			console.log("list        : " + list);
  			console.log("skip        : " + skip);
  			console.log("---------------------------");
  			uploadList(upload_url, info_url, list, event.source, skip);
  			break;
  		}
  		default: {
  			console.log("default", event);
  		}
  	}
  } else {
  	console.log("wrong event!");
  }
  //event.source.postMessage("Hi client");
});

