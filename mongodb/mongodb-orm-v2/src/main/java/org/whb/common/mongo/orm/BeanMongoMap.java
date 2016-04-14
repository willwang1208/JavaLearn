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
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.cglib.core.AbstractClassGenerator;
import net.sf.cglib.core.KeyFactory;
import net.sf.cglib.core.ReflectUtils;

import org.objectweb.asm.ClassVisitor;

import com.mongodb.BasicDBObject;

/**
 * 基于net.sf.cglib.beans.BeanMap源码修改，使其支持com.mongodb.BasicDBObject
 * 
 */
abstract public class BeanMongoMap implements Map {
    /**
     * Limit the properties reflected in the key set of the map
     * to readable properties.
     * @see BeanMongoMap.Generator#setRequire
     */
    public static final int REQUIRE_GETTER = 1;

    /**
     * Limit the properties reflected in the key set of the map
     * to writable properties.
     * @see BeanMongoMap.Generator#setRequire
     */
    public static final int REQUIRE_SETTER = 2;
    
    private transient Set<Map.Entry> entrySet = null;
    
    /**
     * Helper method to create a new <code>BeanMongoMap</code>.  For finer
     * control over the generated instance, use a new instance of
     * <code>BeanMongoMap.Generator</code> instead of this static method.
     * @param bean the JavaBean underlying the map
     * @return a new <code>BeanMongoMap</code> instance
     */
    public static BeanMongoMap create(Object bean) {
        Generator gen = new Generator();
        gen.setBean(bean);
        return gen.create();
    }

    public static class Generator extends AbstractClassGenerator {
        private static final Source SOURCE = new Source(BeanMongoMap.class.getName());

        private static final BeanMongoMapKey KEY_FACTORY =
          (BeanMongoMapKey)KeyFactory.create(BeanMongoMapKey.class, KeyFactory.CLASS_BY_NAME);

        interface BeanMongoMapKey {
            public Object newInstance(Class type, int require);
        }
        
        private Object bean;
        private Class beanClass;
        private int require;
        
        public Generator() {
            super(SOURCE);
        }

        /**
         * Set the bean that the generated map should reflect. The bean may be swapped
         * out for another bean of the same type using {@link #setBean}.
         * Calling this method overrides any value previously set using {@link #setBeanClass}.
         * You must call either this method or {@link #setBeanClass} before {@link #create}.
         * @param bean the initial bean
         */
        public void setBean(Object bean) {
            this.bean = bean;
            if (bean != null)
                beanClass = bean.getClass();
        }

        /**
         * Set the class of the bean that the generated map should support.
         * You must call either this method or {@link #setBeanClass} before {@link #create}.
         * @param beanClass the class of the bean
         */
        public void setBeanClass(Class beanClass) {
            this.beanClass = beanClass;
        }

        /**
         * Limit the properties reflected by the generated map.
         * @param require any combination of {@link #REQUIRE_GETTER} and
         * {@link #REQUIRE_SETTER}; default is zero (any property allowed)
         */
        public void setRequire(int require) {
            this.require = require;
        }

        protected ClassLoader getDefaultClassLoader() {
            return beanClass.getClassLoader();
        }

        /**
         * Create a new instance of the <code>BeanMongoMap</code>. An existing
         * generated class will be reused if possible.
         */
        public BeanMongoMap create() {
            if (beanClass == null)
                throw new IllegalArgumentException("Class of bean unknown");
            setNamePrefix(beanClass.getName());
            return (BeanMongoMap)super.create(KEY_FACTORY.newInstance(beanClass, require));
        }

        public void generateClass(ClassVisitor v) throws Exception {
            new BeanMongoMapEmitter(v, getClassName(), beanClass, require);
        }

        protected Object firstInstance(Class type) {
            return ((BeanMongoMap)ReflectUtils.newInstance(type)).newInstance(bean);
        }

        protected Object nextInstance(Object instance) {
            return ((BeanMongoMap)instance).newInstance(bean);
        }
    }

    /**
     * Create a new <code>BeanMongoMap</code> instance using the specified bean.
     * This is faster than using the {@link #create} static method.
     * @param bean the JavaBean underlying the map
     * @return a new <code>BeanMongoMap</code> instance
     */
    abstract public BeanMongoMap newInstance(Object bean);
    
    abstract public Object createBean();

    /**
     * Get the type of a property.
     * @param name the name of the JavaBean property
     * @return the type of the property, or null if the property does not exist
     */
    abstract public Class getPropertyType(String name);

    protected Object bean;

    protected BeanMongoMap() {
    }

    protected BeanMongoMap(Object bean) {
        setBean(bean);
    }

    public Object get(Object key) {
        return get(bean, key);
    }

    public Object put(Object key, Object value) {
        return put(bean, key, value);
    }

