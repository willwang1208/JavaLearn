package org.whb.web.servlet;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.whb.web.util.StackTraceUtil;

/**
 * 通过注解MultipartConfig设置上传时的参数，处理multipart/form-data类型编码的表单
 * 
 * @author 
 *
 */

@MultipartConfig(
        location = "E:\\upload_tmp",           //上传文件保存的临时路径
        maxRequestSize = 1024 * 1024 * 2,      //指定一次请求最大的上传数据量（上传的总和） 单位：字节， -1表示不限制
        maxFileSize = 1024 * 1024 * 1,         //设定单个文件的最大大小，-1表示不限制
        fileSizeThreshold = 1024 * 1024 * 10   //当上传的文件大小大于该值时才写入文件
)
@WebServlet(name = "Upload", urlPatterns = "/upload")
public class UploadHttpServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        StackTraceUtil.printLocation();
        
        req.setCharacterEncoding("utf-8");
        
        Part part = req.getPart("myFile");
        
        //获取HTTP头信息headerInfo=(form-data; name="file" filename="文件名")
        String headerInfo = part.getHeader("content-disposition");
        String fileName = headerInfo.substring(headerInfo.lastIndexOf("=") + 2, headerInfo.length() - 1);
        
        //获得存储上传文件的文件夹路径和文件名称(采用与上传文件的原始名字相同)
        String fileSavingFolder = this.getServletContext().getRealPath("/upLoad");
        String fileSavingPath = fileSavingFolder + File.separator + fileName;
        File f = new File(fileSavingFolder + File.separator);
        if(!f.exists()){
            f.mkdirs();
        }
        
        //将上传的文件内容写入服务器文件中
        part.write(fileSavingPath);
        
        //可以选择删除临时文件
        part.delete();
        
//        for(Part each : req.getParts()) {
//            InputStream is = each.getInputStream();
//            is.close();
//        }
        
        resp.getWriter().println("done");
    }

}
