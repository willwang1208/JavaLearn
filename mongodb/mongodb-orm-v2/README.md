# Mongodb-orm-v2
An orm-struct for Mongodb java driver 2.13.2

依赖于cglib自动生成java bean的包装类，实现java bean与com.mongodb.BasicDBObject之间的互转。
（参考net.sf.cglib.beans.BeanMap源码修改）

同时支持为java bean的变量定义别名（short name），作为com.mongodb.BasicDBObject的key的名字。

实现了面向对象方式的DAO层，支持大部分Mongodb的命令。

构建映射关系：

    /**
  
	 * ORM构造器
	 
	 * @param dbNeedShard 数据库是否要分片
	 
	 * @param scanPackage 扫描的class目录
	 
	 * @param db          Mongodb连接
	 
    */

    public OrmMongoBuilder(boolean dbNeedShard, String scanPackage, DB db) {

        super();
        
        this.dbNeedShard = dbNeedShard;
        
        this.scanPackage = scanPackage;
        
        this.db = db;
        
    }
    
调用：

OrmMongoBuilder omb_local = new OrmMongoBuilder(true, "org.whb.app.vo.mongo", getDb());

omb_local.build();

