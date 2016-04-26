package org.whb.jndi.fs.service.impl;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

/**
 * 数据库服务对象工厂类被JNDI提供者调用来创建数据库服务实例，对使用JNDI的客户不可见。
 * @author
 *
 */
public class SimpleDBServiceFactory implements ObjectFactory {
    
	/**
	 * 根据Reference中存储的信息创建出SimpleDBService实例
	 */
	public Object getObjectInstance(Object obj, Name name, Context ctx,
			Hashtable<?, ?> env) throws Exception {
		if (obj instanceof Reference) {
			Reference ref = (Reference) obj;
			String location = (String) ref.get("location").getContent();
			String user = (String) ref.get("user").getContent();
			String password = (String) ref.get("password").getContent();
            
			SimpleDBService db = new SimpleDBService();
			db.setProperty(0, location);
			db.setProperty(1, user);
			db.setProperty(2, password);
			return db;
		}
		return null;
	}
}
