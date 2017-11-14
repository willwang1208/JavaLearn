package com.mfp.pgxl.stat.schedule;

import java.util.Date;

public abstract class AbstractScheduledTask implements Runnable {

    protected int id;             // 任务ID

    protected boolean started;    // 任务是否开启，0否1是

    protected boolean unique;     // 任务是否唯一执行，0否1是

    protected Date beginTime;     // 任务开始日期时间，格式yyyy-MM-dd HH:mm:ss

    protected int period;         // 任务周期，单位秒，-1表示只执行一次，无周期

    public AbstractScheduledTask() {
        super();
    }
    
    public AbstractScheduledTask(int id, boolean started, boolean unique, Date beginTime, int period) {
		super();
		this.id = id;
		this.started = started;
		this.unique = unique;
		this.beginTime = beginTime;
		this.period = period;
	}

	@Override
    public void run() {
        if (isUnique()) {
            // 加锁执行任务
            if (lock()) {
                service();
            }
        } else {
            // 所有服务都执行的任务
            service();
        }
    }

    public abstract void service();

    public boolean lock() {
        throw new RuntimeException("not implement.");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

}
