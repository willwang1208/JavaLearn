package org.whb.springmvc.controller;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.WebAsyncTask;
import org.springframework.web.servlet.ModelAndView;

/**
 * 支持异步的Controller
 * 
 * @author whb
 *
 */
@Controller
@RequestMapping("/async")
public class AsyncController {
    
    /**
     * Spring MVC将在TaskExecutor的帮助下在一个单独的线程中调用该回调方法。
     * @param model
     * @return java.util.concurrent.Callable
     */
    @RequestMapping("/callable/1")
    public Callable<String> callable1(final Model model) {
        return new Callable<String>() {
            @Override
            public String call() throws Exception {
                Thread.sleep(2L * 1000);
                model.addAttribute("name", "hello callable 1");
                return "hellojsp";
            }
        };
    }

    @RequestMapping("/callable/2")
    public Callable<ModelAndView> callable2() {
        return new Callable<ModelAndView>() {
            @Override
            public ModelAndView call() throws Exception {
                Thread.sleep(2L * 1000);
                ModelAndView mv = new ModelAndView("hellojsp");
                mv.addObject("name", "hello callable 2");
                return mv;
            }
        };
    }
    
    @RequestMapping("/callable/e")
    public Callable<String> exception() {
        return new Callable<String>() {
            @Override
            public String call() throws Exception {
                Thread.sleep(2L * 1000);
                throw new RuntimeException("exception occur");
            }
        };
    }
    
    /**
     * Callable的封装
     * @param model
     * @return WebAsyncTask
     */
    @RequestMapping("/task")
    public WebAsyncTask<String> task(final Model model) {
        long timeout = 20L * 1000;
        WebAsyncTask<String> webAsyncTask = new WebAsyncTask<>(timeout, new Callable<String>() {
            @Override
            public String call() throws Exception {
                Thread.sleep(2L * 1000);
                model.addAttribute("name", "hello WebAsyncTask");
                return "hellojsp";
            }
        });
        return webAsyncTask;
    }
    
    /**
     * 和Callable类似，延迟发送结果，但可以使用其他线程协作来完成
     * 
     * @return
     */
    @RequestMapping("/deferred")
    @ResponseBody
    public DeferredResult<String> deferred() {
        final DeferredResult<String> result = new DeferredResult<String>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2 * 1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                result.setResult("deferred success");
            }
        }).start();
        return result;
    }
    
    private Queue<DeferredResult<String>> queue = new ConcurrentLinkedQueue<>();
    
    /**
     * 收到请求后开启一个DeferredResult，超时时间设置为60秒
     * 等待超时或直到其他线程调用这个DeferredResult的setResult方法才响应当前这个请求
     * @return
     */
    @RequestMapping("/deferred/queue")
    @ResponseBody
    public DeferredResult<String> queue() {
        long timeout = 60L * 1000;
        final DeferredResult<String> result = new DeferredResult<String>(timeout);
        result.onCompletion(new Runnable() {
            @Override
            public void run() {
                queue.remove(result);
            }
        });
        result.onTimeout(new Runnable() {
            @Override
            public void run() {
                queue.remove(result);
            }
        });
        queue.add(result);
        return result;
    }
    @RequestMapping("/deferred/queue/pushmsg")
    @ResponseBody
    public void pushMessage() {
        Iterator<DeferredResult<String>> iter = queue.iterator();
        while (iter.hasNext()) {
            DeferredResult<String> result = iter.next();
            result.setResult("push new message to all members in queue: " + System.currentTimeMillis());
        }
    }
	
    /**
     * 当这个Controller中的其他方法抛出异常，将会被这个@ExceptionHandler方法拦截，处理异常后再返回
     * @param e
     * @return
     */
    @ExceptionHandler
    @ResponseBody
    public String exceptionHandler(Exception e) {
        return "exception: " + e.getMessage();
    }
}
