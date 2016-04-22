package org.whb.web;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class TestNioWriteClient {

    public static void main(String[] args) throws MalformedURLException, IOException {

        String url = "http://localhost:9999/niowrite";
        
        HttpURLConnection conn = (HttpURLConnection) new URL(url.toString()).openConnection();
        //设置是否从httpUrlConnection输入，默认true
        conn.setDoOutput(true);
        //get方式提交
        conn.setRequestMethod("GET");

        conn.connect();

        InputStream is = new BufferedInputStream(conn.getInputStream());//此处可以使用非阻塞读（目前是阻塞读）

        int count = 0;
        int ch = -1;
        while((ch = is.read()) != -1) {
            count++;
            if(count % (1024 * 100) == 0) {//读1024个字节暂停200毫秒 用于观察非阻塞写
                System.out.println("客户端暂停读取1秒..,已读取:" + count + "个字节");
                try{
                    Thread.sleep(1000L);
                } catch (Exception e) {
                }
            }

            //客户端可强行终止连接
            //此处的目的是为了模拟出客户端主动关闭的情况，即服务器发送了1024*1000，但客户端只读取到1024*500，此时可能导致连接提前中断，服务器写失败
            //需要注释掉后边的is.close();conn.disconnect();
//            if(count >= (1024)) {
//                System.out.println("客户端接收完1024 * 1000个字节");
//                //这将导致服务器端可能还没写完数据，就遭遇了java.io.IOException: 您的主机中的软件放弃了一个已建立的连接
//                //此时服务器端将回调WriteListener#onError
//                is.close();
//                conn.disconnect();
//                break;
//            }
        }

        is.close();
        conn.disconnect();
    }

}
