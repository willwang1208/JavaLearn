package org.whb.common.mongo.orm;

import java.lang.reflect.ParameterizedType;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.cglib.core.CodeEmitter;
import net.sf.cglib.core.Local;
import net.sf.cglib.core.MethodInfo;
import net.sf.cglib.core.ReflectUtils;
import net.sf.cglib.core.TypeUtils;

import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import org.whb.common.util.DailyAttribute;
import org.whb.common.util.NonNegativeCounterMap;
/**
 * Get 方法处理器
 * @author whb
 *
 */
class ElementGetProcessor {
    
    CodeEmitter e;
    
    public ElementGetProcessor(CodeEmitter e) {
        super();
        this.e = e;
    }
    
    public void process(java.lang.reflect.Type fieldType, Local var){
        boolean process = false;
        if(containSpecial(fieldType)){
            if(fieldType instanceof ParameterizedType) {
                java.lang.reflect.Type rawType = ((ParameterizedType) fieldType).getRawType();
                java.lang.reflect.Type[] pts = ((ParameterizedType) fieldType).getActualTypeArguments();
                
                if(rawType instanceof Class){
                    process = processSpecial(rawType, var);
                    if(process == false){
                        if (List.class.isAssignableFrom((Class<?>)rawType)) {
                            process = buildList((Class<?>)rawType, pts[0], var);
                        } else if (Set.class.isAssignableFrom((Class<?>)rawType)) {
                            process = parseToList(pts[0], var);
                        } else if (Map.class.isAssignableFrom((Class<?>)rawType)) {
                            if(pts != null && pts.length == 2){
                                process = buildMap((Class<?>)rawType, pts[1], pts[0], var);
                            }
                            //DailyAttribute由于只有V，K是固定的S，所以这里特殊处理下
                            else if(DailyAttribute.class.isAssignableFrom((Class<?>)rawType)){
                                process = buildMap((Class<?>)rawType, pts[0], String.class, var);
                            }
                        }
                    }
                }
            }else if(fieldType instanceof Class) {
                process = processSpecial(fieldType, var);
            }
        }
    }
    
    public boolean containSpecial(java.lang.reflect.Type type){
        if(type == null){
            return false;
        }else if(type instanceof ParameterizedType) {
            java.lang.reflect.Type rawType = ((ParameterizedType) type).getRawType();
            java.lang.reflect.Type[] pts = ((ParameterizedType) type).getActualTypeArguments();
            
            boolean contain = isSpecial(rawType, pts);
            
            if(pts != null){
                for(java.lang.reflect.Type pt: pts){
                    contain = contain || containSpecial(pt);
                }
            }
            
            return contain;
        }else if(type instanceof Class) {
            return isSpecial(type, null);
        }
        
        return false;
    }
    
    public boolean isSpecial(java.lang.reflect.Type type, java.lang.reflect.Type[] pts){
        if (IMongoBean.class.isAssignableFrom((Class<?>)type)) {
            return true;
        } else if (NonNegativeCounterMap.class.isAssignableFrom((Class<?>)type)) {
            if(pts != null && pts.length == 1
                    && String.class.equals(pts[0]) == false){
                return true;
            }
        } else if (Map.class.isAssignableFrom((Class<?>)type)) {
            //if Map key not String type then return true
            if(pts != null && pts.length == 2
                    && String.class.equals(pts[0]) == false){
                return true;
            }
        } else if (Set.class.isAssignableFrom((Class<?>)type)) {
            return true;
        }
        return false;
    }

    public boolean processSpecial(java.lang.reflect.Type type, Local var){
        if (IMongoBean.class.isAssignableFrom((Class<?>)type)) {
            e.load_local(var);
            e.invoke(getMethodInfo(MongoBeanUtil.class, "create", new Class<?>[] { Object.class }));
            e.store_local(var);
            return true;
        }
        return false;
    }
    
    private boolean buildList(Class<?> clazz, java.lang.reflect.Type elementType, Local var){
        
        String className = "java.util.ArrayList";
        try {
            clazz.newInstance();
            className = clazz.getName();
        } catch (Exception e) {
        }
        
        Local original = e.make_local();
        Local list = e.make_local();
        Local size = e.make_local(Type.INT_TYPE);
        Local loopvar = e.make_local(Type.INT_TYPE);
        Local value = e.make_local();
        
        Label loopbody = e.make_label();
        Label checkloop = e.make_label();
        
        Label skip = e.make_label();
        
        e.load_local(var);
        e.ifnull(skip);
        
        e.load_local(var);
        e.store_local(original);
        e.new_instance(TypeUtils.parseType(className));
        e.dup();
        e.invoke_constructor(TypeUtils.parseType(className));
        e.store_local(list);
        e.load_local(original);
        e.invoke(getMethodInfo(List.class, "size"));
        e.store_local(size);
        e.push(0);
        e.store_local(loopvar);
        
        e.goTo(checkloop);
        
        e.mark(loopbody);
        e.load_local(original);
        e.load_local(loopvar);
        e.invoke(getMethodInfo(List.class, "get", int.class));
        e.store_local(value);
        //处理子元素
        process(elementType, value);
        
        e.load_local(list);
        e.load_local(value);
        e.invoke(getMethodInfo(List.class, "add", Object.class));
        e.pop();
        
        e.iinc(loopvar, 1);
        
        e.mark(checkloop);
        e.load_local(loopvar);
        e.load_local(size);
        e.if_icmp(CodeEmitter.LT, loopbody);
        
        e.load_local(list);
        e.store_local(var);
        
        e.mark(skip);
        
        return true;
    }
    
