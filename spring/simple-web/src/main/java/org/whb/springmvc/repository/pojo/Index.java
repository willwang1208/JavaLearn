package org.whb.springmvc.repository.pojo;

public class Index {
    
    String indexName;
    
    String columnName;
    
    int position;  //字段在联合索引中的顺序位
    
    int type;
    
    boolean nonUnique = true;   //非唯一
    
    boolean asc = true;  //索引顺序

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isNonUnique() {
        return nonUnique;
    }

    public void setNonUnique(boolean nonUnique) {
        this.nonUnique = nonUnique;
    }

    public boolean isAsc() {
        return asc;
    }

    public void setAsc(boolean asc) {
        this.asc = asc;
    }

    @Override
    public String toString() {
        return "Index [indexName=" + indexName + ", columnName=" + columnName + ", position=" + position + ", type="
                + type + ", nonUnique=" + nonUnique + ", asc=" + asc + "]";
    }
    
}
