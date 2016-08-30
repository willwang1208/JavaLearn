package org.whb.spring.boot.web.runner;

import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
/**
 * Spring Boot应用程序在启动后，会遍历CommandLineRunner接口的实例并运行它们的run方法。
 * 
 * @author
 *
 */
@Component
@Order(value = 10)
public class HelloWorldCommandLineRunner implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        System.out.println(">> HelloWorldCommandLineRunner");
        System.out.println(">> args: " + Arrays.toString(args));
    }

}
