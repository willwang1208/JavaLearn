package com.mfp.ali.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
/**
 * 
 * @author 
 *
 */
public class PropertiesLoader {
    
    public static final String DEFAULT_NAMESPACE = "";

    private Map<String, Properties> propsMap = new HashMap<String, Properties>();
    
    public void loadProperties(String filePath, String charset){
        this.loadProperties(DEFAULT_NAMESPACE, filePath, charset);
    }
    
    public void loadProperties(String namespace, String filePath, String charset){
        InputStreamReader in = null;
        try {
            in = new InputStreamReader(new FileInputStream(filePath), charset);
            Properties new_props = new Properties();
            new_props.load(in);
            if(getProperties(namespace) == null){
                propsMap.put(namespace, new_props);
            }else{
                Set<String> keys = new_props.stringPropertyNames();
                for(String key: keys){
                    String new_value = new_props.getProperty(key);
                    String old_value = getProperties(namespace).getProperty(key);
                    if(old_value == null || !old_value.equals(new_value)){
                        getProperties(namespace).setProperty(key, new_value);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    protected Properties getProperties(String namespace){
        return propsMap.get(namespace);
    }
    
    public String getPropertyValue(String namespace, String key){
        if(getProperties(namespace) != null){
            return getProperties(namespace).getProperty(key);
        }
        return null;
    }
    
    public String getPropertyValue(String key){
        return getPropertyValue(DEFAULT_NAMESPACE, key);
    }
    
}
