package org.whb.common.mongo.orm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 定义java bean与Mongodb collection之间的关系
 * @author whb
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MongoCollection {
    
    String dbk() default "";
    
    String name();
    
    boolean needShard() default false;
    
    String note() default "";
}
