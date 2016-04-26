package org.whb.jndi.rmi.service.impl;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import org.whb.jndi.rmi.pojo.TestPojo;
import org.whb.jndi.rmi.service.SimpleRmiService;

/**
 * 资源实现类继承自UnicastRemoteObject
 * @author whb
 *
 */
public class SimpleRmiServiceImpl extends UnicastRemoteObject implements SimpleRmiService {

	private static final long serialVersionUID = 1L;

    public SimpleRmiServiceImpl() throws RemoteException {
        super();
    }

    public String sayHello(String name) throws RemoteException {
        return "SimpleRmiServiceImpl#sayHello: " + name;
    }
    
    public TestPojo savePojo(TestPojo original) throws RemoteException{
        System.out.println("get " + original.toString());
        original.setAge(original.getAge() + 100);
        return original;
    }

}
