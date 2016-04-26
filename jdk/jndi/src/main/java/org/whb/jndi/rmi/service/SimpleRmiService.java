package org.whb.jndi.rmi.service;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.whb.jndi.rmi.pojo.TestPojo;

/**
 * 接口继承自Remote
 * @author whb
 *
 */
public interface SimpleRmiService extends Remote {

	public String sayHello(String name) throws RemoteException;
	
	public TestPojo savePojo(TestPojo original) throws RemoteException;
    
}
