package org.whb.jndi.rmi;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.whb.jndi.rmi.pojo.TestPojo;
import org.whb.jndi.rmi.service.SimpleRmiService;

/**
 * RMI-JNDI客服端
 * 通过名字获取资源并执行远程方法
 * @author whb
 *
 */
public class RmiJndiClient {

	public static void main(String[] args) {
		try {
		    //创建InitialContext
		    Properties properties = new Properties();
	        properties.setProperty(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.rmi.registry.RegistryContextFactory");
	        properties.setProperty(Context.PROVIDER_URL, "rmi://localhost:8899");
			InitialContext ctx = new InitialContext(properties);
			
			//通过RMI查找JNDI资源并执行
			SimpleRmiService remote = (SimpleRmiService) ctx.lookup("java:org/whb.jndirmi/RmiSimple");
			
			System.out.println(remote.sayHello("whb"));
			
			TestPojo pojo = new TestPojo();
			pojo.setAge(10);
			List<String> list = new ArrayList<>();
			list.add("tom");
			list.add("kate");
			pojo.setMsgs(list);
			System.out.println(remote.savePojo(pojo));
			
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}
