#!/bin/bash 
# 通过MongodbSplitJumboChunk.jar生成mongodb shell命令
# 再用脚本读取命令并执行
#

host="172.31.48.14:20000"

daytime=$(date +%Y%m%d%H%M%S)
tmp_cmds_file=/tmp/tmp_mongo_cmds_$daytime
tmp_cmds_result_file=/tmp/tmp_mongo_cmds_result_$daytime

/opt/jdk1.8.0_66/bin/java -jar MongodbSplitJumboChunk.jar $host only > $tmp_cmds_file

while read line
do
    echo $line
    eval "/opt/mongodb-3.2.6/bin/mongo config --host $host --eval '$line' >> $tmp_cmds_result_file"
done < $tmp_cmds_file

