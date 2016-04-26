package org.whb.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.whb.web.util.StackTraceUtil;

/**
 * 支持异步的HttpServlet，需要声明asyncSupported = true
 * 如果前端有Filter，则Filter也需要声明asyncSupported = true，见AsyncFilter
 * 
 * 异步其实是另外开启一个线程执行耗时任务，释放HttpServlet线程资源去处理其他请求。
 * 
 * @author 
 *
 */

@WebServlet(name = "Async", urlPatterns = "/async", asyncSupported = true)
public class AsyncHttpServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    
    private final ExecutorService executor = Executors.newScheduledThreadPool(2);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String mode = req.getParameter("mode");
        if(mode != null){
            switch(mode){
                case "1" : mode1(req, resp); break;
                case "2" : mode2(req, resp); break;
                default : mode1(req, resp); break;
            }
        }else{
            //重定向
            resp.sendRedirect("/404.html");
        }
    }

    protected void mode1(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        StackTraceUtil.printLocation();
        
        resp.setContentType("text/html;charset=utf-8");

        PrintWriter out = resp.getWriter();
        out.write("async 1 simple ");
        out.write("<br/>");
        out.flush();
        
        /**
         * 1、开启异步
         * 可以通过AsyncContext的getRequest()、getResponse()方法取得请求、响应对象
         * 此次对客户端的响应将暂缓至调用AsyncContext的complete()或dispatch()方法为止
         */
        final AsyncContext asyncContext = req.startAsync();
        /**
         * 2、设置超时时间10秒，设置为0表示永不超时
         */
        asyncContext.setTimeout(10L * 1000);
        /**
         * 3、告诉容器分派一个新的线程执行异步任务，可能和请求用同一个线程池
         */
        asyncContext.start(new Runnable() {
            @Override
            public void run() {
                try {
                    ServletResponse response = asyncContext.getResponse();
                    PrintWriter out = response.getWriter();
                    
                    // 如果Server容器使用的不是实现NIO的Connector，将不会看到response.flushBuffer()的输出，
                    // 而会在asyncContext.complete()时一次性输出
                    Thread.sleep(3L * 1000);
                    out.write("publish message <br />");
                    response.flushBuffer();  // 如果Client断开连接这里将抛出异常
                    
                    Thread.sleep(3L * 1000);
                    out.write("publish message <br />");
                    response.flushBuffer();
                    
                    Thread.sleep(3L * 1000);
                    out.write("over <br />");
                    
//                    asyncContext.complete();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    asyncContext.complete();
                }
            }
        });
    }
    
    protected void mode2(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        StackTraceUtil.printLocation();
        
        resp.setContentType("text/html;charset=utf-8");

        PrintWriter out = resp.getWriter();
        out.write("async 2 use ThreadPoolExecutor and simulate timeout ");
        out.write("<br/>");
        out.flush();
        
        //开启异步
        final AsyncContext asyncContext = req.startAsync();
        //设置超时时间
        asyncContext.setTimeout(10L * 1000);
        //注册AsyncListener
        asyncContext.addListener(new AsyncListener() {
            @Override
            public void onComplete(final AsyncEvent event) throws IOException {
                
            }

            @Override
            public void onTimeout(final AsyncEvent event) throws IOException {
                StackTraceUtil.printLocation();
                event.getAsyncContext().dispatch("/hello-world");
            }

            @Override
            public void onError(final AsyncEvent event) throws IOException {
                StackTraceUtil.printLocation();
                event.getAsyncContext().complete();
            }

            @Override
            public void onStartAsync(final AsyncEvent event) throws IOException {
                StackTraceUtil.printLocation();
            }
        });
        //把任务提交给指定线程池的任务队列
        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(15L * 1000);
                    asyncContext.complete();
                } catch (Exception e) {
                    //会抛出IllegalStateException
                    e.printStackTrace();
                }
            }
        });
    }
}
