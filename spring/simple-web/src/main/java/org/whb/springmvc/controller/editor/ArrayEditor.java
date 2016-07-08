package org.whb.springmvc.controller.editor;

import java.beans.PropertyEditorSupport;

public class ArrayEditor extends PropertyEditorSupport {

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        super.setAsText(text);
    }

    @Override
    public String getAsText() {
        return super.getAsText();
    }

}
