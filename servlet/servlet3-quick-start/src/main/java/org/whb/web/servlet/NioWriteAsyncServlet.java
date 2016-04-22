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
            // TODO Auto-generated method stub
         // isReady : 类似于ServletInputStream的isReady，如果有可以无阻塞写数据(从false变为true时)，返回true
            boolean isClose = false;
            while(output.isReady()) {
                output.write((byte)'i');
                count++;

                //此处即需要用户判断什么时候结束写，如果不及时处理，可能遭遇timeout
                if(count >= (1024 * 1000)) { //写1024 * 1000个就结束 即完成写（目的是发现阻塞的情况）
                    isClose = true;
                    System.out.println("服务器写完1024 * 1000个字节了");
                    output.close();
                    asyncContext.complete();
                    break;
                }
            }
            if(!isClose) {//忽略写完的情况
                //如果此时还输出true，表示还可以往外写 即速度很快
                System.out.println("服务器当前可写:" + output.isReady());//即阻塞时，返回false
            }
        }

        @Override
        public void onError(Throwable t) {
            // TODO Auto-generated method stub
          //使用非阻塞I/O写失败时回调
            System.out.println("\n服务器写入失败了");
            t.printStackTrace(System.out);
            asyncContext.complete();
        }
    }
}
