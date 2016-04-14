package org.whb;

import org.apache.commons.collections.list.GrowthList;
import org.whb.raffle.Stuff;

public class Person {
    
    private final String name;
    
    private int num;  //这个将被findbugs发现
    
    private Stuff stuff;

    public Person(String name) {
        this.name = name;
        new GrowthList();
    }

    public String getName() {
        return name;
    }

    public Stuff getStuff() {
        return stuff;
    }

    public void setStuff(Stuff stuff) {
        this.stuff = stuff;
    }
    
}
