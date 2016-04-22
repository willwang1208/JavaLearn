package org.whb.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.whb.web.util.StackTraceUtil;

/**
 * 实现客户端延迟交互的HttpServlet
 * 发起者发出请求，在一定时间内等待响应；
 * 响应者处理后通知发起者的AsyncContext.complete，完成发起者的等待中的请求
 * @author 
 *
 */

@WebServlet(name = "Interactive", urlPatterns = "/interactive", asyncSupported = true)
public class InteractiveAsyncServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    
    /** 暂存的AsyncContext集合，集群环境下需要实现web容器间的通信或者类似zookeeper监听的机制 */
    private static final Map<String, AsyncContext> ASYNC_CONTEXT_MAP = new ConcurrentHashMap<>();
    
    /** 模拟数据库 */
    private static final Map<String, String> SIMULATE_DB = new ConcurrentHashMap<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getParameter("method");
        if(method != null){
            switch(method){
                case "sendGift" : sendGift(req, resp); break;
                case "tryToGetGift" : tryToGetGift(req, resp); break;
                case "confirm" : confirm(req, resp); break;
            }
        }
    }

    protected void sendGift(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        StackTraceUtil.printLocation();
        resp.setContentType("text/html;charset=utf-8");
        
        //这是一个唯一标识
        String sender = "Bill";
        
        PrintWriter out = resp.getWriter();
        out.write("我是" + sender + "，我发出一个红包，等待别人在30秒内领取<br/>");
        out.write("红包地址：<a target=\"_blank\" href=\"");
        out.write(req.getRequestURL().toString() + "?method=tryToGetGift&sender=" + sender);
        out.write("\">点击这里</a><br/>");
        out.flush();
        
        req.setAttribute("sender", sender);
        
        //开启异步，等待响应
        AsyncContext asyncContext = req.startAsync();
        asyncContext.setTimeout(30L * 1000);
        asyncContext.addListener(new SendGiftAsyncListener());
        
        //暂存持有这个AsyncContext，通过sender可以再次找到
        ASYNC_CONTEXT_MAP.put(sender, asyncContext);
    }
    
    protected void tryToGetGift(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        StackTraceUtil.printLocation();
        resp.setContentType("text/html;charset=utf-8");
        
        //这是一个唯一标识
        String receiver = "Tom";
        
        String sender = req.getParameter("sender");
        AsyncContext asyncContextOfSend = ASYNC_CONTEXT_MAP.get(sender);
        if(asyncContextOfSend != null){
            PrintWriter out = resp.getWriter();
            out.write("我是" + receiver + "，我想要领取" + sender + "的红包，等待对方确认<br/>");
            out.flush();
            
            //写入相关数据到DB <sender, receiver>
            SIMULATE_DB.put(sender, receiver);
            
            //开启异步，等待响应
            AsyncContext asyncContext = req.startAsync();
            asyncContext.setTimeout(30L * 1000);
            asyncContext.addListener(new TryToGetGiftAsyncListener());
            
            //暂存持有这个AsyncContext，通过receiver可以再次找到
            ASYNC_CONTEXT_MAP.put(receiver, asyncContext);
            
            //结束sender的AsyncContext，完成sender的异步响应
            asyncContextOfSend.complete();
            ASYNC_CONTEXT_MAP.remove(sender);
        }else{
            PrintWriter out = resp.getWriter();
            out.write("红包不存在<br/>");
            out.flush();
            out.close();
        }
    }
    
    protected void confirm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        StackTraceUtil.printLocation();
        resp.setContentType("text/html;charset=utf-8");
        
        String receiver = req.getParameter("receiver");
        AsyncContext asyncContextOfReceiver = ASYNC_CONTEXT_MAP.get(receiver);
        if(asyncContextOfReceiver != null){
            PrintWriter out = resp.getWriter();
            out.write("同意" + receiver + "领取红包<br/>");
            out.flush();
            out.close();
            
            //结束receiver的AsyncContext，完成receiver的异步响应
            asyncContextOfReceiver.complete();
            ASYNC_CONTEXT_MAP.remove(receiver);
        }else{
            PrintWriter out = resp.getWriter();
            out.write(receiver + "不存在<br/>");
            out.flush();
            out.close();
        }
    }
    
    class SendGiftAsyncListener implements AsyncListener{

        @Override
        public void onComplete(AsyncEvent event) throws IOException {
            StackTraceUtil.printLocation();
            
            //再次获取request和response
            HttpServletRequest requestOfSend = (HttpServletRequest)event.getAsyncContext().getRequest();
            HttpServletResponse responseOfSend = (HttpServletResponse)event.getAsyncContext().getResponse();
            
            if(requestOfSend.getAttribute("timeout") == null){
                String sender = (String)requestOfSend.getAttribute("sender");
                
                String receiver = SIMULATE_DB.get(sender);
                
                PrintWriter outOfSend = responseOfSend.getWriter();
                outOfSend.write(receiver + "想要领取你的红包，请确认<br/>");
                outOfSend.write("<a target=\"_blank\" href=\"");
                outOfSend.write(requestOfSend.getRequestURI() + "?method=confirm&receiver=" + receiver);
                outOfSend.write("\">同意</a><br/>");
            }else{
                PrintWriter outOfSend = responseOfSend.getWriter();
                outOfSend.write("超时无人领取<br/>");
            }
        }

        @Override
        public void onTimeout(AsyncEvent event) throws IOException {
            StackTraceUtil.printLocation();
            //再次获取request和response
            HttpServletRequest requestOfSend = (HttpServletRequest)event.getAsyncContext().getRequest();
            requestOfSend.setAttribute("timeout", 1);
            event.getAsyncContext().complete();
        }

        @Override
        public void onError(AsyncEvent event) throws IOException {
            StackTraceUtil.printLocation();
            event.getAsyncContext().complete();
        }

        @Override
        public void onStartAsync(AsyncEvent event) throws IOException {
            StackTraceUtil.printLocation();
        }
    }
    
    class TryToGetGiftAsyncListener implements AsyncListener{

        @Override
        public void onComplete(AsyncEvent event) throws IOException {
            StackTraceUtil.printLocation();
            //再次获取response
            HttpServletResponse responseOfReceiver = (HttpServletResponse)event.getAsyncContext().getResponse();
            
            PrintWriter outOfReceiver = responseOfReceiver.getWriter();
            outOfReceiver.write("领取成功<br/>");
        }

        @Override
        public void onTimeout(AsyncEvent event) throws IOException {
            StackTraceUtil.printLocation();
            //如果timeout后不结束AsyncContext，会重新发起request，并执行onStartAsync
        }

        @Override
        public void onError(AsyncEvent event) throws IOException {
            StackTraceUtil.printLocation();
        }

        @Override
        public void onStartAsync(AsyncEvent event) throws IOException {
            StackTraceUtil.printLocation();
        }
    }
}
