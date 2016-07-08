package org.whb.springmvc.controller.editor;

import org.springframework.beans.propertyeditors.PropertiesEditor;

public class NonnegativeIntegerEditor extends PropertiesEditor {

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (text == null || text.trim().equals("")) {
            text = "0";
        }
        int value = Integer.parseInt(text.trim());
        if(value < 0){
            throw new IllegalArgumentException(text + " is not 0 or positive integer. ");
        }
        setValue(value);
    }

}
