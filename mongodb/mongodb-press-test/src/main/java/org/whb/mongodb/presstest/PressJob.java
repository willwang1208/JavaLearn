package org.whb.mongodb.presstest;

/**
 * 压力任务，执行方法在MongodbOperatorAdapter中实现。
 * 
 * @author whb
 *
 */
public class PressJob implements Runnable {
    
    long beginTime;
    
    long loopCount;
    
    long exceptionCount;

    MongodbOperatorAdapter adapter;
    
    public PressJob(MongodbOperatorAdapter adapter) {
        super();
        this.adapter = adapter;
    }

    @Override
    public void run() {
        beginTime = System.currentTimeMillis();
        while(true){
            try {
                adapter.execute();
            } catch (Exception e) {
                exceptionCount ++;
//                e.printStackTrace();
            }
            loopCount ++;
        }
    }
    
    public long getBeginTime() {
        return beginTime;
    }

    public long getLoopCount() {
        return loopCount;
    }

    public long getExceptionCount() {
        return exceptionCount;
    }

    @FunctionalInterface
    public interface MongodbOperatorAdapter {
        public void execute();
    }

}
