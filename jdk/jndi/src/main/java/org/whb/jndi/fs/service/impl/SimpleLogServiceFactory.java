package org.whb.jndi.fs.service.impl;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
/**
 * 实现ObjectFactory接口
 * @author 
 *
 */
public class SimpleLogServiceFactory implements ObjectFactory {

	public Object getObjectInstance(Object obj, Name name, Context ctx,
			Hashtable<?, ?> env) throws Exception {
		if (obj instanceof Reference) {
			return new SimpleLogService();
		}
		return null;
	}
}
