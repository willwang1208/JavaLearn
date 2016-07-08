package org.whb.springmvc.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.whb.springmvc.controller.view.GenericExcelView;
import org.whb.springmvc.controller.view.GenericPdfView;
import org.whb.springmvc.entity.Book;
import org.whb.springmvc.entity.Report;
import org.whb.springmvc.entity.User;
import org.whb.springmvc.service.IHelloWorldService;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/hello-world")
public class HelloWorldController {
    
    @Autowired
    ServletContext servletContext;
    
    @Autowired
    IHelloWorldService helloWorldService;
    
    @RequestMapping(value = "/jsp-view")
    public String jsp(HttpServletRequest request, ModelMap model) {
        String name = ServletRequestUtils.getStringParameter(request, "name", "defaultName");
        model.addAttribute("name", name);
        return "hellojsp";
    }

    @RequestMapping(value = "/ftl-view")
    public String ftl() {
        return "helloftl";
    }

    @RequestMapping(value = "/gtl-view")
    public String gtl() {
        return "hellogtl";
    }

    @RequestMapping(value = "/vm-view")
    public String vm() {
        return "hellovm";
    }
    
    @RequestMapping(value = "/post/only", method = RequestMethod.POST)
    public void postOnly(HttpServletResponse response) {
        try {
            response.getOutputStream().write("ok".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @RequestMapping(value = "/redirect", method = RequestMethod.GET)
    public ModelAndView redirect(HttpServletRequest request) throws Exception {
//        return new ModelAndView(new RedirectView("jsp-view"), "name", "peter");
        return new ModelAndView(new RedirectView(request.getContextPath() + request.getServletPath() + "/hello-world/jsp-view"), "name", "peter");
    }
    
    @RequestMapping(value = "/redirect2", method = RequestMethod.GET)
    public String redirect2() throws Exception {
        return "redirect:jsp-view?name=999";
    }
    
    @RequestMapping(value = "/forward", method = RequestMethod.GET)
    public ModelAndView forward(ModelMap model) throws Exception {
        model.addAttribute("name", "tom-forward");
        return new ModelAndView("hellojsp", model);
    }
    
    @RequestMapping("/entity/read")
    public ResponseEntity<User> read() {
        return ResponseEntity.ok(helloWorldService.getUser());
    }
    
    @RequestMapping("/entity/write")
    public ResponseEntity<String> write(RequestEntity<User> requestEntity) {
        System.out.println(requestEntity.getBody());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        String body = "The String ResponseBody with custom status code";
        return new ResponseEntity<String>(body, headers, HttpStatus.FORBIDDEN);
//        return new ResponseEntity<String>(body, HttpStatus.OK);
    }
    
    @RequestMapping("/jackson/book/summary")
    @ResponseBody
    @JsonView(Book.Summary.class)
    public List<Book> bookSummary(){
        return helloWorldService.getBooks();
    }
    
    @RequestMapping("/jackson/book/detail")
    @ResponseBody
    @JsonView(Book.SummaryWithDetail.class)
    public List<Book> bookDetail(){
        return helloWorldService.getBooks();
    }
    
//    @RequestMapping(value = "/json/tran")
    @RequestMapping(value = "/json/tran", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public User json(@RequestBody User user) {
        user.setRemark("服务器新值");
        return user;
    }
    
    /**
     * 代码中调用json序列化工具，返回json字符串
     * 如果不指定produces = "text/html; charset=utf8"，响应结果会乱码
     * @return
     */
    @RequestMapping(value = "/json/string", produces = "text/html; charset=utf8")
    @ResponseBody
    public String string() {
        User user = helloWorldService.getUser();
        
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(user);
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 通过@PathVariable获取URL参数
     * 通过@RequestParam获取Request请求参数
     * 通过@RequestHeader获取Header参数
     * 
     * @param username
     * @param age
     * @param type
     * @param isZip
     */
    @RequestMapping("/params/{username}/{age}")
    public void params(@PathVariable("username") String username, @PathVariable int age, 
            @RequestParam(value = "type", required = false) String type, 
            @RequestHeader(value = "X-ZIP", defaultValue = "false") boolean isZip) {
        System.out.println("username: " + username);
        System.out.println("age: " + age);
        System.out.println("type: " + type);
        System.out.println("isZip: " + isZip);
    }
    
    /**
     * http://localhost:8083/simple-web/mvc/hello-world/login?command=login&username=111
     * @param out
     * @param session
     * @param username
     */
    @RequestMapping(value = "/login", params = "command=login")
    public void login(PrintWriter out, HttpSession session, @RequestParam("username") String username) {
        session.setAttribute("username", username);
        out.println("login: " + username);
    }
    
//    @RequestMapping(value = "/xml")
    @RequestMapping(value = "/xml", produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public Report xml() {
        return helloWorldService.getReport();
    }
    
    @RequestMapping(value = "/excel")
    @ResponseBody
    public ModelAndView excel() throws Exception {
        Map<String, String> data = helloWorldService.getMapData();
        ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        GenericExcelView view = (GenericExcelView)context.getBean("GenericExcelView");
        return new ModelAndView(view, "revenueData", data);
    }
    
    @RequestMapping(value = "/pdf")
    @ResponseBody
    public ModelAndView pdf() {
        Map<String, String> data = helloWorldService.getMapData();
        ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        GenericPdfView view = (GenericPdfView)context.getBean("GenericPdfView");
        return new ModelAndView(view, "revenueData", data);
    }
    
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
