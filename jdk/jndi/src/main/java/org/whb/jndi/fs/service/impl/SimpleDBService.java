package org.whb.jndi.fs.service.impl;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.StringRefAddr;

import org.whb.jndi.fs.service.DBService;

/**
 * 资源实现Referenceable接口
 * @author
 *
 */
public class SimpleDBService implements Referenceable, DBService {

    private String location;
    
    private String user;
    
    private String password;
    
    /**
     * Reference是对象的引用，Context中存放的是Reference，为了从Reference中还原出对象实例，
     * 我们需要添加RefAddr，它们是创建对象实例的线索。在我们的SimpleDBService中，location、user和password是这样三个线索。
     */
    public Reference getReference() throws NamingException {
        Reference ref = new Reference(getClass().getName(), SimpleDBServiceFactory.class.getName(), null);
        ref.add(new StringRefAddr("location", location));
        ref.add(new StringRefAddr("user", user));
        ref.add(new StringRefAddr("password", password));
        return ref;
    }

    public void accessDB() {
        System.out.println("we are accessing DB. " + toString());
    }

    @Override
    public void setProperty(int index, String value) {
        switch(index){
            case 0: location = value; break;
            case 1: user = value; break;
            case 2: password = value; break;
        }
    }

    @Override
    public String toString() {
        return "SimpleDBService [location=" + location + ", user=" + user + ", password=" + password + "]";
    }
}
