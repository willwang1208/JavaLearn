# Mongodb-press-test

基于mongo-java-driver-3.2.2.jar编写简单的增删改查操作，通过设置线程数量控制各类操作对mongodb集群的访问压力。

通过linux_status_report.sh采集系统主要性能指标和mongodb主要状态参数，将输出结果导入Excel做图表分析，一方面可以观察mongodb的性能和特点，另一方面可以对比主机性能差异。

不同主机需要依据实际情况调整linux_status_report.sh中的一些参数。

一般步骤：

1.导入测试数据

2.各主机运行  nohup ./linux_status_report.sh >> /tmp/status_report.out &

3.运行  ./mongodb_pt_runner.sh start  开始压测

4.运行  ./mongodb_pt_runner.sh stop  停止压测

