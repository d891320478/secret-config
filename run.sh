#!/bin/bash

mkdir -p ${LOG_PATH}

GCLOG="-Xlog:gc:/data/logs/secret-config/secret-config.gc"
HEAPDUMP="-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/data/logs/secret-config/secret-config.heap"

java -server -Xms${MEMORY}m -Xmx${MEMORY}m ${ARGS} ${GCLOG} ${HEAPDUMP} -jar secret-config.jar