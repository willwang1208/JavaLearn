package org.whb.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.types.ObjectId;
import org.whb.common.mongo.orm.IMongoBean;
import org.whb.common.mongo.orm.MongoCollection;
import org.whb.common.mongo.orm.MongoElement;
import org.whb.common.util.DailyAttribute;
import org.whb.common.util.SizeLimitedMap;

@MongoCollection(name = "test_data")
public class TestData implements IMongoBean {
    
    @MongoElement(name = "_id")
    private ObjectId id;    //主键，如果是ObjectId，新增时为空，其他自定义
    
    @MongoElement(name = "a")
    private DailyAttribute<Object> aaa;
    
    @MongoElement(name = "b")
    private DailyAttribute<Integer> bbb;
    
    @MongoElement(name = "c")
    private Map<String, Map<Boolean, List<Long>>> ccc;

    @MongoElement(name = "d")
    private HashMap<Integer, Object> ddd;

    @MongoElement(name = "e")
    private HashSet<HashSet<String>> eee;
    
    @MongoElement(name = "g")
    private List<Map<String, Sub>> ggg;
    
    @MongoElement(name = "h")
    private int hhh;
    
    @MongoElement(name = "i")
    private long iii;
    
    @MongoElement(name = "j")
    private String jjj;
    
    @MongoElement(name = "k")
    private double kkk;
    
    @MongoElement(name = "l")
    private boolean lll;
    
//    @MongoElement(name = "m")
//    private String[] mmm;
//    
//    @MongoElement(name = "n")
//    private Sub[] nnn;
    
    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public DailyAttribute<Object> getAaa() {
        return aaa;
    }

    public void setAaa(DailyAttribute<Object> aaa) {
        this.aaa = aaa;
    }

    public DailyAttribute<Integer> getBbb() {
        return bbb;
    }

    public void setBbb(DailyAttribute<Integer> bbb) {
        this.bbb = bbb;
    }

    public Map<String, Map<Boolean, List<Long>>> getCcc() {
        return ccc;
    }

    public void setCcc(Map<String, Map<Boolean, List<Long>>> ccc) {
        this.ccc = ccc;
    }

    public HashMap<Integer, Object> getDdd() {
        return ddd;
    }

    public void setDdd(HashMap<Integer, Object> ddd) {
        this.ddd = ddd;
    }

    public HashSet<HashSet<String>> getEee() {
        return eee;
    }

    public void setEee(HashSet<HashSet<String>> eee) {
        this.eee = eee;
    }

    public int getHhh() {
        return hhh;
    }

    public void setHhh(int hhh) {
        this.hhh = hhh;
    }

    public long getIii() {
        return iii;
    }

    public void setIii(long iii) {
        this.iii = iii;
    }

    public String getJjj() {
        return jjj;
    }

    public void setJjj(String jjj) {
        this.jjj = jjj;
    }

    public double getKkk() {
        return kkk;
    }

    public void setKkk(double kkk) {
        this.kkk = kkk;
    }

    public boolean isLll() {
        return lll;
    }

    public void setLll(boolean lll) {
        this.lll = lll;
    }

//    public String[] getMmm() {
//        return mmm;
//    }
//
//    public void setMmm(String[] mmm) {
//        this.mmm = mmm;
//    }
//
//    public Sub[] getNnn() {
//        return nnn;
//    }
//
//    public void setNnn(Sub[] nnn) {
//        this.nnn = nnn;
//    }

    public List<Map<String, Sub>> getGgg() {
        return ggg;
    }

    public void setGgg(List<Map<String, Sub>> ggg) {
        this.ggg = ggg;
    }

    public static class Sub implements IMongoBean{
        
        @MongoElement(name = "sa")
        private SizeLimitedMap<String, Set<String>> saaa;
        
        @MongoElement(name = "sb")
        private String sbbb;
        
        @MongoElement(name = "sc")
        private Long sccc;

        @MongoElement(name = "sd")
        private Integer sddd;

        @MongoElement(name = "se")
        private Boolean seee;
        
        @MongoElement(name = "sf")
        private Double sfff;

        public SizeLimitedMap<String, Set<String>> getSaaa() {
            return saaa;
        }

        public void setSaaa(SizeLimitedMap<String, Set<String>> saaa) {
            this.saaa = saaa;
        }

        public String getSbbb() {
            return sbbb;
        }

        public void setSbbb(String sbbb) {
            this.sbbb = sbbb;
        }

        public Long getSccc() {
            return sccc;
        }

        public void setSccc(Long sccc) {
            this.sccc = sccc;
        }

        public Integer getSddd() {
            return sddd;
        }

        public void setSddd(Integer sddd) {
            this.sddd = sddd;
        }

        public Boolean getSeee() {
            return seee;
        }

        public void setSeee(Boolean seee) {
            this.seee = seee;
        }

        public Double getSfff() {
            return sfff;
        }

        public void setSfff(Double sfff) {
            this.sfff = sfff;
        }

    }
    
    public static TestData getDemo(){
        DailyAttribute<Object> aaa = new DailyAttribute<>();
        aaa.setLimit(2);
        aaa.setValue("aaa_value");
        
        DailyAttribute<Integer> bbb = new DailyAttribute<Integer>();
        bbb.setLimit(3);
        bbb.setValue("2015-02-02", 1);
        bbb.setValue("2015-02-03", 1);
        
        Map<String, Map<Boolean, List<Long>>> ccc = new HashMap<String, Map<Boolean, List<Long>>>();
        Map<Boolean, List<Long>> s_ccc = new HashMap<Boolean, List<Long>>();
        List<Long> ss_ccc = new ArrayList<Long>();
        ccc.put("s_ccc", s_ccc);
        s_ccc.put(false, ss_ccc);
        ss_ccc.add(1L);
        ss_ccc.add(2L);
        
        
        HashMap<Integer, Object> ddd = new HashMap<Integer, Object>();
        ddd.put(1, 1);
        ddd.put(2, true);
        ddd.put(3, "ddd_v3");
        
        HashSet<HashSet<String>> eee = new HashSet<HashSet<String>>();
        HashSet<String> s_eee = new HashSet<String>();
        HashSet<String> s_eee2 = new HashSet<String>();
        eee.add(s_eee);
        eee.add(s_eee2);
        s_eee.add("vvvvvvvvvv");
        s_eee2.add("xxxxxxxxxxxxxxx");
        
        List<Map<String, Sub>> ggg = new ArrayList<Map<String, Sub>>();
        Map<String, Sub> s_ggg = new HashMap<String, Sub>();
        Sub sub = new Sub();
        sub.setSaaa(null);
        sub.setSbbb("sub_string");
        sub.setSccc(Long.valueOf(8888));
        sub.setSddd(Integer.valueOf(1));
        sub.setSeee(Boolean.valueOf(true));
        sub.setSfff(Double.valueOf(3));
        s_ggg.put("sub", sub);
        ggg.add(s_ggg);
        
        
        TestData data = new TestData();
        data.setAaa(aaa);
        data.setBbb(bbb);
        data.setCcc(ccc);
        data.setDdd(ddd);
        data.setEee(eee);
        data.setHhh(1000);
        data.setIii(9999999);
        data.setJjj("jjjstring");
        data.setKkk(9.762);
        data.setLll(false);
        data.setGgg(ggg);
        
        return data;
    }
}
