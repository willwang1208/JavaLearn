package org.whb.springmvc.entity;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.springframework.format.annotation.DateTimeFormat;

public class Dialog {

	int id;
	
	String name;
	
	String password;
	
	String remark;
	
	String[] friends;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateTime;
	
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

}
