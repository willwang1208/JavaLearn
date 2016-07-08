package org.whb.springmvc.context;

import java.io.IOException;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.web.context.support.AbstractRefreshableWebApplicationContext;
import org.whb.springmvc.util.StackTraceUtil;

/**
 * 似乎用处不大
 * @author 
 *
 */
public class HelloWorldWebApplicationContext extends AbstractRefreshableWebApplicationContext {

    @Override
    protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws BeansException, IOException {
        StackTraceUtil.getLocation();
    }
}