    private boolean parseToList(java.lang.reflect.Type elementType, Local var){
        
        String className = "java.util.ArrayList";
        
        Local original = e.make_local();
        Local list = e.make_local();
        Local value = e.make_local();
        Local iterator = e.make_local();
        
        Label loopbody = e.make_label();
        Label checkloop = e.make_label();
        Label skip = e.make_label();
        
        e.load_local(var);
        e.ifnull(skip);
        
        e.load_local(var);
        e.store_local(original);
        e.new_instance(TypeUtils.parseType(className));
        e.dup();
        e.invoke_constructor(TypeUtils.parseType(className));
        e.store_local(list);
        
        e.load_local(original);
        e.invoke(getMethodInfo(Set.class, "iterator"));
        e.store_local(iterator);
        
        e.goTo(checkloop);
        
        e.mark(loopbody);
        e.load_local(iterator);
        e.invoke(getMethodInfo(Iterator.class, "next"));
        e.store_local(value);
        process(elementType, value);
        
        e.load_local(list);
        e.load_local(value);
        e.invoke(getMethodInfo(List.class, "add", Object.class));
        e.pop();
        
        e.mark(checkloop);
        e.load_local(iterator);
        e.invoke(getMethodInfo(Iterator.class, "hasNext"));
        e.push(true);
        e.if_cmp(Type.BOOLEAN_TYPE, CodeEmitter.EQ, loopbody);
        
        e.load_local(list);
        e.store_local(var);
        
        e.mark(skip);
        return true;
    }
    
    private boolean buildMap(Class<?> clazz, java.lang.reflect.Type elementType, 
            java.lang.reflect.Type keyType, Local var){
        
        String className = "java.util.HashMap";
        try {
            clazz.newInstance();
            className = clazz.getName();
        } catch (Exception e) {
        }
        
        Local original = e.make_local();
        Local map = e.make_local();
        Local entry = e.make_local();
        Local key = e.make_local();
        Local value = e.make_local();
        Local iterator = e.make_local();
        
        Label loopbody = e.make_label();
        Label checkloop = e.make_label();
        
        Label skip = e.make_label();
        
        e.load_local(var);
        e.ifnull(skip);
        
        e.load_local(var);
        e.store_local(original);
        e.new_instance(TypeUtils.parseType(className));
        e.dup();
        e.invoke_constructor(TypeUtils.parseType(className));
        e.store_local(map);
        
        e.load_local(original);
        e.invoke(getMethodInfo(Map.class, "entrySet"));
        e.invoke(getMethodInfo(Set.class, "iterator"));
        e.store_local(iterator);
        
        e.goTo(checkloop);
        
        e.mark(loopbody);
        e.load_local(iterator);
        e.invoke(getMethodInfo(Iterator.class, "next"));
        e.checkcast(TypeUtils.parseType("java.util.Map$Entry"));
        e.store_local(entry);
        
        //处理key
        e.load_local(entry);
        e.invoke(getMethodInfo(Map.Entry.class, "getKey"));
        if("java.lang.Integer".equals(((Class<?>)keyType).getName())){
            e.checkcast(TypeUtils.parseType("Integer"));
            e.invoke(getMethodInfo(Integer.class, "intValue"));
            e.invoke_static(TypeUtils.parseType("String"), TypeUtils.parseSignature("String valueOf(int)"));
        }else if("java.lang.Double".equals(((Class<?>)keyType).getName())){
            e.checkcast(TypeUtils.parseType("Double"));
            e.invoke(getMethodInfo(Double.class, "doubleValue"));
            e.invoke_static(TypeUtils.parseType("String"), TypeUtils.parseSignature("String valueOf(double)"));
        }else if("java.lang.Long".equals(((Class<?>)keyType).getName())){
            e.checkcast(TypeUtils.parseType("Long"));
            e.invoke(getMethodInfo(Long.class, "longValue"));
            e.invoke_static(TypeUtils.parseType("String"), TypeUtils.parseSignature("String valueOf(long)"));
        }else if("java.lang.Boolean".equals(((Class<?>)keyType).getName())){
            e.checkcast(TypeUtils.parseType("Boolean"));
            e.invoke(getMethodInfo(Boolean.class, "booleanValue"));
            e.invoke_static(TypeUtils.parseType("String"), TypeUtils.parseSignature("String valueOf(boolean)"));
        }
        e.store_local(key);
        
        //处理value
        e.load_local(entry);
        e.invoke(getMethodInfo(Map.Entry.class, "getValue"));
        e.store_local(value);
        process(elementType, value);
        
        e.load_local(map);
        e.load_local(key);
        e.load_local(value);
        e.invoke(getMethodInfo(Map.class, "put", new Class<?>[]{ Object.class, Object.class }));
        e.pop();
        
        e.mark(checkloop);
        e.load_local(iterator);
        e.invoke(getMethodInfo(Iterator.class, "hasNext"));
        e.push(true);
        e.if_cmp(Type.BOOLEAN_TYPE, CodeEmitter.EQ, loopbody);
        
        e.load_local(map);
        e.store_local(var);
        
        e.mark(skip);
        
        return true;
    }
    
    private MethodInfo getMethodInfo(Class<?> clazz, String methodName, Class<?>... params){
        try {
            return ReflectUtils.getMethodInfo(clazz.getDeclaredMethod(methodName, params));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (SecurityException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
}
