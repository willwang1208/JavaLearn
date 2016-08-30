package org.whb.springmvc.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.whb.springmvc.entity.Book;
import org.whb.springmvc.entity.Report;
import org.whb.springmvc.entity.User;
import org.whb.springmvc.service.IHelloWorldService;
import org.whb.springmvc.util.StackTraceUtil;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
/**
 * 演示属性注入和运行参数获取
 * 
 * @author 
 */
@Service(value = "helloWorldService")
public class HelloWorldServiceImpl implements IHelloWorldService{
	
	@Autowired
    ApplicationContext context;
	
	@Autowired
	Environment environment;

	@Resource(name = "dbProperties")
    private Properties properties;
	
	//读取environment
	@Value("${server.id}")
	int server_id;
	
	//读取dbProperties
	@Value("#{dbProperties['server.name']}")
	String server_name;
	
	//自动初始化
	@PostConstruct
	public void postConstruct() {
	    StackTraceUtil.printLocation();
	    
	    ObjectMapper mapper = new ObjectMapper();
        try {
            //JAVA_OPTS='-Dhttps.proxyHost=10.162.195.60 -Dhttps.proxyPort=3128 -Dhttp.proxyHost=10.162.195.60 -Dhttp.proxyPort=3128 -Dhttp.nonProxyHosts="localhost|127.0.0.1|10.*.*.*"'
            //在运行应用时可以通过-D传入系统参数（System.getProperty()）
            
            System.out.println("----------------InetAddress.getLocalHost()----------------");
            System.out.println(mapper.writeValueAsString(InetAddress.getLocalHost()));
            
            System.out.println("----------------System.getenv()----------------");
            System.out.println(mapper.writeValueAsString(System.getenv()));
            
            System.out.println("----------------System.getProperties()----------------");
            System.out.println(mapper.writeValueAsString(System.getProperties()));
            
            Environment env = context.getEnvironment();
            env.getProperty("http.proxyHost");
            
            System.out.println("----------------MessageSource----------------");
            System.out.println(context.getMessage("name", null, null));
            System.out.println(context.getMessage("name", null, Locale.SIMPLIFIED_CHINESE));
            System.out.println(context.getMessage("name", null, new Locale("en", "US")));
            
            System.out.println("----------------------------------------------");
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	@PreDestroy
	public void preDestroy() {
	    StackTraceUtil.printLocation();
	}

	public List<Book> getBooks(){
	    List<Book> bookList = new ArrayList<>();
	    bookList.add(new Book(1, "西游记", "吴承恩", 999.99, true, "经典名著"));
	    bookList.add(new Book(2, "水浒传", "施耐庵", 100, true, null));
	    bookList.add(new Book(3, "三国演义", "罗贯中", 88, true, "历史"));
	    return bookList;
	}
	
	public User getUser(){
	    User user = new User();
        user.setFriends(new String[]{"peter", "tom", "lily", "中国人"});
        user.setHeight(188.3);
        user.setId(20);
        user.setName("wang");
        return user;
    }
	
	public Report getReport(){
	    Report report = new Report();
        report.setDate(new Date());
        report.setId(1);
        report.setType(100);
        report.setSales(BigDecimal.TEN);
        report.setName("gun");
        return report;
    }
	
	public Map<String, String> getMapData(){
	    Map<String, String> map = new HashMap<String, String>();
	    map.put("Jan-2010", "$100,000,000");
	    map.put("Feb-2010", "$110,000,000");
	    map.put("Mar-2010", "$130,000,000");
	    map.put("Apr-2010", "$140,000,000");
	    map.put("May-2010", "$200,000,000");
	    return map;
	}
}
