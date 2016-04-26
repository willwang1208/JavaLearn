package org.whb.web.servlet;

import java.io.IOException;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.whb.web.util.StackTraceUtil;

/**
 * 非阻塞IO异步写入数据的HttpServlet
 * 通过向resp.getOutputStream()注册WriteListener的实现类完成
 * @author 
 *
 */

@WebServlet(name = "NioWrite", urlPatterns = "/niowrite", asyncSupported = true)
public class NioWriteAsyncServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        StackTraceUtil.printLocation();
        AsyncContext asyncContext = req.startAsync();
        asyncContext.setTimeout(0);
        asyncContext.addListener(new AsyncListener() {
            @Override
            public void onComplete(AsyncEvent event) throws IOException {
                StackTraceUtil.printLocation();
            }

            @Override
            public void onTimeout(AsyncEvent event) throws IOException {
                StackTraceUtil.printLocation();
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
        });
        
        ServletOutputStream output = resp.getOutputStream();
        //通过设置WriteListener来开启非阻塞写支持
        output.setWriteListener(new NioWriteListener(output, asyncContext));
    }

    class NioWriteListener implements WriteListener{
        
        private ServletOutputStream output;
        
        private AsyncContext asyncContext;
        
        private int count = 0;

        public NioWriteListener(ServletOutputStream output, AsyncContext asyncContext) {
            super();
            this.output = output;
            this.asyncContext = asyncContext;
        }

        @Override
        public void onWritePossible() throws IOException {
            // output.isReady()如果有可以无阻塞写数据(从false变为true时)，返回true
            boolean isClose = false;
            while(output.isReady()) {
                output.write((byte)'i');
                count++;

                //增加判断条件触发asyncContext.complete()
                //写1024 * 1000个就结束，即完成写
                if(count >= (1024 * 1000)) {
                    isClose = true;
                    System.out.println("服务器写完1024 * 1000个字节了");
                    output.close();
                    asyncContext.complete();
                    break;
                }
            }
            // 如果未写完出现阻塞，output.isReady()返回false
            if(!isClose) {
                //如果此时还输出true，表示还可以往外写 即速度很快
                System.out.println("Server blocked. output.isReady(): " + output.isReady());
            }
        }

        @Override
        public void onError(Throwable t) {
            t.printStackTrace(System.out);
            asyncContext.complete();
        }
    }
}
