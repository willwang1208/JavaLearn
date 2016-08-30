package org.whb.spring.boot.web.service;

import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class HelloWorldService {
    
    private final static Logger logger = LoggerFactory.getLogger(HelloWorldService.class);

//    @Autowired
//    private JdbcTemplate jdbcTemplate;
    
    /**
     * 读取配置文件中的customize.my.secret
     */
    @Value("${customize.my.secret}")
    String secret;
    
    /**
     * 读取配置文件中的customize.readnull，如果该值不存在则使用缺省值"Hello World"
     */
    @Value("${customize.readnull:Hello World}")
    String readnull;
    
    @PostConstruct
    public void postConstruct(){
        logger.info("secret: " + secret);
        
    }
    
    public List<?> getUsers(){
//        return jdbcTemplate.queryForList("select * from user");
        return null;
    }
    
}
