#!/bin/bash

# health_check.sh

# Crawl current connected port of WAS
CURRENT_PORT=$(cat /home/ec2-user/service_url.inc | grep -Po '[0-9]+' | tail -1)
TARGET_PORT=0

# Toggle port Number
if [ ${CURRENT_PORT} -eq 8081 ]; then
    TARGET_PORT=8082
elif [ ${CURRENT_PORT} -eq 8082 ]; then
    TARGET_PORT=8081
else
    echo "> No WAS is connected to nginx"
    exit 1
fi


echo "> Start health check of WAS at 'http://127.0.0.1:${TARGET_PORT}' ..."

for RETRY_COUNT in 1 2 3 4 5 6 7 8 9 10
do
    echo "> #${RETRY_COUNT} trying..."
    RESPONSE_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://127.0.0.1:${TARGET_PORT})

    if [[ ${RESPONSE_CODE} -ge 200 && ${RESPONSE_CODE} -lt 300 ]]; then
        echo "> Server is up"
        exit 0
    elif [[ ${RESPONSE_CODE} -ge 300 && ${RESPONSE_CODE} -lt 400 ]]; then
        echo "> Redirection, server is up"
        exit 0
    elif [[ ${RESPONSE_CODE} -ge 400 && ${RESPONSE_CODE} -lt 500 ]]; then
        echo "> Client error, server may be up"
        exit 0
    elif [[ ${RESPONSE_CODE} -ge 500 && ${RESPONSE_CODE} -lt 600 ]]; then
        echo "> Server error, server may be up"
        exit 0
    elif [ ${RETRY_COUNT} -eq 10 ]; then
        echo "> Health check failed."
        exit 1
    fi
    sleep 10
done

