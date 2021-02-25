export class BaseCommon {

    public static assets_common_config: string = "../assets/config.json";
    public static config: any = null;

    static toArray(list: FileList): Array<File> {
        if (!list) {
            return null;
        }
        let arr = new Array<File>();
        for (let i = 0; i < list.length; i++) {
            arr.push(list[i]);
        }
        return arr;
    }

    static time(): string {
        const date = new Date();
        return date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds();
    }

    static setStatusToolbarGui(status: boolean) {

        if (status) {
            document.getElementById("btn_toolbar_config").removeAttribute("disabled");
            document.getElementById("btn_toolbar_upload").removeAttribute("disabled");
            document.getElementById("btn_toolbar_reorder").removeAttribute("disabled");
        } else {
            document.getElementById("btn_toolbar_config").setAttribute("disabled", "disabled");
            document.getElementById("btn_toolbar_upload").setAttribute("disabled", "disabled");
            document.getElementById("btn_toolbar_reorder").setAttribute("disabled", "disabled");
        }
    }
}