package com.caresilabs.mahappdev.assignment2;

import android.content.res.Resources;

/**
 * Created by Simon on 8/31/2016.
 */
public class Controller {
    private Instructions[] instructions = new Instructions[3];
    private int index = 0;
    private ViewerFragment ui;

    public Controller(ViewerFragment ui) {
        this.ui = ui;
        initResources();
    }

    private void initResources() {
        Resources res = ui.getResources();

        instructions[0] = new Instructions(res.getString(R.string.what_to_do), res.getString(R.string.content));
        instructions[1] = new Instructions(res.getString(R.string.what_to_do2), res.getString(R.string.content2));
        instructions[2] = new Instructions(res.getString(R.string.what_to_do3), res.getString(R.string.content3));
    }

    public void previousClick() {
        index--;
        if (index < 0)
            index = instructions.length - 1;

        ui.setWhatToDo(instructions[index].getWhatToDo());
        ui.setContent(instructions[index].getContent());
    }

    public void nextClick() {
        index++;
        if (index >= instructions.length)
            index = 0;

        ui.setWhatToDo(instructions[index].getWhatToDo());
        ui.setContent(instructions[index].getContent());
    }
}
