package com.caresilabs.mahappdev.assignment2;

/**
 * Created by Simon on 8/31/2016.
 */
public class Instructions {
    private String whatToDo;
    private String content;

    public Instructions(String whatToDo, String content) {
        this.whatToDo = whatToDo;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getWhatToDo() {
        return whatToDo;
    }

    public void setWhatToDo(String whatToDo) {
        this.whatToDo = whatToDo;
    }
}
