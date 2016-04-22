package org.whb.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.whb.web.util.StackTraceUtil;
/**
 * 通过注解WebServlet注册HttpServlet
 * @author 
 *
 */

@WebServlet(name = "HelloWorld", 
    urlPatterns = {"/hello-world", "/hello-world/*"}, 
    loadOnStartup = 1,
    initParams = {
        @WebInitParam(name = "shared-msg", value="hello world"), 
        @WebInitParam(name = "msg", value="hello servlet")
    })
public class HelloWorldHttpServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    
    /** 多线程共享的变量 */
    private String sharedMsg;

    public HelloWorldHttpServlet() {
        super();
        StackTraceUtil.printLocation();
    }

    /**
     * HelloWorldHttpServlet销毁时调用一次
     */
    @Override
    public void destroy() {
        super.destroy();
        StackTraceUtil.printLocation();
    }

    /**
     * HelloWorldHttpServlet初始化时调用一次
     */
    @Override
    public void init() throws ServletException {
        super.init();
        StackTraceUtil.printLocation();
        sharedMsg = this.getInitParameter("shared-msg");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        StackTraceUtil.printLocation();
        
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        try {
            // write some content
            out.println("<html>");
            out.println("<head>");
            out.println("<title>HelloWorldHttpServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h2>HelloWorldHttpServlet</h2>");
            out.println("<br />");
            out.println("Private attribute sharedMsg: " + sharedMsg);
            out.println("<br />");
            out.println("InitParameter msg: " + this.getInitParameter("msg"));
            out.println("<br />");
            out.println("req.getContextPath(): " + req.getContextPath());
            out.println("<br />");
            out.println("req.getParameterValues(\"p\"): " + Arrays.toString(req.getParameterValues("p")));
            out.println("<br />");
            out.println("req.getQueryString(): " + req.getQueryString());
            out.println("<br />");
            out.println("req.getRequestedSessionId(): " + req.getRequestedSessionId());
            out.println("<br />");
            out.println("req.getDispatcherType(): " + req.getDispatcherType());
            out.println("<br />");
            out.println("req.getServletContext().getResourcePaths(\"/\"): " + req.getServletContext().getResourcePaths("/"));
            out.println("<br />");
            out.println("req.getServletContext().getVirtualServerName(): " + req.getServletContext().getVirtualServerName());
            out.println("<br />");
            out.println("</body>");
            out.println("</html>");
        } finally {
            out.close();
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        StackTraceUtil.printLocation();
    }

    @Override
    protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        StackTraceUtil.printLocation();
    }
    
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        StackTraceUtil.printLocation();
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        StackTraceUtil.printLocation();
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        StackTraceUtil.printLocation();
    }

    @Override
    protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        StackTraceUtil.printLocation();
    }

}
