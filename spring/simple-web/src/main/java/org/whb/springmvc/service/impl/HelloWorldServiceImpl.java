package org.whb.springmvc.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.whb.springmvc.entity.Book;
import org.whb.springmvc.entity.Report;
import org.whb.springmvc.entity.User;
import org.whb.springmvc.service.IHelloWorldService;
import org.whb.springmvc.util.StackTraceUtil;
/**
 * 
 * 
 * 
 * @author 
 */
@Service(value = "helloWorldService")
public class HelloWorldServiceImpl implements IHelloWorldService{
	
	@Autowired
    ApplicationContext context;

//	//加载xml中util:properties配置
//	@Resource(name = "appConfig")
//	Map<String, String> configProperties;
//	
//	//读取xml中context:property-placeholder配置的资源
//	@Value("${service.a.id}")
//	int a_id;
//	
//	//读取xml中util:properties配置的资源
//	@Value("#{appConfig['server.type']}")
//	String server_type;
	
	//自动初始化
	@PostConstruct
	public void postConstruct() {
	    StackTraceUtil.printLocation();
	    //JAVA_OPTS='-Dhttps.proxyHost=10.162.195.60 -Dhttps.proxyPort=3128 -Dhttp.proxyHost=10.162.195.60 -Dhttp.proxyPort=3128 -Dhttp.nonProxyHosts="localhost|127.0.0.1|10.*.*.*"'
	    Environment env = context.getEnvironment();
	    //在运行应用时可以通过-D传入系统参数（System.getProperty()）
	    env.getProperty("http.proxyHost");
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
