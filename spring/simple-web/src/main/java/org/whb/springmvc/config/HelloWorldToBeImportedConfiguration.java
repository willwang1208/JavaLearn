package org.whb.springmvc.config;

import java.util.Random;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.whb.springmvc.entity.User;
/**
 * 将通过@Import在HelloWorldConfiguration中引入
 * @author whb
 *
 */
@Configuration
public class HelloWorldToBeImportedConfiguration {

    @Bean(name = "importUser")
    public User createLeader(){
        User leader = new User();
        leader.setName("import user");
        leader.setId(new Random().nextInt(1000));
        return leader;
    }
}
