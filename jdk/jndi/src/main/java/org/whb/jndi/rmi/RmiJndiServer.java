package org.whb.jndi.rmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.whb.jndi.rmi.service.SimpleRmiService;
import org.whb.jndi.rmi.service.impl.SimpleRmiServiceImpl;

/**
 * RMI-JNDI服务端
 * 注册资源等待客户端持续调用
 * 资源接口需要继承java.rmi.Remote
 * 资源接口实现类需要继承java.rmi.server.UnicastRemoteObject
 * @author whb
 *
 */
public class RmiJndiServer {

	public static void main(String[] args) {
		try {
		    //为RMI注册端口8899
			LocateRegistry.createRegistry(8899);

			//创建InitialContext
			Properties properties = new Properties();
			properties.setProperty(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.rmi.registry.RegistryContextFactory");
			properties.setProperty(Context.PROVIDER_URL, "rmi://localhost:8899");
			InitialContext ctx = new InitialContext(properties);

			//绑定SimpleRmiServiceImpl到资源java:org/whb.jndirmi/RmiSimple
			SimpleRmiService server = new SimpleRmiServiceImpl();
			ctx.bind("java:org/whb.jndirmi/RmiSimple", server);
			ctx.close();

			System.out.println("RMI与JNDI集成服务启动.等待客户端调用...");
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}
}
