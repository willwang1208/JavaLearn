#!/bin/bash
#
#

PROJECT_HOME=$(dirname `readlink -f "$0"`)
METHOD=$1
JAR_FILE=${PROJECT_HOME}/postgres-xl-statistics-0.1.0.jar
MAIN_CLASS=com.mfp.pgxl.stat.SpringBootWebApplication
SPRING_BOOT_CONF_FILE=${PROJECT_HOME}/conf/application.properties
LIB_DIR=${PROJECT_HOME}/lib
LOG_FILE=/data/log/pgxl_stat.out


help(){
    echo "help."
}

start(){
    local pid=`ps aux |grep "${MAIN_CLASS}" |grep "${JAR_FILE}" |awk '{print \$2}'`
    if [ "${pid}" != "" ]; then
        exit 1
    fi
    
    nohup java -Djava.ext.dirs=${LIB_DIR} -cp ${JAR_FILE} ${MAIN_CLASS} --spring.config.location=${SPRING_BOOT_CONF_FILE} > ${LOG_FILE} 2>&1 &
}

stop(){
    local pid=`ps aux |grep "${MAIN_CLASS}" |grep "${JAR_FILE}" |awk '{print \$2}'`
    if [ "${pid}" != "" ]; then
        kill ${pid}
    fi
}


case "$METHOD" in
    start)             start
                       ;;
    stop)              stop
                       ;;
    *)                 help
                       ;;
esac
