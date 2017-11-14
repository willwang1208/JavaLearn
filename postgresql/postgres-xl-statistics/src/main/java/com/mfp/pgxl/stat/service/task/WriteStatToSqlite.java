package com.mfp.pgxl.stat.service.task;

import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;

public class WriteStatToSqlite implements Runnable {

	private final static Logger logger = LoggerFactory.getLogger(WriteStatToSqlite.class);
    
	private JdbcTemplate sqlite;
	
	private LinkedBlockingQueue<Object> queue;
	
	public WriteStatToSqlite(JdbcTemplate sqlite, LinkedBlockingQueue<Object> queue) {
		super();
		this.sqlite = sqlite;
		this.queue = queue;
	}

	@Override
	public void run() {
		while (true) {
            Object obj = null;
            try {
            	obj = queue.take();   //阻塞
            	
            	if(obj instanceof String){
            		sqlite.execute((String)obj);
            	}else if(obj instanceof PreparedStatementCreator){
            		sqlite.update((PreparedStatementCreator)obj);
            	}else{
            		logger.warn("Can not execute: " + obj);
            	}
            } catch (Exception e) {
//            	if(queue.size() < 1000){
//        			queue.add(obj);
//        		}
//                logger.error(e.getMessage());
            	e.printStackTrace();
                
            }
        }
	}

}
