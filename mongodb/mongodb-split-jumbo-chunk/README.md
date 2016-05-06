# Mongodb-split-jumbo-chunks

基于mongodb3.2版本。

Mongodb分片集群环境下会通过Balancer自动迁移各分片上的chunk，来达到数量均衡的目的。

但是当某个chunk的大小或其中的doc数量超过限制时，将抛出异常chunk too big to move，并标记该chunk为jumbo:true，不再迁移。

这时如果仍有均衡的需求就需要手动切分jumbo类型的chunk：

sh.splitAt("jelly_360.user_type_data",{_id:NumberLong("-4563171244068174933")})

这份代码的思路是在config数据库的chunks集合中找到所有jumbo的chunk，并计算middle point，执行split。

本来打算全部由java实现，但在执行时抛出Error: not connected to a mongos（原因待查），所以执行部分改为shell实现。


