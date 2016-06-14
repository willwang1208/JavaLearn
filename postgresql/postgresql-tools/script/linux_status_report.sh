#!/bin/bash 
# ./linux_status_report.sh 
# nohup ./linux_status_report.sh >> /tmp/status_report.out &

status_disk_io(){
    tail_n=$1;
    util=$(iostat -x 1 2 | tail -n$tail_n | head -n1 | awk '{print $NF}')
    echo $util
}

status_cpu(){
    item_n=$1;
    usage=$(top -b -n 2 |grep Cpu |tail -n 1 |awk '{print $'$item_n'}')
    echo $usage
}

status_load(){
    item_n=$1;
    #load=$(top -bn1 | grep load | awk '{printf "CPU Load: %.2f\n", $(NF-2)}')
    load=$(top -bn1 | grep load | awk '{print $(NF-'$item_n')+0}')
    echo $load
}

status_memory(){
    #util=$(free -m | awk 'NR==2{printf "Memory Usage: %s/%sMB (%.2f%%)\n", $3,$2,$3*100/$2 }')
    util=$(free -m | awk 'NR==2{printf "%.2f\n", ($2-$4-$5-$6-$7)*100/$2}')
    echo $util
}

status_cpu_and_disk_io(){
    cpu=$1;
    disk_1=$2;
    disk_2=$3;
    util=$(iostat -x 1 2 | awk 'NR=='$cpu'{printf $1" "$3" "$4" "} NR=='$disk_1'{printf $NF" "} NR=='$disk_2'{printf $NF}' )
    echo $util
}

status_mongodb(){
    cmd=$1;
    port=$2;
    #mongo=$($cmd --port $port -n2 1 | tail -n1 | awk '{print substr($1,2)" "substr($2,2)" "substr($3,2)" "substr($4,2)" "substr($5,2)}' )
    mongo=$($cmd --port $port -n2 1 | tail -n1 | awk '{print $1" "$2" "$3" "$4" "$5" "$6" "$11" "$12}' )
    echo $mongo
}

main(){
    daytime=$(date +%Y-%m-%d_%H:%M:%S)
    
    #disk_io_util_1=$(status_disk_io 2)
    #disk_io_util_2=$(status_disk_io 3)
    
    #cpu_usage=$(status_cpu 2)
    
    cpu_usage_and_disk_io=$(status_cpu_and_disk_io 11 15 100)
    
    load_1=$(status_load 2)
    load_5=$(status_load 1)
    load_15=$(status_load 0)
    
    memory_util=$(status_memory)
    
    mongo=$(status_mongodb /opt/mongodb-3.2.3/bin/mongostat 20000)
    
    #echo $daytime" "$cpu_usage" "$load_1" "$load_5" "$load_15" "$memory_util" "$disk_io_util_1" "$disk_io_util_2
    echo $daytime" "$cpu_usage_and_disk_io" "$load_1" "$load_5" "$load_15" "$memory_util" "$mongo
}

#monitor
while [ true ]
do
    main
    sleep 1
done

