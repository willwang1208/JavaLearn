package org.whb.springmvc.controller;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.whb.springmvc.entity.User;
import org.whb.springmvc.service.IAsyncService;
import org.whb.springmvc.service.ICacheService;
import org.whb.springmvc.service.IHelloWorldService;
import org.whb.springmvc.service.IHelloWorldService4Groovy;

@Controller
@RequestMapping("/app")
public class AppController {
    
    @Autowired
    @Qualifier("GS_HelloWorld")
    IHelloWorldService4Groovy gsHelloWorldService;
    
    @Autowired
    IAsyncService asyncService;
    
    @Autowired
    ICacheService cacheService;
    
    @Autowired
    IHelloWorldService helloWorldService;
    
    /**
     * 执行groovy脚本，需要配置 @link ScriptFactoryPostProcessor
     * @see org.whb.springmvc.config.ServiceConfiguration
     * 
     * @return
     */
	@RequestMapping(value="/groovy")
	@ResponseBody
    public String groovy() {
        return gsHelloWorldService.hello();
    }
	
	/**
	 * 异步服务，需要配置@EnableAsync
	 * @see org.whb.springmvc.config.ServiceConfiguration
	 * 
	 * @return
	 */
	@RequestMapping(value="/async/task")
    @ResponseBody
    public String asyncTask() {
	    asyncService.doAsyncTask();
        return "response back";
    }
	@RequestMapping(value="/async/exception")
    @ResponseBody
    public String asyncException() {
        asyncService.throwAsyncException();
        return "response back";
    }
	
	/**
     * 缓存服务，需要配置@EnableCaching
     * @see org.whb.springmvc.config.ServiceConfiguration
     * 
     * @return
     */
	@RequestMapping(value="/cache/get/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public User cacheGet(@PathVariable("id") int id) {
        return cacheService.findById(id);
    }
    @RequestMapping(value="/cache/put")
    @ResponseBody
    public User cachePut() {
        User user = helloWorldService.getUser();
        return cacheService.save(user);
    }
    @RequestMapping(value="/cache/delete/{id}")
    @ResponseBody
    public User cacheDelete(@PathVariable("id") int id) {
        return cacheService.remove(id);
    }
	
    /**
     * 上传文件，需要配置 @link CommonsMultipartResolver 
     * @see org.whb.springmvc.config.MvcConfiguration
     * 
     * @param files
     * @param model
     * @param request
     * @return
     */
    @RequestMapping(value="/upload")
    public String upload(@RequestParam("files") MultipartFile[] files, ModelMap model, HttpServletRequest request) {
        System.out.println(request.getParameter("username"));
        int count = 0;
        if(files != null && files.length > 0){
            for(int i = 0; i < files.length; i ++){
                if(files[i] != null && files[i].isEmpty() == false){
                    // 文件保存路径
//                    String filePath = "/tmp/upload000." + i;
                    String filePath = "E://upload000." + i;
                    // 转存文件
                    try {
                        files[i].transferTo(new File(filePath));
                        count ++;
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        model.addAttribute("count", count);
        return "upload";
    }
    @RequestMapping(value="/to-upload")
    public String toUpload(ModelMap model) {
        model.addAttribute("count", 0);
        return "upload";
    }
    
    
    
    
    
    /*
     * TODO  BinderController.java  表单   validator editor
     * MvcConfiguration
     * ArrayEditor NonnegativeIntegerEditor UserValidator
     * WebInterfaceController protobuf
     * spring-xxx.xml
     * 
     */
	
	/**
     * 当这个Controller中的其他方法抛出RuntimeException异常，将会被这个@ExceptionHandler方法拦截，处理异常后再返回
     * @param e
     * @return
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String exceptionHandler(Exception e) {
        return "exception: " + e.getMessage();
    }
}
