# AliyunECS

在阿里云上复制生产环境机群构建测试环境机群

main 函数支持两个参数：配置文件路径、方法名

例如：AliGo.main(new String[]{"/home/whb/ali/example.properties", "create"});

1.create：根据配置一键构建环境

2.destroy：根据配置一键销毁环境，不含镜像和快照 

3.describe：显示你在阿里云上拥有的所有主机 

4.force_create_snapshot：强制重新创建快照

5.force_create_image：强制重新创建镜像

创建快照会影响源机的性能，所以一般在线上用户不活跃时先执行4，以后再使用1的时候会默认使用最新的快照和镜像。
