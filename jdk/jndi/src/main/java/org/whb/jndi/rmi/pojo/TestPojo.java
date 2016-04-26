package org.whb.jndi.rmi.pojo;

import java.io.Serializable;
import java.util.List;

public class TestPojo implements Serializable{

    private static final long serialVersionUID = 1L;

    int age;
    
    List<String> msgs;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public List<String> getMsgs() {
        return msgs;
    }

    public void setMsgs(List<String> msgs) {
        this.msgs = msgs;
    }

    @Override
    public String toString() {
        return "TestPojo [age=" + age + ", msgs=" + msgs + "]";
    }
    
}
