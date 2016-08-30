package org.whb.spring.boot.web.properties;

import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
/**
 * 通过注解 @ConfigurationProperties 把一组属性注入到java bean对象中
 * 支持嵌套属性注入，例如 SubTask
 * 支持使用JSR-303注解进行验证，例如 @NotNull
 * @author 
 *
 */
@Component
@ConfigurationProperties(prefix = "customize.task")
public class TaskProperties {

    @NotNull
    private String name;
    
    private int count;
    
    private String firstTag;
    
    private String secondTag;
    
    private String thirdTag;

    private SubTask subTask;
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getFirstTag() {
        return firstTag;
    }

    public void setFirstTag(String firstTag) {
        this.firstTag = firstTag;
    }

    public String getSecondTag() {
        return secondTag;
    }

    public void setSecondTag(String secondTag) {
        this.secondTag = secondTag;
    }

    public String getThirdTag() {
        return thirdTag;
    }

    public void setThirdTag(String thirdTag) {
        this.thirdTag = thirdTag;
    }

    public SubTask getSubTask() {
        return subTask;
    }

    public void setSubTask(SubTask subTask) {
        this.subTask = subTask;
    }
    
}
