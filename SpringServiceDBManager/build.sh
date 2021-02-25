#!/bin/sh

if [ -z "$1" ]
  then
    echo "No argument - DELETE"
    rm -fv MioServiceDB.*
fi


./mvnw clean compile package 
