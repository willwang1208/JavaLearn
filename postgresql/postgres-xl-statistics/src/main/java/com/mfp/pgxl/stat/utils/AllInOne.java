package com.mfp.pgxl.stat.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AllInOne {
    
    private static final Logger logger = LoggerFactory.getLogger(AllInOne.class);
    
    private static final long K = 1024;
    private static final long M = 1048576;
    private static final long G = 1073741824;
    
    private static final String yMd_Hms = "yyyy-MM-dd HH:mm:ss";
    
    private static final String NULL_STRING = "";
    
    private static SimpleDateFormat formatter = new SimpleDateFormat(yMd_Hms);
    
    private static String currentDatetime;
    
    private static int taskId;

    public static List<String> getSqlFromInputStream(InputStream is){
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            
            StringBuffer buf = new StringBuffer();
            String line = null;
            while((line = br.readLine()) != null){
                buf.append(line.replaceAll("--.*", ""));
            }
            
            List<String> rs = new ArrayList<>();
            String[] sqlArr = buf.toString().split(";");
            for (int i = 0; i < sqlArr.length; i++) {
                String sql = sqlArr[i].trim();
                if (!sql.equals("")) {
                    rs.add(sql);
                }
            }
            return rs;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(br != null){
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    
    public static String getCurrentDatetime(){
        if(currentDatetime == null){
            logger.warn("Should compute current date time on scheduled task");
            return new SimpleDateFormat(yMd_Hms).format(new Date());
        }
        return currentDatetime;
    }
    
    public static void computeCurrentDatetime(){
        currentDatetime = formatter.format(new Date());
    }
    
    @SuppressWarnings("unchecked")
    public static <T extends Number> T parseNumberIfErrorThenDefault(Class<T> clazz, String source, T defaultValue){
        try {
            if(clazz.equals(Integer.class)){
                return (T) Integer.valueOf(Integer.parseInt(source));
            }else if(clazz.equals(Long.class)){
                return (T) Long.valueOf(Long.parseLong(source));
            }else if(clazz.equals(Double.class)){
                return (T) Double.valueOf(Double.parseDouble(source));
            }else if(clazz.equals(Float.class)){
                return (T) Float.valueOf(Float.parseFloat(source));
            }else{
                throw new RuntimeException("Not support type: " + clazz);
            }
        } catch (Exception e) {
            logger.warn("Parse number error: " + source + ". Use default value: " + defaultValue);
            return defaultValue;
        }
    }
    
    public static <T> T getValueIfNullThenDefault(T source, T defaultValue){
        if(source == null){
            return defaultValue;
        }
        return source;
    }
    
    public static int getNextTaskId(){
        return ++taskId;
    }
    
    public static String humanReadableByteSize(long byteSize){
        if(byteSize > G){
            return round(byteSize*1.0/G, 2) + "GB";
        }else if(byteSize > M){
            return round(byteSize*1.0/M, 2) + "MB";
        }else if(byteSize > K){
            return round(byteSize*1.0/K, 2) + "KB";
        }else{
            return byteSize + "B";
        }
    }
    
    public static double round(double value, int scale) {
        return new BigDecimal(value).setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
    
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
