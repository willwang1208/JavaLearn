package org.whb.web;

import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.plus.webapp.EnvConfiguration;
import org.eclipse.jetty.plus.webapp.PlusConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.FragmentConfiguration;
import org.eclipse.jetty.webapp.JettyWebXmlConfiguration;
import org.eclipse.jetty.webapp.MetaInfConfiguration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebInfConfiguration;
import org.eclipse.jetty.webapp.WebXmlConfiguration;

public class JettyServer {

    public static void main(String[] args) throws Exception {
        int port = 9999;
        Server server = new Server(port);  
        
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
