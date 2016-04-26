package org.whb.jndi.fs;

import java.io.InputStream;
import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.whb.jndi.fs.service.DBService;
import org.whb.jndi.fs.service.LogService;
/**
 * 使用SUN的文件系统服务提供者（没有包含在JDK中）
 * @author whb
 *
 */
public class FsJndiServer {

	private Context ctx;

	public void init() throws Exception {
		//创建InitialContext
		Hashtable<String, Object> env = new Hashtable<>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.fscontext.RefFSContextFactory");
		env.put(Context.PROVIDER_URL, "file:/e:/chss"); // fscontext的初始目录，在c:\下创建sample目录。
		ctx = new InitialContext(env);
		
		//加载服务资源
		loadServices();
	}

	private void loadServices() throws Exception {
	    //从资源文件fs-services.properties中读取DBService和LogService实现，绑定到Context中
		InputStream in = getClass().getResourceAsStream("fs-services.properties");
		Properties props = new Properties();
		props.load(in);

		//DBService
		Object obj = Class.forName(props.getProperty("DBServiceClass")).newInstance();
		if (obj instanceof DBService) {
			DBService db = (DBService) obj;
			String[] ss = props.getProperty("DBServiceProperty").split(";");
			for (int i = 0; i < ss.length; i++)
				db.setProperty(i, ss[i]);
			ctx.rebind(props.getProperty("DBServiceName"), db);
		}

		//LogService
		obj = Class.forName(props.getProperty("LogServiceClass")).newInstance();
		if (obj instanceof LogService) {
			LogService log = (LogService) obj;
			ctx.rebind(props.getProperty("LogServiceName"), log);
		}
	}

	public void close() throws NamingException {
		ctx.close();
	}

	public Context getContext() {
		return ctx;
	}
}
