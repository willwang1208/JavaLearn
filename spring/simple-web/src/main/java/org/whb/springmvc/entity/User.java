package org.whb.springmvc.entity;

import java.io.Serializable;
import java.util.Arrays;

public class User implements Serializable{

    private static final long serialVersionUID = 1L;

    int id;
	
	String name;
	
	String password;
	
	String remark;
	
	String[] friends;
	
	double height;

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

	public String[] getFriends() {
		return friends;
	}

	public void setFriends(String[] friends) {
		this.friends = friends;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

    @Override
    public String toString() {
        return "User [id=" + id + ", name=" + name + ", password=" + password + ", remark=" + remark + ", friends="
                + Arrays.toString(friends) + ", height=" + height + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (id != user.getId()) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
