#!/bin/bash 

pt_start(){
    head_str="/opt/jdk1.8.0_66/bin/java -jar postgresql-tools-jar-with-dependencies.jar PressTest"

    `$head_str jdbc:postgresql://172.31.32.2:31001/jelly_ios jelly null check_in find_first_not_ps 80 5 >> log_psql_pt.log 2>&1 &`
    `$head_str jdbc:postgresql://172.31.32.3:31002/jelly_ios jelly null check_in find_first_not_ps 80 5 >> log_psql_pt.log 2>&1 &`

    #`$head_str jdbc:postgresql://127.0.0.1:31001/jelly_ios jelly null check_in find_first_not_ps 10 5 >> log_psql_pt.log 2>&1 &`
    #`$head_str jdbc:postgresql://127.0.0.1:31001/jelly_ios jelly null check_in find_update_one_not_ps 10 5 >> log_psql_pt.log 2>&1 &`
    #`$head_str jdbc:postgresql://127.0.0.1:31001/jelly_ios jelly null check_in find_first 10 5 >> log_psql_pt.log 2>&1 &`
    #`$head_str jdbc:postgresql://127.0.0.1:31001/jelly_ios jelly null check_in find_update_one 10 5 >> log_psql_pt.log 2>&1 &`
    #`$head_str jdbc:postgresql://127.0.0.1:31001/jelly_ios jelly null check_in find_in 10 5 >> log_psql_pt.log 2>&1 &`
    #`$head_str jdbc:postgresql://127.0.0.1:31001/jelly_ios jelly null check_in find_insert_one 10 5 >> log_psql_pt.log 2>&1 &`
    #`$head_str jdbc:postgresql://127.0.0.1:31001/jelly_ios jelly null check_in find_delete_one 10 5 >> log_psql_pt.log 2>&1 &`
    #`$head_str jdbc:postgresql://127.0.0.1:31001/jelly_ios jelly null check_in find_insert_many 10 5 >> log_psql_pt.log 2>&1 &`
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
