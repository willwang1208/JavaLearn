package org.whb.springmvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/bind")
public class BinderController {
    //在SpringMVC中，bean中定义了Date，double等类型，如果没有做任何处理的话，日期以及double都无法绑定。
//    @InitBinder
//    protected void initBinder(WebDataBinder binder) {
//        binder.registerCustomEditor(Date.class,
//                new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"), true));
//        // binder.registerCustomEditor(int.class, new
//        // CustomNumberEditor(int.class, true));
//        binder.registerCustomEditor(int.class, new IntegerEditor());
//        // binder.registerCustomEditor(long.class, new
//        // CustomNumberEditor(long.class, true));
//        binder.registerCustomEditor(long.class, new LongEditor());
//        binder.registerCustomEditor(double.class, new DoubleEditor());
//        binder.registerCustomEditor(float.class, new FloatEditor());
//    }
//    
//    
//
//    @ExceptionHandler(Exception.class)
//    @ResponseStatus(HttpStatus.FORBIDDEN)
//    public String exceptionHandler(Exception e) throws Exception {
//        throw e;
//    }
}
