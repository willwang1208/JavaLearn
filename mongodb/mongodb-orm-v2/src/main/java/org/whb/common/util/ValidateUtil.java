package org.whb.common.util;

import java.util.Collection;
import java.util.Map;

public class ValidateUtil {
    
    private static final String NULL_STRING = "";
    
    public static boolean isNull(Object value){
        if(value == null){
            return true;
        }else if(value instanceof String && ((String)value).trim().equals(NULL_STRING)){
            return true;
        }else if(value instanceof Object[] && ((Object[])value).length == 0){
            return true;
        }else if(value instanceof Map<?, ?> && ((Map<?, ?>)value).size() == 0){
            return true;
        }else if(value instanceof Collection<?> && ((Collection<?>)value).size() == 0){
            return true;
        }
        return false;
    }
    
    public static boolean isNotNull(Object value){
        return !isNull(value);
    }
    
    public static boolean isOneNullAtLeast(Object... values){
        if(values == null){
            return true;
        } else {
            for(Object value: values){
                if(isNull(value)){
                    return true;
                }
            }
        }
        return false;
    }
    
    public static boolean isAllNull(Object... values){
        if(values == null){
            return true;
        } else {
            for(Object value: values){
                if(isNull(value) == false){
                    return false;
                }
            }
        }
        return true;
    }
    
    public static boolean isAllNotNull(Object... values){
        return !isOneNullAtLeast(values);
    }
    
}
