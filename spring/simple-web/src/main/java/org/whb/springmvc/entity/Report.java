package org.whb.springmvc.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * 注解 @XmlRootElement 定义xml的根节点
 * 注解 @XmlAttribute 为节点添加attribute
 * 注解 @XmlElement 为节点添加子Element
 * 注解 @XmlTransient 用来忽视某个映射关系
 * 注解 @XmlType 用来指定排序，否则默认按首字母排序
 * getXXX方法默认带有@XmlElement，所以在bean的属性上添加@XmlElement会产生冲突
 * 
 * @author whb
 *
 */
@XmlRootElement(name = "record")
@XmlType(propOrder = { "id", "name", "type", "sales", "date"})
public class Report implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(name = "id")
	private int id;
	
//    @XmlElement(name = "sales")
	private BigDecimal sales;
	
//    @XmlElement(name = "type")
	private int type;
	
	private String name;
	
	private Date date;
	
	@XmlAttribute(name = "date")
    public String getFormatDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

	@XmlTransient
	public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BigDecimal getSales() {
        return sales;
    }

    public void setSales(BigDecimal sales) {
        this.sales = sales;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Report [id=" + id + ", sales=" + sales + ", type=" + type + ", name=" + name + ", date=" + date + "]";
    }
}