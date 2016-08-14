package org.whb.springmvc.repository.pojo;

public class Column {

    String name;
    
    int type;
    
    int length;
    
    int scale;  //浮点数精度
    
    boolean nullable = true;  //允许为空
    
    String defaultValue;
    
    String remark;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "Column [name=" + name + ", type=" + type + ", length=" + length + ", scale=" + scale + ", nullable="
                + nullable + ", defaultValue=" + defaultValue + ", remark=" + remark + "]";
    }

}
