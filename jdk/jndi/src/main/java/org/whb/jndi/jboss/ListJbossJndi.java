package org.whb.jndi.jboss;

import java.io.FileInputStream;
import java.util.Properties;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
/**
 * 列出jboss上绑定的jndi资源
 * @author 
 *
 */
public class ListJbossJndi {

	public static void main(String[] args) {
		try {
			Properties env = new Properties();
			env.load(new FileInputStream("jboss-services.properties"));
			// env.list(System.out);
			Context ctx = new InitialContext(env);
			listCtx(ctx.lookup("sylilzy"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void listCtx(Object o) {
		if (!(o instanceof Context))
			log(":" + o);
		else {
			log("\n-----------------------------");
			try {
				Context ctx = (Context) o;
				// log(ctx.getNameInNamespace()+"/:");
				NamingEnumeration<Binding> list = ctx.listBindings("");
				while (list.hasMore()) {
					Binding bind = list.next();
					log("\n/" + ctx.getNameInNamespace() + "/" + bind.getName());
					listCtx(bind.getObject());
				}
				log("\n-----------------------------");
			} catch (NamingException e) {
				e.printStackTrace();
			}
		}
	}

	static void log(Object o) {
		System.out.print(o);
	}
}
