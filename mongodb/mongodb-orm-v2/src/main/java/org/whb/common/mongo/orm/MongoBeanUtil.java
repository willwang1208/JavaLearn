package org.whb.common.mongo.orm;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.whb.common.util.ClassUtil;
/**
 * 注册对象映射关系的工具
 * @author whb
 *
 */
public class MongoBeanUtil {
    
    //<className, BeanMongoMap>
    private static Map<String, BeanMongoMap> beanMongoMapCache = new HashMap<String, BeanMongoMap>();
    //<className, Class>
    private static Map<String, Class<?>> classCache = new HashMap<String, Class<?>>();
    //<className, mongoCollectionName>
    private static Map<String, MongoCollection> mongoCollectionNameCache = new HashMap<String, MongoCollection>();
    //<className, Map<fieldName, mongoElementName>>
    private static Map<String, Map<String, MongoElement>> mongoElementNameCache = new HashMap<String, Map<String, MongoElement>>();
    
    public static boolean addMappingClass(Class<?> clazz) {
        try {
            if(beanMongoMapCache.containsKey(clazz.getName()) == false) {
                BeanMongoMap beanMongoMap = BeanMongoMap.create(clazz.newInstance());
                
                beanMongoMapCache.put(clazz.getName(), beanMongoMap);
                
                classCache.put(clazz.getName(), clazz);
                
                MongoCollection mongoCollection = clazz.getAnnotation(MongoCollection.class);
                if(mongoCollection != null){
                    mongoCollectionNameCache.put(clazz.getName(), mongoCollection);
                }
                
                if(mongoElementNameCache.get(clazz.getName()) == null){
                    mongoElementNameCache.put(clazz.getName(), new HashMap<String, MongoElement>());
                }
                List<Field> fields = ClassUtil.getFields(clazz);
                for(Field field: fields){
                    MongoElement mongoElement = field.getAnnotation(MongoElement.class);
                    if(mongoElement != null){
                        mongoElementNameCache.get(clazz.getName()).put(field.getName(), mongoElement);
                    }
                }
                
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return false;
    }
    
    public static BeanMongoMap create(Object bean) {
        BeanMongoMap bmm = beanMongoMapCache.get(bean.getClass().getName()).newInstance(bean);
        return bmm;
    }
    
    public static Object parse(Class<?> clazz, Map<?, ?> map) {
        return parse(clazz.getName(), map);
    }
    
    public static Object parse(String className, Map<?, ?> map) {
        //新建BeanMongoMap实例和className的实例
        BeanMongoMap bmm = beanMongoMapCache.get(className).newInstance(null);
        bmm.setBean(bmm.createBean());
        //赋值
        bmm.putAll(map);
        return bmm.getBean();
    }
    
    public static BeanMongoMap getStaticBeanMongoMap(Class<?> clazz) {
        return getStaticBeanMongoMap(clazz.getName());
    }
    
    public static BeanMongoMap getStaticBeanMongoMap(String className) {
        return beanMongoMapCache.get(className);
    }
    
    public static Class<?> getClass(String className) {
        return classCache.get(className);
    }
    
    public static MongoCollection getMongoCollectionAnnotation(Class<?> clazz) {
        return getMongoCollectionAnnotation(clazz.getName());
    }
    
    public static MongoCollection getMongoCollectionAnnotation(String className) {
        return mongoCollectionNameCache.get(className);
    }
    
    public static Map<String, MongoElement> getMongoElementAnnotationMap(Class<?> clazz) {
        return getMongoElementAnnotationMap(clazz.getName());
    }
    
    public static Map<String, MongoElement> getMongoElementAnnotationMap(String className) {
        return mongoElementNameCache.get(className);
    }
    
    public static MongoElement getMongoElementAnnotation(String className, String fieldName) {
        if(mongoElementNameCache.get(className) == null){
            return null;
        }
        return mongoElementNameCache.get(className).get(fieldName);
    }
}
