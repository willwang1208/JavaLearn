package org.whb.common.util;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class SizeLimitedMap<K extends Comparable<K>, V> extends HashMap<K, V> {

    private static final long serialVersionUID = 1L;
    
    private static final int DEFAULT_INITIAL_LIMIT = 0;  //<=0表示没限制
    
    private static final boolean DEFAULT_INITIAL_ELIMINATED_TYPE = true;  //true表示排除小的
    
    protected int limit;
    
    protected boolean minEliminated;
    
    protected Comparator<V> comparator;
    
    public SizeLimitedMap() {
        this(DEFAULT_INITIAL_LIMIT, DEFAULT_INITIAL_ELIMINATED_TYPE, null);
    }

    public SizeLimitedMap(int limit) {
        this(limit, DEFAULT_INITIAL_ELIMINATED_TYPE, null);
    }
    
    public SizeLimitedMap(int limit, boolean minEliminated) {
        this(limit, minEliminated, null);
    }
    
    public SizeLimitedMap(int limit, Comparator<V> comparator) {
        this(limit, DEFAULT_INITIAL_ELIMINATED_TYPE, comparator);
    }
    
    public SizeLimitedMap(int limit, boolean minEliminated, Comparator<V> comparator) {
        super();
        this.limit = limit;
        this.minEliminated = minEliminated;
        this.comparator = comparator;
    }

    @Override
    public V put(K key, V value) {
        while (true) {
            Map.Entry<K, V> eliminatedEntry = getEliminatedEntry();
            if (eliminatedEntry != null) {
                this.remove(eliminatedEntry.getKey());
            }else{
                break;
            }
        }
        return super.put(key, value);
    }
    
    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        if(m != null){
            super.putAll(m);
        }
    }
    
    public SizeLimitedMap<K, V> merge(Map<? extends K, ? extends V> m){
        putAll(m);
        return this;
    }

    public Map.Entry<K,V> getEliminatedEntry(){
        Map.Entry<K,V> eliminatedEntry = null;
        if(limit > 0 && this.size() >= limit){
            for(Map.Entry<K,V> entry: this.entrySet()){
                if(eliminatedEntry == null){
                    eliminatedEntry = entry;
                }else{
                    int c = valueCompare(entry.getValue(), eliminatedEntry.getValue());
                    if(c == 0){
                        c = keyCompare(entry.getKey(), eliminatedEntry.getKey());
                    }
                    if(c > 0){
                        eliminatedEntry = entry;
                    }
                }
            }
        }
        return eliminatedEntry;
    }
    
    /**
     * 
     * @param k1
     * @param k2
     * @return >0时eliminated=k1
     */
    protected int keyCompare(K k1, K k2){
        if(minEliminated){
            return -k1.compareTo(k2);
        }else{
            return k1.compareTo(k2);
        }
    }
    
    /**
     * 
     * @param v1
     * @param v2
     * @return >0时eliminated=v1
     */
    protected int valueCompare(V v1, V v2){
        if(comparator == null){
            return 0;
        }else{
            return comparator.compare(v1, v2);
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public boolean isMinEliminated() {
        return minEliminated;
    }

    public void setMinEliminated(boolean minEliminated) {
        this.minEliminated = minEliminated;
    }

    public Comparator<V> getComparator() {
        return comparator;
    }

    public void setComparator(Comparator<V> comparator) {
        this.comparator = comparator;
    }

}
