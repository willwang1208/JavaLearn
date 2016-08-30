package org.whb.spring.boot.web.actuator.health;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health.Builder;
import org.springframework.stereotype.Component;
/**
 * 自定义的健康指标，加入到actuator模块的/health中
 * 参考AbstractHealthIndicator的子类们
 * @author 
 *
 */
@Component
public class HelloWorldHealthIndicator extends AbstractHealthIndicator {

    @Override
    protected void doHealthCheck(Builder builder) throws Exception {
        builder.up().withDetail("version", "{test-health:1}");
    }
}