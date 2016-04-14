package org.whb.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DailyAttribute<V> extends SizeLimitedMap<String, V> {

    private static final long serialVersionUID = 1L;
    
    private static final String patten = "yyyy-MM-dd";
    
    public DailyAttribute() {
        super();
    }

    public DailyAttribute(int limit, boolean minEliminated) {
        super(limit, minEliminated);
    }

    public DailyAttribute(int limit) {
        super(limit);
    }

    public V setValue(V value){
        return setValue(new Date(), value);
    }
    
    public V setValue(Date date, V value){
        SimpleDateFormat format = new SimpleDateFormat(patten);
        return setValue(format.format(date), value);
    }
    
    public V setValue(String date, V value){
        return put(date, value);
    }
    
    public V getValue(){
        return getValue(new Date());
    }
    
    public V getValue(Date date){
        SimpleDateFormat format = new SimpleDateFormat(patten);
        return getValue(format.format(date));
    }
    
    public V getValue(String date){
        return get(date);
    }
    
    public String getKey(){
        return getKey(new Date());
    }
    
    public String getKey(Date date){
        SimpleDateFormat format = new SimpleDateFormat(patten);
        return format.format(date);
    }
}
