package org.whb.springmvc.repository.domain;

import java.io.Serializable;

public class Author implements Serializable{

    private static final long serialVersionUID = 1L;

    private int id;
    
    private String name;
    
    private String email;
    
    private int delFlag;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(int delFlag) {
        this.delFlag = delFlag;
    }

    @Override
    public String toString() {
        return "Author [id=" + id + ", name=" + name + ", email=" + email + ", delFlag=" + delFlag + "]";
    }

}
