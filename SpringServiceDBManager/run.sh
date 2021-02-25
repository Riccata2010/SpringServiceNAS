#!/bin/sh

export SERVER_PORT=8801
export AUTO_PORTS=true
export ENABLE_INTERCEPT_AOP=true
export DEBUG_SLEEP_TIME=0
export RUNNING_MODE=1

./mvnw spring-boot:run -Dspring-boot.run.arguments=--mio.test=0
