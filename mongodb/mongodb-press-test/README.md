# Mongodb-press-test

基于mongo-java-driver-3.2.2.jar编写简单的增删改查操作，通过设置线程数量控制各类操作对mongodb集群的访问压力。

一般步骤：

1.导入测试数据

2.各主机运行  nohup ./linux_status_report.sh >> /tmp/status_report.out &

3.运行  ./mongodb_pt_runner.sh start  开始压测

4.观察输出log，tail -f log_mongodb_pt.log。输出项：[tag] [时间戳] [时间间隔内的执行次数] [总执行次数] [时间间隔内的异常次数] [总异常次数] [每秒执行次数] [每秒无异常执行次数] [方法平均执行时间ms]

5.运行  ./mongodb_pt_runner.sh stop  停止压测

