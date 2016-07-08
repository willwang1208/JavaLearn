package org.whb.springmvc.service.impl;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.whb.springmvc.service.IHelloWorldService;
import org.whb.springmvc.util.StackTraceUtil;
/**
 * 
 * 
 * 
 * @author 
 */
@Service
//@DemoSpringAnnotation(name="demoService")
public class ScheduleServiceImpl{
	
	//自动装载ApplicationContext
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
		//依据spring管理的注解找到所有bean
//		try {
//			Map<String, Object> map = context.getBeansWithAnnotation(DemoSpringAnnotation.class);
//			
//			for(Map.Entry<String, Object> entry: map.entrySet()){
//				Object bean = entry.getValue();
//				String name = bean.getClass().getAnnotation(DemoSpringAnnotation.class).name();
//				
//				System.out.println(bean.getClass() + "\t" + StackTraceUtil.getLocation());
//				System.out.println(name + "\t" + StackTraceUtil.getLocation());
//			}
//		} catch (Throwable e) {
//			System.out.println("ERROR: DemoServiceImpl init. " + e.getMessage() + "\t" + StackTraceUtil.getLocation());
//			e.printStackTrace();
//		}
//		
//		System.out.println("a_id: " + a_id + "\t" + StackTraceUtil.getLocation());
//		System.out.println("server_type: " + server_type + "\t" + StackTraceUtil.getLocation());
//		System.out.println("configProperties.get(\"server.type\"): " + configProperties.get("server.type") + "\t" + StackTraceUtil.getLocation());
	}
	
	@PreDestroy
	public void preDestroy() {
	    
	    
	}

	/**
	 * CRON表达式    含义 
	 * "0 0 12 * * ?"    每天中午十二点触发 
	 * "0 15 10 ? * *"    每天早上10：15触发 
	 * "0 15 10 * * ?"    每天早上10：15触发 
	 * "0 15 10 * * ? *"    每天早上10：15触发 
	 * "0 15 10 * * ? 2005"    2005年的每天早上10：15触发 
	 * "0 * 14 * * ?"    每天从下午2点开始到2点59分每分钟一次触发 
	 * "0 0/5 14 * * ?"    每天从下午2点开始到2：55分结束每5分钟一次触发 
	 * "0 0/5 14,18 * * ?"    每天的下午2点至2：55和6点至6点55分两个时间段内每5分钟一次触发 
	 * "0 0-5 14 * * ?"    每天14:00至14:05每分钟一次触发 
	 * "0 10,44 14 ? 3 WED"    三月的每周三的14：10和14：44触发 
	 * "0 15 10 ? * MON-FRI"    每个周一、周二、周三、周四、周五的10：15触发
	 */
//	@Scheduled(cron="0/60 * *  * * ? ")
//	public void timeTask(){
//		StackTraceUtil.printLocation();
//	}
}
