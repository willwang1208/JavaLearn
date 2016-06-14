#!/bin/bash 

pt_start(){
    head_str="/opt/jdk1.8.0_66/bin/java -jar postgresql-tools-jar-with-dependencies.jar PressTest jdbc:postgresql://127.0.0.1:31001/jelly_ios jelly null"

    `$head_str user_data find 2 5 >> all.log 2>&1 &`
    `$head_str user_data find_first 60 5 >> all.log 2>&1 &`
    `$head_str user_data find_in 5 5 >> all.log 2>&1 &`
    `$head_str user_data find_update_one 20 5 >> all.log 2>&1 &`
    `$head_str user_data find_insert_one 10 5 >> all.log 2>&1 &`
    `$head_str user_data find_delete_one 10 5 >> all.log 2>&1 &`
    ###`$head_str user_data find_insert_many 2 5 >> all.log 2>&1 &`
    
    #`$head_str user_data find 2 5 >> find.log 2>&1 &`
    #`$head_str user_data find_first 60 5 >> find_first.log 2>&1 &`
    #`$head_str user_data find_in 5 5 >> find_in.log 2>&1 &`
    #`$head_str user_data find_update_one 10 5 >> find_update_one.log 2>&1 &`
    #`$head_str user_data find_insert_one 10 5 >> find_insert_one.log 2>&1 &`
    #`$head_str user_data find_delete_one 10 5 >> find_delete_one.log 2>&1 &`
    ###`$head_str user_data find_insert_many 2 5 >> find_insert_many.log 2>&1 &`
}

pt_stop(){
    local pids=$(ps aux | grep postgresql-tools-jar-with-dependencies.jar | grep -v 'grep' | awk '{print $2}')
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
