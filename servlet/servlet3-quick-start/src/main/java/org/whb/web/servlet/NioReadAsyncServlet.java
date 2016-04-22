package org.whb.web.servlet;

import java.io.IOException;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.whb.web.util.StackTraceUtil;

/**
 * 非阻塞IO异步读取数据的HttpServlet
 * 通过向req.getInputStream()注册ReadListener的实现类完成
 * @author 
 *
 */

@WebServlet(name = "NioRead", urlPatterns = "/nioread", asyncSupported = true)
public class NioReadAsyncServlet extends HttpServlet {

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
        
        ServletInputStream input = req.getInputStream();
        //通过设置ReadListener来开启非阻塞读支持
        input.setReadListener(new NioReadListener(input, asyncContext));
    }

    class NioReadListener implements ReadListener{
        
        private ServletInputStream input;
        
        private AsyncContext asyncContext;
        
        public NioReadListener(ServletInputStream input, AsyncContext asyncContext) {
            super();
            this.input = input;
            this.asyncContext = asyncContext;
        }

        @Override
        public void onDataAvailable() throws IOException {
          //当有可用数据时由容器发起回调（容器仅当isReady方法返回true时才调用，直到返回false）
            //is.isReady:如果有可以无阻塞获取的数据时(从false变为true时)，返回true，即当该方法返回true时去读取数据，肯定是不阻塞的

            //当input.isReady()返回true时去读取数据，肯定是不阻塞的
            System.out.println("服务器读取到数据:>>");
            int ch = -1;
            while(input.isReady() && ((ch = input.read()) != -1)) {
                System.out.write((char) ch);
            }
            System.out.println("<<等待..");
        }

        @Override
        public void onAllDataRead() throws IOException {
            System.out.println("服务器数据读取完毕:" + input.isFinished());
            asyncContext.complete();
        }

        @Override
        public void onError(Throwable t) {
            System.out.println("\n服务器读取失败");
            t.printStackTrace(System.out);
            asyncContext.complete();
        }
    }
}
