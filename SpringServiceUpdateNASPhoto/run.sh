#!/bin/sh

export DIR=$(pwd)
export NAME=$(basename $DIR)
export DATA_DIR=Data
export UPLOADS_DIR=Uploads
export SERVER_PORT=8804
export SERVER_MASTER_PORT=8801
export ENABLE_INTERCEPT_AOP=true
export RUNNING_MODE=1
export THREADS_POOL=2
export REPLACE_EXISTING=true
export SERVICE_NAME=ss-nas-photo-up1

./mvnw spring-boot:run -Dspring-boot.run.arguments=--mio.test=0
