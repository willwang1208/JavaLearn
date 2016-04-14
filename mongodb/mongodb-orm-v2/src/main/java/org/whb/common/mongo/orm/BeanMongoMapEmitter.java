package org.whb.common.mongo.orm;

/*
 * Copyright 2003,2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import net.sf.cglib.core.ClassEmitter;
import net.sf.cglib.core.CodeEmitter;
import net.sf.cglib.core.Constants;
import net.sf.cglib.core.EmitUtils;
import net.sf.cglib.core.Local;
import net.sf.cglib.core.MethodInfo;
import net.sf.cglib.core.ObjectSwitchCallback;
import net.sf.cglib.core.ReflectUtils;
import net.sf.cglib.core.Signature;
import net.sf.cglib.core.TypeUtils;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;


/**
 * 基于net.sf.cglib.beans.BeanMapEmitter源码修改，使其支持key的别名
 * 
 * 建议用list代替set和array
 * 建议map的key优先用string类型
 * 
 * @author
 *
 */
class BeanMongoMapEmitter extends ClassEmitter {
    private static final Type BEAN_MAP = TypeUtils.parseType("org.whb.common.mongo.orm.BeanMongoMap");
    private static final Type FIXED_KEY_SET = TypeUtils.parseType("net.sf.cglib.beans.FixedKeySet");
    private static final Signature CSTRUCT_OBJECT = TypeUtils.parseConstructor("Object");
    private static final Signature CSTRUCT_STRING_ARRAY = TypeUtils.parseConstructor("String[]");
    private static final Signature BEAN_MAP_GET = TypeUtils.parseSignature("Object get(Object, Object)");
    private static final Signature BEAN_MAP_PUT = TypeUtils.parseSignature("Object put(Object, Object, Object)");
    private static final Signature BEAN_CREATE = TypeUtils.parseSignature("Object createBean()");
    private static final Signature KEY_SET = TypeUtils.parseSignature("java.util.Set keySet()");
    private static final Signature NEW_INSTANCE = new Signature("newInstance", BEAN_MAP, new Type[] { Constants.TYPE_OBJECT });
    private static final Signature GET_PROPERTY_TYPE = TypeUtils.parseSignature("Class getPropertyType(String)");
    
    public BeanMongoMapEmitter(ClassVisitor v, String className, Class type, int require) {
        super(v);
        Map<String, String> mfMap = new LinkedHashMap<String, String>();
        Map<String, String> fmMap = new LinkedHashMap<String, String>();
        begin_class(Constants.V1_2, Constants.ACC_PUBLIC, className, BEAN_MAP, null, Constants.SOURCE_FILE);
        EmitUtils.null_constructor(this);
        EmitUtils.factory_method(this, NEW_INSTANCE);
        generateConstructor();
        factory_type_method(this, BEAN_CREATE, type);
        Map getters = makePropertyMap(type, ReflectUtils.getBeanGetters(type));
        Map setters = makePropertyMap(type, ReflectUtils.getBeanSetters(type));
        Map allProps = new HashMap();
        allProps.putAll(getters);
        allProps.putAll(setters);
        for (Iterator it = allProps.keySet().iterator(); it.hasNext();) {
            String name = (String) it.next();
            addField(type, name, mfMap, fmMap);
        }
        if (require != 0) {
            for (Iterator it = allProps.keySet().iterator(); it.hasNext();) {
                String name = (String) it.next();
                if ((((require & BeanMongoMap.REQUIRE_GETTER) != 0) && !getters.containsKey(name)) || (((require & BeanMongoMap.REQUIRE_SETTER) != 0) && !setters.containsKey(name))) {
                    it.remove();
                    getters.remove(name);
                    setters.remove(name);
                }
            }
        }
        generateGet(type, getters, mfMap, fmMap);
        generatePut(type, setters, mfMap, fmMap);
        String[] allNames = getNames(allProps, fmMap);
        generateKeySet(allNames);
        generateGetPropertyType(allProps, allNames, mfMap);
        end_class();
    }
    
    private static Field getField(Class<?> type, String name) {
        try {
            Field f = type.getDeclaredField(name);
            return f;
        } catch (Exception e) {
            Class<?> superClass = type.getSuperclass();
            if (superClass.equals(Object.class)) throw new RuntimeException(e);
            else {
                return getField(superClass, name);
            }
        }
    }
    
    private static void addField(Class<?> type, String name, final Map<String, String> mfMap, final Map<String, String> fmMap) {
        try {
            Field f = type.getDeclaredField(name);
            MongoElement me = f.getAnnotation(MongoElement.class);
            String mongoName = name;
            if (me != null && me.name() != null) {
                mongoName = me.name();
            }
            mfMap.put(mongoName, name);
            fmMap.put(name, mongoName);
        } catch (Exception e) {
            Class<?> superClass = type.getSuperclass();
            if (superClass.equals(Object.class)) throw new RuntimeException(e);
            else {
                addField(superClass, name, mfMap, fmMap);
            }
        }
    }
    
    public static void factory_type_method(ClassEmitter ce, Signature sig, Class clazz) {
        CodeEmitter e = ce.begin_method(Constants.ACC_PUBLIC, sig, null);
        Type type = TypeUtils.getType(clazz.getName());
        e.new_instance(type);
        e.dup();
        e.invoke_constructor(type);
        e.return_value();
        e.end_method();
    }
    
