package org.whb.spring.boot.web.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.whb.spring.boot.web.properties.TaskProperties;
import org.whb.spring.boot.web.service.HelloWorldService;

@RestController
@RequestMapping("/hello")
public class HelloController {
    
    @Resource
    TaskProperties taskProperties;
    
    @Autowired
    HelloWorldService helloWorldService;

    @RequestMapping("/say")
    public String say(HttpServletRequest request, ModelMap model) {
        return "hello spring boot";
    }
    
    @RequestMapping("/task")
    public TaskProperties task() {
        return taskProperties;
    }
    
    @RequestMapping("/users")
    public void users() {
        helloWorldService.getUsers();
    }
}
