package org.whb.groovy.springmvc.script

import org.whb.springmvc.service.IHelloWorldService4Groovy;

/**
 * Groovy语言实现的脚本，可以动态修改脚本内容，不用重启即可生效
 * 
 */
public class GS_HelloWorld implements IHelloWorldService4Groovy {

    String hello() {
        return "Hello. Welcome to groovy in spring. This code can be modified on runtime";
    }
}
