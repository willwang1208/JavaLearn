#!/bin/bash 

pt_start(){
    head_str="/opt/jdk1.8.0_66/bin/java -jar MongodbPressTest-jar-with-dependencies.jar"

    `$head_str 172.31.32.2:20000 jelly_ios check_in find_first 80 5 >> log_mongodb_pt.log 2>&1 &`
    `$head_str 172.31.32.3:20000 jelly_ios check_in find_first 80 5 >> log_mongodb_pt.log 2>&1 &`
    
    #`$head_str 127.0.0.1:20000 jelly_ios check_in find 80 5 >> log_mongodb_pt.log 2>&1 &`
    #`$head_str 127.0.0.1:20000 jelly_ios check_in find_first 80 5 >> log_mongodb_pt.log 2>&1 &`
    #`$head_str 127.0.0.1:20000 jelly_ios check_in find_in 80 5 >> log_mongodb_pt.log 2>&1 &`
    #`$head_str 127.0.0.1:20000 jelly_ios check_in find_update_one 80 5 >> log_mongodb_pt.log 2>&1 &`
    #`$head_str 127.0.0.1:20000 jelly_ios check_in find_insert_one 80 5 >> log_mongodb_pt.log 2>&1 &`
    #`$head_str 127.0.0.1:20000 jelly_ios check_in find_delete_one 80 5 >> log_mongodb_pt.log 2>&1 &`
    #`$head_str 127.0.0.1:20000 jelly_ios check_in find_insert_many 80 5 >> log_mongodb_pt.log 2>&1 &`
}

pt_stop(){
    local pids=$(ps aux | grep MongodbPressTest-jar-with-dependencies.jar | grep -v 'grep' | awk '{print $2}')
    local pidarr=($pids)
    local length=${#pidarr[@]}
    local pid
    
    for ((i=0; i<$length; i++))
    do
        pid=${pidarr[$i]}
        kill -9 $pid
    done
}

if [ "$1" == "start" ]; then 
    pt_start
elif [ "$1" == "stop" ]; then 
    pt_stop
fi
