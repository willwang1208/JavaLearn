#!/bin/bash 
# 通过MongodbSplitJumboChunk.jar生成mongodb shell命令
# 再用脚本读取命令并执行
#

daytime=$(date +%Y%m%d%H%M%S)
tmp_cmds_file=/tmp/tmp_mongo_cmds_$daytime
tmp_cmds_result_file=/tmp/tmp_mongo_cmds_result_$daytime

/mnt/jdk1.8.0_66/bin/java -jar MongodbSplitJumboChunk.jar only > $tmp_cmds_file

while read line
do
    echo $line
    eval "/alidata1/mongodb-3.2.3/bin/mongo config --port 20000 --eval '$line' >> $tmp_cmds_result_file"
done < $tmp_cmds_file

