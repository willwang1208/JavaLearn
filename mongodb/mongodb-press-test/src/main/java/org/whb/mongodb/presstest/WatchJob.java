package org.whb.mongodb.presstest;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 每隔一定时间输出一次被监视的jobs的执行情况，结果为jobs的合计值。
 * jobs需要属于相同的tag才有意义。
 * 
 * 输出项：[tag] [时间戳] [时间间隔内的执行次数] [总执行次数] [时间间隔内的异常次数] [总异常次数] [每秒执行次数] [每秒无异常执行次数] [方法平均执行时间ms]
 * 
 * @author whb
 *
 */
public class WatchJob implements Runnable {
    
    long lastLoopCount;
    
    long lastExceptionCount;

    List<PressJob> jobs;
    
    int interval;   //时间间隔，单位秒
    
    String tag;
    
    public WatchJob(String tag, List<PressJob> jobs, int interval) {
        super();
        this.tag = tag;
        this.jobs = jobs;
        this.interval = interval;
    }

    @Override
    public void run() {
        if(interval > 0){
            while(true){
                try {
                    Thread.sleep(1000 * interval);
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
                println();
            }
        }
    }
    
    public void println(){
        long curTime = System.currentTimeMillis();
        long exceptionCount = 0;
        long loopCount = 0;
        long useTime = 0;
        for(PressJob job: jobs){
            exceptionCount += job.getExceptionCount();
            loopCount += job.getLoopCount();
            useTime += (curTime - job.getBeginTime());
        }
        
        System.out.println(tag + " " + LocalDateTime.now()
                + " " + (loopCount - lastLoopCount)
                + " " + (loopCount)
                + " " + (exceptionCount - lastExceptionCount)
                + " " + (exceptionCount)
                + " " + ((loopCount - lastLoopCount) / interval)
                + " " + (((loopCount - lastLoopCount) - (exceptionCount - lastExceptionCount)) / interval)
                + " " + (loopCount == 0 ? "NaN" : (useTime / loopCount)));
        
        lastLoopCount = loopCount;
        lastExceptionCount = exceptionCount;
    }
}
