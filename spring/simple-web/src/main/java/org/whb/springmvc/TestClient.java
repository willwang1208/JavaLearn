package org.whb.springmvc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.springframework.remoting.rmi.RmiProxyFactoryBean;
import org.whb.springmvc.service.IHelloWorldService;
/**
 * 测试Http请求
 * @author whb
 *
 */
public class TestClient {

    public static void main(String[] args) {
//        postOnly();
//        entityRead();
//        entityWrite();
//        jsonTran();
//        cacheGet();
//        asyncDeferredQueue();
        
//        restGet();
//        restPost();
//        restPut();
//        restDelete();
        
        rmiClient();
    }
    
    public static void postOnly(){
        String url = "http://localhost:8083/simple-web/mvc/hello-world/post/only";
        String method = "POST";
        String contentType = "text/html; charset=utf8";
        String data = "";
        
        doHttp(url, method, contentType, data.getBytes());
    }
    
    public static void entityRead(){
        String url = "http://localhost:8083/simple-web/mvc/hello-world/entity/read";
        String method = "POST";
        String contentType = "text/html; charset=utf8";
        String data = "";
        
        doHttp(url, method, contentType, data.getBytes());
    }

    public static void entityWrite(){
        String url = "http://localhost:8083/simple-web/mvc/hello-world/entity/write";
        String method = "POST";
        String contentType = "application/json; charset=utf8";
        String data = "{\"id\":20,\"name\":\"wang\",\"friends\":[\"peter\",\"中国人\"],\"height\":98}";
        
        doHttp(url, method, contentType, data.getBytes());   //服务器收到数据并正确解析，返回HttpStatus.FORBIDDEN=403
    }
    
    public static void jsonTran(){
        String url = "http://localhost:8083/simple-web/mvc/hello-world/json/tran";
        String method = "POST";
        String contentType = "application/json; charset=utf8";
        String data = "{\"id\":10,\"name\":\"li\",\"friends\":[\"zhang\",\"四哥\"],\"height\":118}";
        
        doHttp(url, method, contentType, data.getBytes());
    }
    
    public static void cacheGet(){
        String url = "http://localhost:8083/simple-web/mvc/app/cache/get/20";
        String method = "GET";
        String contentType = "text/html; charset=utf8";
        String data = "";
        
        doHttp(url, method, contentType, data.getBytes());
    }
    
    public static void asyncDeferredQueue(){
        //开启三个线程发出请求
        for (int i = 0; i < 3; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String url = "http://localhost:8083/simple-web/mvc/async/deferred/queue";
                    String method = "POST";
                    String contentType = "text/html; charset=utf8";
                    String data = "";
                    doHttp(url, method, contentType, data.getBytes());
                }
            }).start();
        }
        wait(3);
        //推送消息响应前面三个请求
        String url = "http://localhost:8083/simple-web/mvc/async/deferred/queue/pushmsg";
        String method = "POST";
        String contentType = "text/html; charset=utf8";
        String data = "";
        doHttp(url, method, contentType, data.getBytes());
        wait(1);
    }
    
    public static void restGet(){
        String url = "http://localhost:8083/simple-web/mvc/user/20";
        String method = "GET";
        String contentType = "text/html; charset=utf8";
        String data = "";
        
        doHttp(url, method, contentType, data.getBytes());
    }
    
    public static void restPost(){
        String url = "http://localhost:8083/simple-web/mvc/user";
        String method = "POST";
        String contentType = "application/json; charset=utf8";
        String data = "{\"id\":10,\"name\":\"li\",\"friends\":[\"zhang\",\"四哥\"],\"height\":118}";
        
        doHttp(url, method, contentType, data.getBytes());
    }
    
    public static void restPut(){
        String url = "http://localhost:8083/simple-web/mvc/user/20";
        String method = "PUT";
        String contentType = "application/json; charset=utf8";
        String data = "{\"id\":10,\"name\":\"li\",\"friends\":[\"zhang\",\"四哥\"],\"height\":118}";
        
        doHttp(url, method, contentType, data.getBytes());
    }
    
    public static void restDelete(){
        String url = "http://localhost:8083/simple-web/mvc/user/20";
        String method = "DELETE";
        String contentType = "text/html; charset=utf8";
        String data = "";
        
        doHttp(url, method, contentType, data.getBytes());
    }
    
    public static void rmiClient(){
        //创建远程代理
        RmiProxyFactoryBean rpfb = new RmiProxyFactoryBean();
        rpfb.setServiceUrl("rmi://localhost:21001/Hello");
        rpfb.setServiceInterface(IHelloWorldService.class);
        
        //发起连接并获得本地代理
        rpfb.afterPropertiesSet();
        IHelloWorldService hello = (IHelloWorldService)rpfb.getObject();
        
        //执行方法
        System.out.println(hello.getUser());
    }
    
    public static void doHttp(String url, String method, String contentType, byte[] request){
        HttpURLConnection conn = null;
        OutputStream os = null; 
        BufferedReader reader = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setUseCaches(false);
//            conn.setAllowUserInteraction(false);
//            conn.addRequestProperty("Authorization", "Basic YWRtaW4fYFgjkl5463");
            
            if("GET".equals(method)){
                //No need to send a request body for GET requests.
                
            }else{
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setRequestMethod(method);
                conn.setRequestProperty("Content-Type", contentType);
                conn.setRequestProperty("Content-Length", String.valueOf(request.length));
                
                os = conn.getOutputStream();
                os.write(request);
                os.flush();
                os.close();
            }
            
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuffer buf = new StringBuffer();
            String line = null;
            while((line = reader.readLine()) != null){
                buf.append(line);
            }
            
            System.out.println(buf);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(os != null){
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(conn != null){
                conn.disconnect();
            }
        }
    }
    
    public static void wait(int second){
        try {
            Thread.sleep(second * 1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
}
