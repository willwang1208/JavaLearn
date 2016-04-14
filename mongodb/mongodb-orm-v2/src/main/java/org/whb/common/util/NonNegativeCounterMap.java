package org.whb.common.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class NonNegativeCounterMap<K> extends HashMap<K, Integer> {
    
    private static final long serialVersionUID = 1L;

    public NonNegativeCounterMap() {}
    
    public NonNegativeCounterMap(Map<K, Integer> src) {
        if (src != null) {
            putAll(src);
        }
    }
    
    public int add(K key, int count) {
        Integer value = super.get(key);
        if (value == null) {
            value = 0;
        }
        if (value + count < 0 ) {
            throw new RuntimeException(key.toString() + " current " + value + " need " + count);
        }
        value += count;
        if (value == 0) {
            super.remove(key);
        }
        else {
            super.put(key, value);
        }
        return value;
    }
    public int add(K key, int count,boolean isNegative) {
        Integer value = super.get(key);
        if (value == null) {
            value = 0;
        }
        if (value + count < 0 && isNegative) {
            throw new RuntimeException(key.toString() + " current " + value + " need " + count);
        }
        value += count;
        if (value == 0) {
            super.remove(key);
        }
        else {
            super.put(key, value);
        }
        return value;
    }
    
    public void add(NonNegativeCounterMap<K> m,boolean isNegative) {
        if (m != null && m.size() > 0) {
            for (Entry<K, Integer> pair: m.entrySet()) {
                add(pair.getKey(), pair.getValue(),isNegative);
            }
        }
    }
    public void add(NonNegativeCounterMap<K> m) {
        if (m != null && m.size() > 0) {
            for (Entry<K, Integer> pair: m.entrySet()) {
                add(pair.getKey(), pair.getValue());
            }
        }
    }
    
    public void subtract(NonNegativeCounterMap<K> m) {
        if (m != null && m.size() > 0) {
            for (Entry<K, Integer> pair: m.entrySet()) {
                add(pair.getKey(), -pair.getValue());
            }
        }
    }
    
    public int increase(K key) {
        return add(key, 1);
    }
    
    @Override
    public Integer get(Object key) {
        Integer value = super.get(key);
        if (value == null) return 0;
        return value;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        for (Entry<K, Integer> pair: entrySet()) {
            sb.append(pair.getKey() + ": " + pair.getValue() + ", ");
        }
        sb.append("}");
        return sb.toString();
    }
    
    public DBObject toDBObject() {
        DBObject dbo = new BasicDBObject();
        for (Entry<K, Integer> pair: this.entrySet()) {
            dbo.put(pair.getKey().toString(), pair.getValue());
        }
        return dbo;
    }
    
    public static NonNegativeCounterMap<Integer> fromString(String s) {
        NonNegativeCounterMap<Integer> result = new NonNegativeCounterMap<Integer>();
        String pairs[] = s.split(";");
        for (String pair: pairs) {
            String elements[] = pair.split(",");
            if (elements.length == 2) {
                result.put(Integer.parseInt(elements[0]), Integer.parseInt(elements[1]));
            }
        }
        return result;
    }
    
}
