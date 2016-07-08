package org.whb.springmvc.entity;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * 注解@JsonView是jackson json中的一个注解，Spring webmvc也支持这个注解，
 * 可以用来过滤序列化对象的字段属性，实现有选择的序列化对象。
 * @author 
 *
 */
public class Book implements Serializable {

    private static final long serialVersionUID = 1L;

    public static interface Summary {}  //用于标记属性，以便有选择的序列化对象
    public static interface SummaryWithDetail extends Summary {}

    @JsonView(Summary.class)
    private long id;

    @JsonView(Summary.class)
    private String name;
    
    @JsonView(Summary.class)
    private String author;
    
    @JsonView(SummaryWithDetail.class)
    private double price;
    
    @JsonView(SummaryWithDetail.class)
    private boolean canBorrow;
    
    private String note;

    public Book() {
        super();
    }
    
    public Book(long id, String name, String author, double price, boolean canBorrow, String note) {
        super();
        this.id = id;
        this.name = name;
        this.author = author;
        this.price = price;
        this.canBorrow = canBorrow;
        this.note = note;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isCanBorrow() {
        return canBorrow;
    }

    public void setCanBorrow(boolean canBorrow) {
        this.canBorrow = canBorrow;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "Book [id=" + id + ", name=" + name + ", author=" + author + ", price=" + price + ", canBorrow="
                + canBorrow + ", note=" + note + "]";
    }
    
}
