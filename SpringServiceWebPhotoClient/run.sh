#!/bin/sh

export SERVER_PORT=8888
export SERVER_MASTER_PORT=8801
export ENABLE_INTERCEPT_AOP=true
export UPLOADS_DIR=Uploads
export DATA_DIR=Data
export RUNNING_MODE=1
export THREADS_POOL=2
export REPLACE_EXISTING=true

./mvnw spring-boot:run -Dspring-boot.run.arguments=--mio.test=0
