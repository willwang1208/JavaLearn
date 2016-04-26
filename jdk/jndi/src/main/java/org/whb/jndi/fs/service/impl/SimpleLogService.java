package org.whb.jndi.fs.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;

import org.whb.jndi.fs.service.LogService;

/**
 * 资源实现Referenceable接口
 * @author
 *
 */
public class SimpleLogService implements Referenceable, LogService {

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * SimpleLogService没有任何属性，通过SimpleLogService类名创建出来的SimpleLogService实例都是一样的，
	 * 因此这里无需添加RefAddr了。
	 */
	public Reference getReference() throws NamingException {
		return new Reference(getClass().getName(), SimpleLogServiceFactory.class.getName(), null);
//	    return new Reference(getClass().getName());
	}

	public void log(String message) {
		String date = sdf.format(new Date());
		System.out.println(date + ":" + message);
	}
}
