package org.whb.web;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class TestNioReadClient {

    public static void main(String[] args) throws MalformedURLException, IOException {

        String url = "http://localhost:9999/nioread";
        
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        //设置是否向httpUrlConnection输出，默认false
        conn.setDoOutput(true);
        //设置块大小（字节），当写满后自动flush（这个用于不知道实际大小，以块的方式去写）
        //sun.net.www.http.ChunkedOutputStream
        conn.setChunkedStreamingMode(2);

        //sun.net.www.protocol.http.HttpURLConnection#StreamingOutputStream
        //设置固定长度（如文件上传，我们知道实际要传输的大小）
        //conn.setFixedLengthStreamingMode(1024);

        //默认是如下输出流，即写完最后自己flush，即只有最后关闭时才刷出数据
        //sun.net.www.http.PosterOutputStream

        //post方式提交
        conn.setRequestMethod("POST");
        OutputStream os = new BufferedOutputStream(conn.getOutputStream());

        for(int i = 1; i <= 10 * 100; i++) {
            os.write(("hello" + i).getBytes("ISO-8859-1"));
            try {
                if(i % 20 == 0) { //每写20次 flush下，等待1秒
                    os.flush();
                    Thread.sleep(1000L);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        os.close();//如果注释掉这行，可能导致服务器端没有读取完数据抛出java.io.EOFException，此刻将回调ReadListener#onError
        conn.disconnect(); //释放连接
    }

}
