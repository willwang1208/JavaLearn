#!/bin/bash 

pt_start(){
    /opt/jdk1.8.0_66/bin/java -jar MongodbPressTest.jar "find" 2 2000000000 >> /dev/null 2>&1 &
    
    /opt/jdk1.8.0_66/bin/java -jar MongodbPressTest.jar "find_first" 40 2000000000 >> /dev/null 2>&1 &
    
    /opt/jdk1.8.0_66/bin/java -jar MongodbPressTest.jar "find_insert_one" 10 2000000000 >> /dev/null 2>&1 &
    
    /opt/jdk1.8.0_66/bin/java -jar MongodbPressTest.jar "find_insert_many" 5 2000000000 >> /dev/null 2>&1 &
    
    /opt/jdk1.8.0_66/bin/java -jar MongodbPressTest.jar "find_update_one" 10 2000000000 >> /dev/null 2>&1 &
    
    /opt/jdk1.8.0_66/bin/java -jar MongodbPressTest.jar "find_delete_one" 5 2000000000 >> /dev/null 2>&1 &
}

pt_stop(){
    local pids=$(ps aux | grep MongodbPressTest.jar | grep -v 'grep' | awk '{print $2}')
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
