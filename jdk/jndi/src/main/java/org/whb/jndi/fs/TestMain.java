package org.whb.jndi.fs;

import javax.naming.Context;

import org.whb.jndi.fs.service.DBService;
import org.whb.jndi.fs.service.LogService;

public class TestMain {

	public static void main(String[] args) throws Exception {
	    FsJndiServer container = new FsJndiServer();
        container.init();

        // JNDI客户端使用标准JNDI接口访问命名服务。
        Context ctx = container.getContext();
        
        DBService db = (DBService) ctx.lookup("DBService");
        System.out.println(db.toString());
        db.accessDB();

        LogService ls = (LogService) ctx.lookup("LogService");
        ls.log("this is a log message.");

        container.close();
	}

}
