package org.whb.springmvc.controller.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.whb.springmvc.entity.User;

public class UserValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        //just validate the User instances
        return User.class.isAssignableFrom(clazz);
    }
 
    @Override
    public void validate(Object target, Errors errors) {
        validatePage1Form(target, errors);
        validatePage2Form(target, errors);
        validatePage3Form(target, errors);
    }
    
    //validate page 1, userName
    public void validatePage1Form(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "userName",
                "required.userName", "Field name is required.");
    }
 
    //validate page 2, password
    public void validatePage2Form(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password",
            "required.password", "Field name is required.");
    }
 
    //validate page 3, remark
    public void validatePage3Form(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "remark",
            "required.remark", "Field name is required.");
    }

}