    private Map makePropertyMap(Class type, PropertyDescriptor[] props) {
        Map names = new HashMap();
        for (int i = 0; i < props.length; i++) {
            String name = ((PropertyDescriptor) props[i]).getName();
            try {
                Field f = type.getDeclaredField(name);
                MongoElement me = f.getAnnotation(MongoElement.class);
                if (me.name().equals("transient")) {
                    continue;
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            //end
            names.put(name, props[i]);
        }
        return names;
    }
    
    private String[] getNames(Map propertyMap, Map<String, String> fmMap) {
        String[] names = new String[propertyMap.size()];
        int i = 0;
        for (Object n : propertyMap.keySet()) {
            names[i] = fmMap.get((String) n);
            i = i + 1;
        }
        return names;
    }
    
    private void generateConstructor() {
        CodeEmitter e = begin_method(Constants.ACC_PUBLIC, CSTRUCT_OBJECT, null);
        e.load_this();
        e.load_arg(0);
        e.super_invoke_constructor(CSTRUCT_OBJECT);
        e.return_value();
        e.end_method();
    }

    private void generateGet(final Class type, final Map getters, final Map<String, String> mfMap, final Map<String, String> fmMap) {
        final CodeEmitter e = begin_method(Constants.ACC_PUBLIC, BEAN_MAP_GET, null);
        e.load_arg(0);
        e.checkcast(Type.getType(type));
        e.load_arg(1);
        e.checkcast(Constants.TYPE_STRING);
        
        final ElementGetProcessor elementGetProcessor = new ElementGetProcessor(e);
        
        EmitUtils.string_switch(e, getNames(getters, fmMap), Constants.SWITCH_STYLE_HASH, new ObjectSwitchCallback() {
            public void processCase(Object mkey, Label end) {
                String key = mfMap.get(mkey);
                PropertyDescriptor pd = (PropertyDescriptor) getters.get(key);
                MethodInfo method = ReflectUtils.getMethodInfo(pd.getReadMethod());
                e.invoke(method);
                e.box(method.getSignature().getReturnType());
                
                Field field = getField(type, pd.getName());
                java.lang.reflect.Type fieldType = field.getGenericType();
                Local var = e.make_local();
                e.store_local(var);
                elementGetProcessor.process(fieldType, var);
                e.load_local(var);
                
                e.return_value();
            }

            public void processDefault() {
                e.aconst_null();
                e.return_value();
            }
        });
        e.end_method();
    }
    
    private void generatePut(final Class type, final Map setters, final Map<String, String> mfMap, final Map<String, String> fmMap) {
        final CodeEmitter e = begin_method(Constants.ACC_PUBLIC, BEAN_MAP_PUT, null);
        e.load_arg(0);
        e.checkcast(Type.getType(type));
        e.load_arg(1);
        e.checkcast(Constants.TYPE_STRING);
        
        final ElementPutProcessor elementPutProcessor = new ElementPutProcessor(e);
        
        EmitUtils.string_switch(e, getNames(setters, fmMap), Constants.SWITCH_STYLE_HASH, new ObjectSwitchCallback() {
            public void processCase(Object mkey, Label end) {
                String key = mfMap.get(mkey);
                PropertyDescriptor pd = (PropertyDescriptor) setters.get(key);
                if (pd.getReadMethod() == null) {
                    e.aconst_null();
                } else {
                    MethodInfo read = ReflectUtils.getMethodInfo(pd.getReadMethod());
                    e.dup();
                    e.invoke(read);
                    e.box(read.getSignature().getReturnType());
                }
                e.swap(); // move old value behind bean
                e.load_arg(2); // new value
                
                //中间转换，bsondbo --> java bean
                Field field = getField(type, pd.getName());
                java.lang.reflect.Type fieldType = field.getGenericType();
                Local var = e.make_local();
                e.store_local(var);
                elementPutProcessor.process(fieldType, var);
                e.load_local(var);
                
                MethodInfo write = ReflectUtils.getMethodInfo(pd.getWriteMethod());
                e.unbox(write.getSignature().getArgumentTypes()[0]);
                e.invoke(write);
                e.return_value();
            }

            public void processDefault() {
                // fall-through
            }
        });
        e.aconst_null();
        e.return_value();
        e.end_method();
    }
    
    private void generateKeySet(String[] allNames) {
        // static initializer
        declare_field(Constants.ACC_STATIC | Constants.ACC_PRIVATE, "keys", FIXED_KEY_SET, null);
        CodeEmitter e = begin_static();
        e.new_instance(FIXED_KEY_SET);
        e.dup();
        EmitUtils.push_array(e, allNames);
        e.invoke_constructor(FIXED_KEY_SET, CSTRUCT_STRING_ARRAY);
        e.putfield("keys");
        e.return_value();
        e.end_method();
        // keySet
        e = begin_method(Constants.ACC_PUBLIC, KEY_SET, null);
        e.load_this();
        e.getfield("keys");
        e.return_value();
        e.end_method();
    }
    
    private void generateGetPropertyType(final Map allProps, String[] allNames, final Map<String, String> mfMap) {
        final CodeEmitter e = begin_method(Constants.ACC_PUBLIC, GET_PROPERTY_TYPE, null);
        e.load_arg(0);
        EmitUtils.string_switch(e, allNames, Constants.SWITCH_STYLE_HASH, new ObjectSwitchCallback() {
            public void processCase(Object mkey, Label end) {
                String key = mfMap.get(mkey);
                PropertyDescriptor pd = (PropertyDescriptor) allProps.get(key);
                EmitUtils.load_class(e, Type.getType(pd.getPropertyType()));
                e.return_value();
            }

            public void processDefault() {
                e.aconst_null();
                e.return_value();
            }
        });
        e.end_method();
    }
    
}

