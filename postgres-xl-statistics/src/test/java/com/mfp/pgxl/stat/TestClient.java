package com.mfp.pgxl.stat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
/**
 * 测试Http请求
 * @author whb
 *
 */
public class TestClient {

    public static void main(String[] args) {
//        sayHello();
//        getTaskProps();
        
        actuator("beans");
    }
    
    public static void sayHello(){
        String url = "http://localhost:8083/hello/say";
        String method = "POST";
        String contentType = "text/html; charset=utf8";
        String data = "";
        
        doHttp(url, method, contentType, data.getBytes());
    }
    
    public static void getTaskProps(){
        String url = "http://localhost:8083/hello/task";
        String method = "POST";
        String contentType = "text/html; charset=utf8";
        String data = "";
        
        doHttp(url, method, contentType, data.getBytes());
    }
    
    /**
     * 支持以下参数：
     * dump         打印线程栈
     * heapdump     下载heapdump.hprof文件
     * configprops  查看配置属性，包括默认配置
     * trace        查看基本追踪信息
     * health       查看应用健康指标
     * mappings     查看所有url映射
     * info         查看应用信息
     * env          查看所有环境变量
     * metrics      查看应用基本指标
     * autoconfig   查看自动配置的使用情况
     * beans        查看bean及其关系列表
     * shutdown     关闭应用，需要配置参数开启，endpoints.shutdown.enabled: true
     * 
     * @param tag
     */
    public static void actuator(String tag){
        String url = "http://localhost:8083/" + tag;
        String method = "GET";
        String contentType = "text/html; charset=utf8";
        String data = "";
        
        doHttp(url, method, contentType, data.getBytes());
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
