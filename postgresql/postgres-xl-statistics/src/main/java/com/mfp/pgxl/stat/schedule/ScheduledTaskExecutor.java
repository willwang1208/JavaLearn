package com.mfp.pgxl.stat.schedule;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
/**
 * 
 * @author 
 *
 */
public class ScheduledTaskExecutor{

	private Map<Integer, ScheduledFuture<?>> tasks = new HashMap<Integer, ScheduledFuture<?>>();
	
	private static final int MIN_PERIOD = 1; //秒
	
	private ScheduledExecutorService timerExecutor;
    
	public ScheduledTaskExecutor(int corePoolSize) {
		super();
		
		final ThreadFactory backingThreadFactory = Executors.defaultThreadFactory();
		
		ThreadFactory factory = new ThreadFactory() {
			@Override
			public Thread newThread(Runnable runnable) {
				Thread thread = backingThreadFactory.newThread(runnable);
				thread.setDaemon(true);
				return thread;
			}
		};
		
		timerExecutor = new ScheduledThreadPoolExecutor(
				corePoolSize, 
				factory, 
				new ThreadPoolExecutor.CallerRunsPolicy());
	}
	
	/**
	 * 销毁前结束任务
	 */
	public void preDestroy() {
		if(tasks != null){
			for(ScheduledFuture<?> task: tasks.values()){
				task.cancel(false);
			}
		}
		if(timerExecutor != null && timerExecutor.isShutdown() == false){
			timerExecutor.shutdown();
		}
	}
	
	public void registerTask(AbstractScheduledTask task){
		registerTask(task, true);
	}
	
	/**
	 * 注册任务
	 * @param task
	 * @param override
	 */
	public void registerTask(AbstractScheduledTask task, boolean override){
		//已注册任务且不覆盖
		if(tasks.containsKey(task.getId()) && override == false){
			return;
		}
		
		//取消旧任务
		removeTask(task.getId());
		
		//任务未开启
		if(task.isStarted() == false){
			return;
		}
		
		Date beginTime = task.getBeginTime();
		
		ScheduledFuture<?> future = null;
        //小于最小运行周期的任务作为只执行一次的任务，过期的不加入日程表
        if(task.getPeriod() < MIN_PERIOD && beginTime.after(new Date())){
        	future = timerExecutor.schedule(task, (beginTime.getTime() - new Date().getTime()), TimeUnit.MILLISECONDS);
        }
        //周期任务
        else if(task.getPeriod() >= MIN_PERIOD){
        	//第一次执行时间小于当前时间的任务，延长至最近的下一次运行时间
        	Calendar current = Calendar.getInstance();
    		Calendar next = Calendar.getInstance();
    		next.setTime(beginTime);
    		
    		long dis = next.getTimeInMillis() - current.getTimeInMillis();
    		
    		if(dis < 0){
    			int count = (int) (dis / (task.getPeriod() * 1000));
    			count = -count + 1;
    			next.add(Calendar.SECOND, task.getPeriod() * count);
    		}
        	
    		// 以固定频率执行，区别于scheduleWithFixedDelay（以相对固定频率执行），两者不管任务执行耗时是否大于间隔时间，都不会导致同一任务被并发执行
    		future = timerExecutor.scheduleAtFixedRate(task, (next.getTime().getTime() - new Date().getTime()), 
        			task.getPeriod() * 1000, TimeUnit.MILLISECONDS);
        }
        
        //保存运行任务句柄
        if(future != null){
        	tasks.put(task.getId(), future);
        }
	}
	
	public void removeTask(int taskId){
		ScheduledFuture<?> task = tasks.get(taskId);
		if(task != null){
			task.cancel(false);    //false允许进行中的任务执行完
			tasks.remove(taskId);
		}
	}
	
}
