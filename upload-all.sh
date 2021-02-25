#!/bin/bash
#-----------------------------------------------------------------
#  _   _       _                 _   ____  _           _        _ 
# | | | |_ __ | | ___   __ _  __| | |  _ \| |__   ___ | |_ ___ | |
# | | | | '_ \| |/ _ \ / _` |/ _` | | |_) | '_ \ / _ \| __/ _ \| |
# | |_| | |_) | | (_) | (_| | (_| | |  __/| | | | (_) | || (_) |_|
#  \___/| .__/|_|\___/ \__,_|\__,_| |_|   |_| |_|\___/ \__\___/(_)
#       |_|                                                       
# 
#-----------------------------------------------------------------
##################################################################
CUR=$(pwd)

send() {
    type=$1
    data=$(ls *$type)
    for i in $data; do
        echo "Uploading file: " $i
        curl -i -X POST 'http://'$NAS':8800/ss-nas-photo/upload' -F 'file=@'$i > /dev/null
        echo ""
    done;
}

if [ -f banner.txt ]; then
    cat banner.txt
else
    tail -n +2 upload-all.sh | head -n 9
fi

echo ""
echo "Please, type the path with photo:"
read WORK_DIR

if [ -d $WORK_DIR ]; then
    cd $WORK_DIR
    send jpg
    send png
    send jpeg
    send bmp
    send gif
    send mov
    send mkv
    send mp4
    sleep 5
    curl -i -X GET 'http://'$NAS':8800/ss-nas-photo/reorder' > /dev/null
else
    echo "Invalid path!"
fi

cd $CUR

exit 0
