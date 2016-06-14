#!/bin/bash 

pt_start(){
    head_str="/opt/jdk1.8.0_66/bin/java -jar MongodbPressTest-jar-with-dependencies.jar 172.31.32.2:20000 jelly_ios"
    
    `$head_str user_data find 2 5 >> log_mongodb_pt_all.log 2>&1 &`
    `$head_str user_data find_first 50 5 >> log_mongodb_pt_all.log 2>&1 &`
    `$head_str user_data find_in 5 5 >> log_mongodb_pt_all.log 2>&1 &`
    `$head_str user_data find_update_one 10 5 >> log_mongodb_pt_all.log 2>&1 &`
    `$head_str user_data find_insert_one 10 5 >> log_mongodb_pt_all.log 2>&1 &`
    `$head_str user_data find_delete_one 10 5 >> log_mongodb_pt_all.log 2>&1 &`
    ###`$head_str user_data find_insert_many 5 5 >> log_mongodb_pt_all.log 2>&1 &`
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