    /**
     * Get the property of a bean. This allows a <code>BeanMongoMap</code>
     * to be used statically for multiple beans--the bean instance tied to the
     * map is ignored and the bean passed to this method is used instead.
     * @param bean the bean to query; must be compatible with the type of
     * this <code>BeanMongoMap</code>
     * @param key must be a String
     * @return the current value, or null if there is no matching property
     */
    abstract public Object get(Object bean, Object key);

    /**
     * Set the property of a bean. This allows a <code>BeanMongoMap</code>
     * to be used statically for multiple beans--the bean instance tied to the
     * map is ignored and the bean passed to this method is used instead.
     * @param key must be a String
     * @return the old value, if there was one, or null
     */
    abstract public Object put(Object bean, Object key, Object value);

    /**
     * Change the underlying bean this map should use.
     * @param bean the new JavaBean
     * @see #getBean
     */
    public void setBean(Object bean) {
        this.bean = bean;
    }

    /**
     * Return the bean currently in use by this map.
     * @return the current JavaBean
     * @see #setBean
     */
    public Object getBean() {
        return bean;
    }

    public void clear() {
        throw new UnsupportedOperationException();
    }

    public boolean containsKey(Object key) {
        return keySet().contains(key);
    }

    public boolean containsValue(Object value) {
        for (Iterator it = keySet().iterator(); it.hasNext();) {
            Object v = get(it.next());
            if (((value == null) && (v == null)) || value.equals(v))
                return true;
        }
        return false;
    }

    public int size() {
        return keySet().size();
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public Object remove(Object key) {
        throw new UnsupportedOperationException();
    }

    public void putAll(Map t) {
        for (Iterator it = t.keySet().iterator(); it.hasNext();) {
            Object key = it.next();
            put(key, t.get(key));
        }
    }

    public boolean equals(Object o) {
        if (o == null || !(o instanceof Map)) {
            return false;
        }
        Map other = (Map)o;
        if (size() != other.size()) {
            return false;
        }
        for (Iterator it = keySet().iterator(); it.hasNext();) {
            Object key = it.next();
            if (!other.containsKey(key)) {
                return false;
            }
            Object v1 = get(key);
            Object v2 = other.get(key);
            if (!((v1 == null) ? v2 == null : v1.equals(v2))) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int code = 0;
        for (Iterator it = keySet().iterator(); it.hasNext();) {
            Object key = it.next();
            Object value = get(key);
            code += ((key == null) ? 0 : key.hashCode()) ^
                ((value == null) ? 0 : value.hashCode());
        }
        return code;
    }

    // TODO: optimize
    public Set entrySet() {
        
//        HashMap copy = new HashMap();
//        for (Iterator it = keySet().iterator(); it.hasNext();) {
//            Object key = it.next();
//            copy.put(key, get(key));
//        }
//        return Collections.unmodifiableMap(copy).entrySet();
        
        Set<Map.Entry> es = entrySet;
        return es != null ? es : (entrySet = new EntrySet());
    }

    public Collection values() {
        Set keys = keySet();
        List values = new ArrayList(keys.size());
        for (Iterator it = keys.iterator(); it.hasNext();) {
            values.add(get(it.next()));
        }
        return Collections.unmodifiableCollection(values);
    }

    /*
     * @see java.util.AbstractMap#toString
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append('{');
        for (Iterator it = keySet().iterator(); it.hasNext();) {
            Object key = it.next();
            sb.append(key);
            sb.append('=');
            sb.append(get(key));
            if (it.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append('}');
        return sb.toString();
    }
    
    public BasicDBObject toBson() {
        BasicDBObject bson = new BasicDBObject();
        Set keys = keySet();
        for (Iterator it = keys.iterator(); it.hasNext();) {
             Object key = it.next();
             bson.put(key.toString(), get(key));
        }
        return bson;
    }
    
    private final class Entry implements Map.Entry {
        
        Object key;
        
        public Entry(Object key) {
            super();
            this.key = key;
        }

        @Override
        public Object getKey() {
            return key;
        }

        @Override
        public Object getValue() {
            return BeanMongoMap.this.get(key);
        }

        @Override
        public Object setValue(Object value) {
            return BeanMongoMap.this.put(key, value);
        }
        
    }
    
    private final class EntryIterator implements Iterator {

        Iterator keyIterator;
        
        public EntryIterator(Iterator keyIterator) {
            super();
            this.keyIterator = keyIterator;
        }

        @Override
        public boolean hasNext() {
            return keyIterator.hasNext();
        }

        @Override
        public Object next() {
            return new Entry(keyIterator.next());
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
        
    }
    
    private final class EntrySet extends AbstractSet<Map.Entry> {

        @Override
        public Iterator<java.util.Map.Entry> iterator() {
            return new EntryIterator(keySet().iterator());
        }

        @Override
        public int size() {
            return BeanMongoMap.this.size();
        }
        
    }
}
