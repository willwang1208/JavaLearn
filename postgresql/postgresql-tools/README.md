# Postgresql-Tools

包含BSON数据导入到PostgresSQL和PostgresSQL压力测试。

###BSON数据导入

BSON数据是从Mongodb导出的gz文件，导入到PostgresSQL时创建统一样式的表。

表名与mongodb中的一致，包含两个字段id和data。主键是id，类型根据mongodb数据文档中的_id的类型转换。data字段是jsonb类型，mongodb的文档类型的数据保存在data字段中。

命令：

nohup /opt/jdk1.8.0_66/bin/java -jar postgresql-tools-jar-with-dependencies.jar BsonImport jdbc:postgresql://127.0.0.1:31001/jelly_ios jelly null /mfpdata1/2016-05-26/dump/jelly_ios/ >> out22.log 2>&1 &

###PostgresSQL压力测试

基于C3P0连接池实现，支持从驱动端采集测试样本，包含各种方法的每秒执行次数和平均执行时间。

命令：

./psql_pt_runner.sh start

./psql_pt_runner.sh stop

log输出项：[tag] [时间戳] [时间间隔内的执行次数] [总执行次数] [时间间隔内的异常次数] [总异常次数] [每秒执行次数] [每秒无异常执行次数] [方法平均执行时间ms]

