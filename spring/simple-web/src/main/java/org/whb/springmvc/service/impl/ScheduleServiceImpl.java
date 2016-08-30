package org.whb.springmvc.service.impl;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
/**
 * 通过注解 @Scheduled 配置定时任务，需要@EnableScheduling
 * 
 *    字段                         允许值                      允许的特殊字符
 *    秒                              0-59           , - * /
 *    分                              0-59           , - * /
 *    小时                          0-23           , - * /
 *    日期                          1-31           , - * ? / L W C
 *    月份                1-12 或者 JAN-DEC    , - * /
 *    星期                1-7 或者 SUN-SAT     , - * ? / L C #
 *    年（可选）        留空, 1970-2099    , - * / 
 *    
 * 注：
 *    - 区间  
 *    * 通配符  
 *    ? 你不想设置那个字段
 * 
 * 例子：
 *        0 0 12 * * ?              每天中午十二点触发 
 *        0 15 10 ? * *             每天早上10:15触发 
 *        0 15 10 * * ?             每天早上10:15触发 
 *        0 15 10 * * ? *           每天早上10:15触发 
 *        0 15 10 * * ? 2015        2015年的每天早上10:15触发 
 *        0 * 14 * * ?              每天从下午2点开始到2点59分每分钟一次触发 
 *        0 0/5 14 * * ?            每天从下午2点开始到2：55分结束每5分钟一次触发 
 *        0 0/5 14,18 * * ?         每天的下午2点至2：55和6点至6点55分两个时间段内每5分钟一次触发 
 *        0 0-5 14 * * ?            每天14:00至14:05每分钟一次触发 
 *        0 10,44 14 ? 3 WED        三月的每周三的14:10和14:44触发 
 *        0 15 10 ? * MON-FRI       每个周一、周二、周三、周四、周五的10:15触发 
 * 
 * 
 * @author 
 */
@Service
public class ScheduleServiceImpl{
	
	@PostConstruct
	public void postConstruct() {

	}
	
	@PreDestroy
	public void preDestroy() {
	    
	}

	@Scheduled(cron="0/60 * * * * ? ")
	public void timeTask(){
		System.out.println("每60秒执行一次的任务......");
	}
}
