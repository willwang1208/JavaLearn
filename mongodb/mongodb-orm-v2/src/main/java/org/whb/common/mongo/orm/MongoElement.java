package org.whb.common.mongo.orm;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/**
 * 定义java bean属性与Mongodb json字段之间的关系
 * @author whb
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface MongoElement {
    
    String name();
    
    IndexType indexType() default IndexType.None;
    
    String note() default "";
    
    public static enum IndexType {
        
        //0非索引，1正向，-1逆向
        None(0),
        Positive(1),
        Negative(-1);
        
        private int value;
        
        private IndexType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
