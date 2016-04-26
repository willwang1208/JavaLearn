package org.whb.web;

import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.plus.webapp.EnvConfiguration;
import org.eclipse.jetty.plus.webapp.PlusConfiguration;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.NetworkTrafficServerConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.FragmentConfiguration;
import org.eclipse.jetty.webapp.JettyWebXmlConfiguration;
import org.eclipse.jetty.webapp.MetaInfConfiguration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebInfConfiguration;
import org.eclipse.jetty.webapp.WebXmlConfiguration;

/**
 * Jetty服务端
 * 
 * 扩展：tomcat设置实现NIO的Connector，protocol="org.apache.coyote.http11.Http11NioProtocol"
 * <!-- NIO HTTP/1.1 connector -->
 * <Connector port="8080" protocol="org.apache.coyote.http11.Http11NioProtocol"
     connectionTimeout="20000" asyncTimeout="150000" URIEncoding="utf-8" redirectPort="8443" />

 * @author whb
 * 
 */
public class JettyServer {
    
    public static void main(String[] args) throws Exception {
        int port = 9999;
//        Server server = new Server(port);
        //使用实现NIO的Connector，NetworkTrafficServerConnector
        Server server = new Server();
        NetworkTrafficServerConnector connector = new NetworkTrafficServerConnector(server);
        connector.setPort(port);
        server.setConnectors(new Connector[]{connector});
        
        WebAppContext context = new WebAppContext();
        context.setResourceBase("src/main/");
        context.setConfigurations(new Configuration[] {
            new AnnotationConfiguration(), 
            new WebInfConfiguration(), 
            new WebXmlConfiguration(), 
            new MetaInfConfiguration(), 
            new FragmentConfiguration(), 
            new EnvConfiguration(), 
            new PlusConfiguration(), 
            new JettyWebXmlConfiguration() 
        });
        context.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",".*/classes/.*");
        context.setContextPath("/");
        context.setParentLoaderPriority(true);
        
        server.setHandler(context);
        
        server.start();
        server.join();
    }
}
